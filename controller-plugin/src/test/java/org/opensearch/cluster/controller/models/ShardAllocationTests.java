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
import java.util.Arrays;

public class ShardAllocationTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        ShardAllocation shardAllocation = new ShardAllocation();

        BytesReference bytesRef = XContentHelper.toXContent(shardAllocation, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        ShardAllocation deserializedShardAllocation = ShardAllocation.fromXContent(parser);
        assertEquals(shardAllocation, deserializedShardAllocation);
    }

    public void testSerializationWithAllFields() throws IOException {
        ShardAllocation shardAllocation = new ShardAllocation();
        shardAllocation.setShardId("shard-0");
        shardAllocation.setIndexName("my-index");
        shardAllocation.setIngestSUs(Arrays.asList("node1", "node2", "node3"));
        shardAllocation.setSearchSUs(Arrays.asList("node4", "node5"));
        shardAllocation.setAllocationTimestamp(System.currentTimeMillis());

        BytesReference bytesRef = XContentHelper.toXContent(shardAllocation, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        ShardAllocation deserializedShardAllocation = ShardAllocation.fromXContent(parser);
        assertEquals(shardAllocation, deserializedShardAllocation);
    }
}

