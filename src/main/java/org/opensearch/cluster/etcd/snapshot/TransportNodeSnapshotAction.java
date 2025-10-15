/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.etcd.snapshot;

import org.opensearch.Version;
import org.opensearch.action.support.ActionFilters;
import org.opensearch.action.support.HandledTransportAction;
import org.opensearch.cluster.ClusterState;
import org.opensearch.cluster.SnapshotsInProgress;
import org.opensearch.cluster.routing.ShardRouting;
import org.opensearch.cluster.service.ClusterService;
import org.opensearch.common.inject.Inject;
import org.opensearch.core.action.ActionListener;
import org.opensearch.core.index.shard.ShardId;
import org.opensearch.repositories.IndexId;
import org.opensearch.repositories.RepositoriesService;
import org.opensearch.repositories.Repository;
import org.opensearch.repositories.RepositoryData;
import org.opensearch.repositories.ShardGenerations;
import org.opensearch.snapshots.Snapshot;
import org.opensearch.snapshots.SnapshotId;
import org.opensearch.tasks.Task;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.transport.TransportService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static org.opensearch.repositories.blobstore.BlobStoreRepository.SHARD_PATH_TYPE;

public class TransportNodeSnapshotAction extends HandledTransportAction<NodeSnapshotRequest, NodeSnapshotResponse> {
    private final ClusterService clusterService;
    private final RepositoriesService repositoriesService;
    private final ThreadPool threadPool;

    @Inject
    public TransportNodeSnapshotAction(ClusterService clusterService, RepositoriesService repositoriesService, ThreadPool threadPool, TransportService transportService, ActionFilters actionFilters) {
        super(NodeSnapshotAction.NAME, transportService, actionFilters, NodeSnapshotRequest::new, ThreadPool.Names.SNAPSHOT);
        this.clusterService = clusterService;
        this.repositoriesService = repositoriesService;
        this.threadPool = threadPool;
    }

    @Override
    protected void doExecute(Task task, NodeSnapshotRequest request, ActionListener<NodeSnapshotResponse> listener) {
        repositoriesService.getRepositoryData(request.getRepository(), ActionListener.delegateFailure(listener, (l,r)-> {
            Function<ClusterState, ClusterState> startSnapshotFunction = cs -> startSnapshots(cs, request, r);
            clusterService.getClusterApplierService().updateClusterState("snapshot started", startSnapshotFunction,
                    (s, e) -> l.onFailure(e));
        }));
    }

    private ClusterState startSnapshots(ClusterState clusterState, NodeSnapshotRequest request, RepositoryData repoData) {

        SnapshotsInProgress snapshotsInProgress = clusterState.custom(SnapshotsInProgress.TYPE, SnapshotsInProgress.EMPTY);
        if (snapshotsInProgress.entries().isEmpty() == false) {
            throw new IllegalStateException("Cannot create snapshot, another snapshot is in progress");
        }
        Repository repository = repositoriesService.repository(request.getRepository());
        int pathType = clusterState.nodes().getMinNodeVersion().onOrAfter(Version.V_2_17_0)
                ? SHARD_PATH_TYPE.get(repository.getMetadata().settings()).getCode()
                : IndexId.DEFAULT_SHARD_PATH_TYPE;
        String localNodeId = clusterState.nodes().getLocalNodeId();

        ShardGenerations shardGenerations = repoData.shardGenerations();
        Map<ShardId, SnapshotsInProgress.ShardSnapshotStatus> shards = new HashMap<>();
        Map<String, IndexId> inflightIndexIds = new HashMap<>();
        for (ShardRouting shardRouting : clusterState.routingTable().allShards()) {
            if (shardRouting.currentNodeId().equals(localNodeId) && shardRouting.primary() && shardRouting.started()) {
                IndexId indexId;
                if (inflightIndexIds.containsKey(shardRouting.getIndexName())) {
                    indexId = inflightIndexIds.get(shardRouting.getIndexName());
                } else {
                    indexId = repoData.resolveNewIndices(List.of(shardRouting.getIndexName()), inflightIndexIds, pathType).getFirst();
                    inflightIndexIds.put(shardRouting.getIndexName(), indexId);
                }
                String shardGen = shardGenerations.getShardGen(indexId, shardRouting.id());
                shards.put(shardRouting.shardId(), new SnapshotsInProgress.ShardSnapshotStatus(localNodeId, shardGen));
            }
        }
        List<IndexId> indexIds = new ArrayList<>(inflightIndexIds.values());

        SnapshotId snapshotId = new SnapshotId(localNodeId, UUID.randomUUID().toString());
        Snapshot snapshot = new Snapshot(request.getRepository(), snapshotId);
        SnapshotsInProgress.Entry entry = SnapshotsInProgress.startedEntry(snapshot, false, false, indexIds, Collections.emptyList(), threadPool.absoluteTimeInMillis(), repoData.getGenId(), shards, Collections.emptyMap(), Version.CURRENT, false);
        return ClusterState.builder(clusterState).putCustom(SnapshotsInProgress.TYPE, SnapshotsInProgress.of(List.of(entry))).build();
    }
}
