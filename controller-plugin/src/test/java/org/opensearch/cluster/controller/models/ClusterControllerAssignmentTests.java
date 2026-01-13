/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.models;

import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.core.common.bytes.BytesReference;
import org.opensearch.core.xcontent.DeprecationHandler;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentHelper;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.test.OpenSearchTestCase;

import java.io.IOException;

public class ClusterControllerAssignmentTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        ClusterControllerAssignment assignment = new ClusterControllerAssignment();

        BytesReference bytesRef = XContentHelper.toXContent(
            assignment,
            JsonXContent.jsonXContent.mediaType(),
            ToXContent.EMPTY_PARAMS,
            false
        );
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(
            NamedXContentRegistry.EMPTY,
            DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
            bytes
        );
        ClusterControllerAssignment deserializedAssignment = ClusterControllerAssignment.fromXContent(parser);
        assertEquals(assignment, deserializedAssignment);
    }

    public void testSerializationWithAllFields() throws IOException {
        ClusterControllerAssignment assignment = new ClusterControllerAssignment();
        assignment.setController("controller-1");
        assignment.setCluster("my-cluster");
        assignment.setTimestamp(System.currentTimeMillis());
        assignment.setLease("lease-12345");

        BytesReference bytesRef = XContentHelper.toXContent(
            assignment,
            JsonXContent.jsonXContent.mediaType(),
            ToXContent.EMPTY_PARAMS,
            false
        );
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(
            NamedXContentRegistry.EMPTY,
            DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
            bytes
        );
        ClusterControllerAssignment deserializedAssignment = ClusterControllerAssignment.fromXContent(parser);
        assertEquals(assignment, deserializedAssignment);
    }
}
