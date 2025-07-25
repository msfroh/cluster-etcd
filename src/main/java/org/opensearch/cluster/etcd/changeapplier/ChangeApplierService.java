/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.etcd.changeapplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.cluster.ClusterState;
import org.opensearch.cluster.node.DiscoveryNode;
import org.opensearch.cluster.node.DiscoveryNodes;
import org.opensearch.cluster.service.ClusterApplierService;

public class ChangeApplierService {
    private final Logger logger = LogManager.getLogger(getClass());
    private final ClusterApplierService clusterApplierService;

    public ChangeApplierService(ClusterApplierService clusterApplierService) {
        this.clusterApplierService = clusterApplierService;
    }

    public void applyNodeState(String source, NodeState nodeState) {
        clusterApplierService.onNewClusterState(source,
            () -> nodeState.buildClusterState(clusterApplierService.state()),
            this::logError);
    }

    public void removeNode(String source, DiscoveryNode localNode) {
        // Return to empty cluster state
        ClusterState clusterState = ClusterState.builder(ClusterState.EMPTY_STATE)
            .nodes(DiscoveryNodes.builder().localNodeId(localNode.getId()).add(localNode).build())
            .build();
        clusterApplierService.onNewClusterState(source, () -> clusterState, this::logError);
    }

    private void logError(String source, Exception e) {
        logger.error("Failed to update cluster state from {}", source, e);
    }
}
