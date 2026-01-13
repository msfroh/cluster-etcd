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

public class TemplateRequestTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        TemplateRequest templateRequest = new TemplateRequest();

        BytesReference bytesRef = XContentHelper.toXContent(
            templateRequest,
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
        TemplateRequest deserializedTemplateRequest = TemplateRequest.fromXContent(parser);
        assertEquals(templateRequest, deserializedTemplateRequest);
    }

    public void testTemplateDefinitionSerializationEmpty() throws IOException {
        TemplateRequest.TemplateDefinition templateDefinition = new TemplateRequest.TemplateDefinition();

        BytesReference bytesRef = XContentHelper.toXContent(
            templateDefinition,
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
        TemplateRequest.TemplateDefinition deserializedTemplateDefinition = TemplateRequest.TemplateDefinition.fromXContent(parser);
        assertEquals(templateDefinition, deserializedTemplateDefinition);
    }
}
