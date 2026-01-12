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

public class AliasTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        Alias alias = new Alias();

        BytesReference bytesRef = XContentHelper.toXContent(alias, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        Alias deserializedAlias = Alias.fromXContent(parser);
        assertEquals(alias, deserializedAlias);
    }

    public void testSerializationWithAllFields() throws IOException {
        Alias alias = new Alias();
        alias.setAliasName("my-alias");
        alias.setTargetIndices("my-index");
        alias.setCreatedAt("2026-01-12T10:00:00Z");
        alias.setUpdatedAt("2026-01-12T11:00:00Z");

        BytesReference bytesRef = XContentHelper.toXContent(alias, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        Alias deserializedAlias = Alias.fromXContent(parser);
        assertEquals(alias, deserializedAlias);
    }

    public void testSerializationWithListTargetIndices() throws IOException {
        Alias alias = new Alias();
        alias.setAliasName("my-alias");
        alias.setTargetIndices(java.util.Arrays.asList("index1", "index2", "index3"));
        alias.setCreatedAt("2026-01-12T10:00:00Z");
        alias.setUpdatedAt("2026-01-12T11:00:00Z");

        BytesReference bytesRef = XContentHelper.toXContent(alias, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        Alias deserializedAlias = Alias.fromXContent(parser);
        assertEquals(alias, deserializedAlias);
    }
}

