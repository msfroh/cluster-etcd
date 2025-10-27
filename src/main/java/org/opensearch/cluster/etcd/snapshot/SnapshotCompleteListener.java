/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.etcd.snapshot;

import org.opensearch.cluster.ClusterChangedEvent;
import org.opensearch.cluster.ClusterStateListener;
import org.opensearch.cluster.service.ClusterApplierService;

public class SnapshotCompleteListener implements ClusterStateListener {
    private final ClusterApplierService clusterApplierService;

    public SnapshotCompleteListener(ClusterApplierService clusterApplierService) {
        this.clusterApplierService = clusterApplierService;
    }

    @Override
    public void clusterChanged(ClusterChangedEvent event) {

    }
}
