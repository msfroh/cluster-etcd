/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
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
import java.time.OffsetDateTime;

public class TaskMetadataTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        TaskMetadata taskMetadata = new TaskMetadata();

        BytesReference bytesRef = XContentHelper.toXContent(taskMetadata, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        TaskMetadata deserializedTaskMetadata = TaskMetadata.fromXContent(parser);
        assertEquals(taskMetadata, deserializedTaskMetadata);
    }

    public void testSerializationWithAllFields() throws IOException {
        TaskMetadata taskMetadata = new TaskMetadata();
        taskMetadata.setName("reindex-task");
        taskMetadata.setStatus("RUNNING");
        taskMetadata.setPriority(1);
        taskMetadata.setSchedule("0 0 * * *");
        taskMetadata.setInput("{\"source\":\"index1\",\"dest\":\"index2\"}");
        taskMetadata.setOutput("{\"documentsIndexed\":1000}");
        OffsetDateTime now = OffsetDateTime.now();
        taskMetadata.setLastUpdated(now);
        taskMetadata.setCreatedAt(now.minusHours(1));

        BytesReference bytesRef = XContentHelper.toXContent(taskMetadata, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        TaskMetadata deserializedTaskMetadata = TaskMetadata.fromXContent(parser);
        assertEquals(taskMetadata, deserializedTaskMetadata);
    }
}

