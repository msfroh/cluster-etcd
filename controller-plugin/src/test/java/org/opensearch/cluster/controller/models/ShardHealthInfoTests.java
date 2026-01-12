/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.controller.models;

import org.opensearch.cluster.controller.enums.HealthState;
import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.core.common.bytes.BytesReference;
import org.opensearch.core.xcontent.DeprecationHandler;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentHelper;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.test.OpenSearchTestCase;

import java.io.IOException;

public class ShardHealthInfoTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        ShardHealthInfo shardHealthInfo = new ShardHealthInfo();

        BytesReference bytesRef = XContentHelper.toXContent(shardHealthInfo, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        ShardHealthInfo deserializedShardHealthInfo = ShardHealthInfo.fromXContent(parser);
        assertEquals(shardHealthInfo, deserializedShardHealthInfo);
    }

    public void testSerializationWithAllFields() throws IOException {
        ShardHealthInfo shardHealthInfo = new ShardHealthInfo();
        shardHealthInfo.setShardId(0);
        shardHealthInfo.setStatus(HealthState.GREEN);
        shardHealthInfo.setPrimaryActive(true);
        shardHealthInfo.setActiveReplicas(2);
        shardHealthInfo.setRelocatingReplicas(0);
        shardHealthInfo.setInitializingReplicas(1);
        shardHealthInfo.setUnassignedReplicas(0);

        BytesReference bytesRef = XContentHelper.toXContent(shardHealthInfo, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        ShardHealthInfo deserializedShardHealthInfo = ShardHealthInfo.fromXContent(parser);
        assertEquals(shardHealthInfo, deserializedShardHealthInfo);
    }
}

