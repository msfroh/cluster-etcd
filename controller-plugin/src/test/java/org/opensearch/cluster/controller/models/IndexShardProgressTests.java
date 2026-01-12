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

public class IndexShardProgressTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        IndexShardProgress indexShardProgress = new IndexShardProgress();

        BytesReference bytesRef = XContentHelper.toXContent(indexShardProgress, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        IndexShardProgress deserializedIndexShardProgress = IndexShardProgress.fromXContent(parser);
        assertEquals(indexShardProgress, deserializedIndexShardProgress);
    }

    public void testSerializationWithAllFields() throws IOException {
        IndexShardProgress indexShardProgress = new IndexShardProgress();
        indexShardProgress.setIndexShard("my-index/shard-0");
        indexShardProgress.setGoalStateUpdatedCount(5);
        indexShardProgress.setActualStateConvergedCount(3);
        indexShardProgress.setTotalNodes(10);
        indexShardProgress.setUpdatedNodes(Arrays.asList("node1", "node2", "node3", "node4", "node5"));

        BytesReference bytesRef = XContentHelper.toXContent(indexShardProgress, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        IndexShardProgress deserializedIndexShardProgress = IndexShardProgress.fromXContent(parser);
        assertEquals(indexShardProgress, deserializedIndexShardProgress);
    }
}

