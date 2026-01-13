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

public class IndexMetadataTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        IndexMetadata indexMetadata = new IndexMetadata();

        BytesReference bytesRef = XContentHelper.toXContent(
            indexMetadata,
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
        IndexMetadata deserializedIndexMetadata = IndexMetadata.fromXContent(parser);
        assertEquals(indexMetadata, deserializedIndexMetadata);
    }

    public void testAliasConfigSerializationEmpty() throws IOException {
        IndexMetadata.AliasConfig aliasConfig = new IndexMetadata.AliasConfig();

        BytesReference bytesRef = XContentHelper.toXContent(
            aliasConfig,
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
        IndexMetadata.AliasConfig deserializedAliasConfig = IndexMetadata.AliasConfig.fromXContent(parser);
        assertEquals(aliasConfig, deserializedAliasConfig);
    }

    public void testBatchIngestionSourceSerializationEmpty() throws IOException {
        IndexMetadata.BatchIngestionSource batchIngestionSource = new IndexMetadata.BatchIngestionSource();

        BytesReference bytesRef = XContentHelper.toXContent(
            batchIngestionSource,
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
        IndexMetadata.BatchIngestionSource deserializedBatchIngestionSource = IndexMetadata.BatchIngestionSource.fromXContent(parser);
        assertEquals(batchIngestionSource, deserializedBatchIngestionSource);
    }

    public void testLiveIngestionSourceSerializationEmpty() throws IOException {
        IndexMetadata.LiveIngestionSource liveIngestionSource = new IndexMetadata.LiveIngestionSource();

        BytesReference bytesRef = XContentHelper.toXContent(
            liveIngestionSource,
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
        IndexMetadata.LiveIngestionSource deserializedLiveIngestionSource = IndexMetadata.LiveIngestionSource.fromXContent(parser);
        assertEquals(liveIngestionSource, deserializedLiveIngestionSource);
    }
}
