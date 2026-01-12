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

public class TemplateTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        Template template = new Template();

        BytesReference bytesRef = XContentHelper.toXContent(template, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        Template deserializedTemplate = Template.fromXContent(parser);
        assertEquals(template, deserializedTemplate);
    }

    public void testTemplateDefinitionSerializationEmpty() throws IOException {
        Template.TemplateDefinition templateDefinition = new Template.TemplateDefinition();

        BytesReference bytesRef = XContentHelper.toXContent(templateDefinition, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false);
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, bytes);
        Template.TemplateDefinition deserializedTemplateDefinition = Template.TemplateDefinition.fromXContent(parser);
        assertEquals(templateDefinition, deserializedTemplateDefinition);
    }
}

