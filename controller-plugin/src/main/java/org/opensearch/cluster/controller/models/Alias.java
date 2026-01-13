/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.models;

import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.time.ZoneOffset.UTC;

/**
 * Configuration for an alias
 * Stored at /aliases/{clusterId}/{aliasName}/conf
 */
public class Alias implements ToXContentObject {

    private String aliasName;
    private Object targetIndices; // Can be String (single) or List<String> (multiple)
    private String createdAt = java.time.OffsetDateTime.now(UTC).toString();
    private String updatedAt = java.time.OffsetDateTime.now(UTC).toString();

    public Alias() {}

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Object getTargetIndices() {
        return targetIndices;
    }

    public void setTargetIndices(Object targetIndices) {
        this.targetIndices = targetIndices;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Get target indices as a list (whether stored as String or List)
     */
    public List<String> getTargetIndicesAsList() {
        if (targetIndices instanceof String) {
            List<String> list = new ArrayList<>();
            list.add((String) targetIndices);
            return list;
        } else if (targetIndices instanceof List) {
            return (List<String>) targetIndices;
        }
        return new ArrayList<>();
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (aliasName != null) {
            builder.field("aliasName", aliasName);
        }
        if (targetIndices != null) {
            if (targetIndices instanceof String) {
                builder.field("targetIndices", (String) targetIndices);
            } else if (targetIndices instanceof List) {
                builder.field("targetIndices", (List<?>) targetIndices);
            }
        }
        if (createdAt != null) {
            builder.field("createdAt", createdAt);
        }
        if (updatedAt != null) {
            builder.field("updatedAt", updatedAt);
        }
        builder.endObject();
        return builder;
    }

    public static Alias fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        Alias alias = new Alias();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                if ("targetIndices".equals(currentFieldName)) {
                    List<String> targetIndicesList = new ArrayList<>();
                    parser.nextToken();
                    while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                        if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                            targetIndicesList.add(parser.text());
                        } else {
                            throw new ParsingException(parser.getTokenLocation(), "Unexpected value in targetIndices array");
                        }
                        parser.nextToken();
                    }
                    alias.targetIndices = targetIndicesList;
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected array for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                }
                switch (currentFieldName) {
                    case "aliasName":
                        alias.aliasName = parser.text();
                        break;
                    case "targetIndices":
                        alias.targetIndices = parser.text();
                        break;
                    case "createdAt":
                        alias.createdAt = parser.text();
                        break;
                    case "updatedAt":
                        alias.updatedAt = parser.text();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Invalid string for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return alias;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Alias alias = (Alias) obj;
        return java.util.Objects.equals(aliasName, alias.aliasName)
            && java.util.Objects.equals(targetIndices, alias.targetIndices)
            && java.util.Objects.equals(createdAt, alias.createdAt)
            && java.util.Objects.equals(updatedAt, alias.updatedAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(aliasName, targetIndices, createdAt, updatedAt);
    }
}
