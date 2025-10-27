/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.etcd.snapshot;

import org.opensearch.core.action.ActionResponse;
import org.opensearch.core.common.io.stream.StreamInput;
import org.opensearch.core.common.io.stream.StreamOutput;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;

import java.io.IOException;

public class NodeSnapshotResponse extends ActionResponse implements ToXContentObject {

    public NodeSnapshotResponse() {
        super();
    }

    public NodeSnapshotResponse(StreamInput streamInput) throws IOException {
        super(streamInput);
    }

    @Override
    public void writeTo(StreamOutput streamOutput) throws IOException {
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder xContentBuilder, Params params) throws IOException {
        return xContentBuilder.startObject().endObject();
    }
}
