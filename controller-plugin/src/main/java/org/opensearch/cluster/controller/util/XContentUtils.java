/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.util;

import org.opensearch.common.CheckedFunction;
import org.opensearch.common.xcontent.XContentFactory;
import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.core.common.bytes.BytesReference;
import org.opensearch.core.xcontent.DeprecationHandler;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentHelper;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.util.Map;

public final class XContentUtils {
    private XContentUtils() {}

    public static String writeValue(ToXContentObject value) throws IOException {
        return XContentHelper.toXContent(value, JsonXContent.jsonXContent.mediaType(), ToXContent.EMPTY_PARAMS, false).utf8ToString();
    }

    public static String writeValue(Map<String, Object> map) throws IOException {
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder.map(map);
            return BytesReference.bytes(builder).utf8ToString();
        }
    }

    public static <T, E extends Exception> T readValue(String value, CheckedFunction<XContentParser, T, E> parser) throws IOException, E {
        try (
            XContentParser xContentParser = JsonXContent.jsonXContent.createParser(
                NamedXContentRegistry.EMPTY,
                DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
                value
            )
        ) {
            return parser.apply(xContentParser);
        }
    }

    public static Map<String, Object> readMap(String value) throws IOException {
        try (
            XContentParser xContentParser = JsonXContent.jsonXContent.createParser(
                NamedXContentRegistry.EMPTY,
                DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
                value
            )
        ) {
            return xContentParser.map();
        }
    }
}
