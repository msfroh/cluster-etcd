/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
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
import java.util.HashMap;
import java.util.Map;

public class SearchUnitTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        SearchUnit searchUnit = new SearchUnit();

        BytesReference bytesRef = XContentHelper.toXContent(
            searchUnit,
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
        SearchUnit deserializedSearchUnit = SearchUnit.fromXContent(parser);
        assertEquals(searchUnit, deserializedSearchUnit);
    }

    public void testSerializationWithAllFields() throws IOException {
        SearchUnit searchUnit = new SearchUnit();
        searchUnit.setId("su-12345");
        searchUnit.setName("node-1");
        searchUnit.setClusterName("my-cluster");
        searchUnit.setRole("PRIMARY");
        searchUnit.setHost("192.168.1.10");
        searchUnit.setPortHttp(9200);
        searchUnit.setPortTransport(9300);
        searchUnit.setZone("us-west-2a");
        searchUnit.setShardId("shard-1");
        searchUnit.setStateAdmin("NORMAL");
        searchUnit.setStatePulled(HealthState.GREEN);
        Map<String, Object> nodeAttributes = new HashMap<>();
        nodeAttributes.put("node.data", "true");
        nodeAttributes.put("node.master", "false");
        searchUnit.setNodeAttributes(nodeAttributes);

        BytesReference bytesRef = XContentHelper.toXContent(
            searchUnit,
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
        SearchUnit deserializedSearchUnit = SearchUnit.fromXContent(parser);
        assertEquals(searchUnit, deserializedSearchUnit);
    }
}
