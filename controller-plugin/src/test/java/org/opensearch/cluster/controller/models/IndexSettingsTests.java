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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IndexSettingsTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        IndexSettings indexSettings = new IndexSettings();

        BytesReference bytesRef = XContentHelper.toXContent(
            indexSettings,
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
        IndexSettings deserializedIndexSettings = IndexSettings.fromXContent(parser);
        assertEquals(indexSettings, deserializedIndexSettings);
    }

    public void testSerializationWithAllFields() throws IOException {
        IndexSettings indexSettings = new IndexSettings();
        indexSettings.setNumberOfShards(3);
        indexSettings.setShardReplicaCount(Arrays.asList(2, 2, 2));
        indexSettings.setNumGroupsPerShard(Arrays.asList(1, 1, 1));
        indexSettings.setNumIngestGroupsPerShard(Arrays.asList(1, 1, 1));
        indexSettings.setPausePullIngestion(false);
        indexSettings.setRefreshInterval("1s");
        indexSettings.setNumberOfReplicas(2);
        Map<String, Object> additionalProps = new HashMap<>();
        additionalProps.put("knn", true);
        additionalProps.put("replication.type", "SEGMENT");
        indexSettings.setAdditionalProperty("knn", true);
        indexSettings.setAdditionalProperty("replication.type", "SEGMENT");

        BytesReference bytesRef = XContentHelper.toXContent(
            indexSettings,
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
        IndexSettings deserializedIndexSettings = IndexSettings.fromXContent(parser);
        assertEquals(indexSettings, deserializedIndexSettings);
    }
}
