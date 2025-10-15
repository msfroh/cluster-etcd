/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.etcd.snapshot;

import org.opensearch.action.ActionType;

public class NodeSnapshotAction extends ActionType<NodeSnapshotResponse> {
    public static final NodeSnapshotAction INSTANCE = new NodeSnapshotAction();
    public static final String NAME = "cluster:admin/etcd/snapshot";

    public NodeSnapshotAction() {
        super(NAME, NodeSnapshotResponse::new);
    }
}
