/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.etcd.snapshot;

import org.opensearch.rest.BaseRestHandler;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.action.RestToXContentListener;
import org.opensearch.transport.client.node.NodeClient;

import java.io.IOException;
import java.util.List;

public class RestNodeSnapshotAction extends BaseRestHandler {
    @Override
    public String getName() {
        return "";
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
        String repository = request.param("repository");
        String snapshotId = request.param("snapshotId");
        NodeSnapshotRequest nodeSnapshotRequest = new NodeSnapshotRequest(repository, snapshotId);
        return channel -> client.execute(NodeSnapshotAction.INSTANCE, nodeSnapshotRequest, new RestToXContentListener<>(channel));
    }

    @Override
    public List<Route> routes() {
        return List.of(new Route(RestRequest.Method.POST, "/_node_snapshot/{repository}/{snapshotId}"));
    }
}
