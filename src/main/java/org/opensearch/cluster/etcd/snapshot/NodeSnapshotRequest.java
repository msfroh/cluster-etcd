/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.etcd.snapshot;

import org.opensearch.action.ActionRequest;
import org.opensearch.action.ActionRequestValidationException;
import org.opensearch.core.common.io.stream.StreamInput;

import java.io.IOException;

public class NodeSnapshotRequest extends ActionRequest {
    private final String repository;
    private final String snapshotName;

    public NodeSnapshotRequest(String repository, String snapshotName) {
        this.repository = repository;
        this.snapshotName = snapshotName;
    }

    public NodeSnapshotRequest(StreamInput streamInput) throws IOException {
        super(streamInput);
        this.repository = streamInput.readString();
        this.snapshotName = streamInput.readString();
    }


    @Override
    public ActionRequestValidationException validate() {
        return null;
    }

    public String getRepository() {
        return repository;
    }

    public String getSnapshotName() {
        return snapshotName;
    }
}
