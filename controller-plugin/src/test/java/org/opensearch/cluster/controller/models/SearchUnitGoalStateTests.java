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
import java.util.HashMap;
import java.util.Map;

public class SearchUnitGoalStateTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        SearchUnitGoalState searchUnitGoalState = new SearchUnitGoalState();

        BytesReference bytesRef = XContentHelper.toXContent(
            searchUnitGoalState,
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
        SearchUnitGoalState deserializedSearchUnitGoalState = SearchUnitGoalState.fromXContent(parser);
        assertEquals(searchUnitGoalState, deserializedSearchUnitGoalState);
    }

    public void testSerializationWithAllFields() throws IOException {
        SearchUnitGoalState searchUnitGoalState = new SearchUnitGoalState();
        Map<String, Map<String, String>> localShards = new HashMap<>();
        Map<String, String> index1Shards = new HashMap<>();
        index1Shards.put("0", "PRIMARY");
        index1Shards.put("1", "SEARCH_REPLICA");
        localShards.put("index1", index1Shards);
        Map<String, String> index2Shards = new HashMap<>();
        index2Shards.put("0", "PRIMARY");
        localShards.put("index2", index2Shards);
        searchUnitGoalState.setLocalShards(localShards);
        searchUnitGoalState.setLastUpdated("2026-01-12T10:00:00Z");
        searchUnitGoalState.setVersion(5);

        BytesReference bytesRef = XContentHelper.toXContent(
            searchUnitGoalState,
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
        SearchUnitGoalState deserializedSearchUnitGoalState = SearchUnitGoalState.fromXContent(parser);
        assertEquals(searchUnitGoalState, deserializedSearchUnitGoalState);
    }
}
