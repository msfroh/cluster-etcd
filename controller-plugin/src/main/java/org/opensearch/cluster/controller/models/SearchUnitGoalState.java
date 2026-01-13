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
import java.util.HashMap;
import java.util.Map;

/**
 * Goal state for data node search units.
 * Specifies which shards should be assigned to this node and their roles.
 */
public class SearchUnitGoalState implements ToXContentObject {

    /**
     * Data node local shards: index-name -> shard-id -> role
     * Role is a simple string: "PRIMARY" or "SEARCH_REPLICA"
     */
    private Map<String, Map<String, String>> localShards;
    private String lastUpdated;
    private long version;

    public SearchUnitGoalState() {
        this.localShards = new HashMap<>();
        this.version = 1;
    }

    public Map<String, Map<String, String>> getLocalShards() {
        return localShards;
    }

    public void setLocalShards(Map<String, Map<String, String>> localShards) {
        this.localShards = localShards != null ? localShards : new HashMap<>();
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    // ========== UTILITY METHODS ==========

    /**
     * Check if this goal state contains a specific index
     */
    public boolean hasIndex(String indexName) {
        return localShards != null && localShards.containsKey(indexName);
    }

    /**
     * Get list of shard IDs for a specific index
     * @return List of shard IDs, or empty list if index not found
     */
    public java.util.List<String> getShardsForIndex(String indexName) {
        if (!hasIndex(indexName)) {
            return new java.util.ArrayList<>();
        }
        Map<String, String> shards = localShards.get(indexName);
        return shards != null ? new java.util.ArrayList<>(shards.keySet()) : new java.util.ArrayList<>();
    }

    /**
     * Get the role for a specific shard in an index
     * @param indexName The index name
     * @param shardId The shard ID
     * @return The role ("PRIMARY" or "SEARCH_REPLICA"), or null if not found
     */
    public String getShardRole(String indexName, String shardId) {
        if (!hasIndex(indexName)) {
            return null;
        }
        Map<String, String> shards = localShards.get(indexName);
        return shards != null ? shards.get(shardId) : null;
    }

    @Override
    public String toString() {
        return "SearchUnitGoalState{"
            + "localShards="
            + localShards
            + ", lastUpdated='"
            + lastUpdated
            + '\''
            + ", version="
            + version
            + '}';
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (localShards != null && !localShards.isEmpty()) {
            builder.field("localShards");
            builder.startObject();
            for (Map.Entry<String, Map<String, String>> entry : localShards.entrySet()) {
                builder.field(entry.getKey(), entry.getValue());
            }
            builder.endObject();
        }
        if (lastUpdated != null) {
            builder.field("lastUpdated", lastUpdated);
        }
        builder.field("version", version);
        builder.endObject();
        return builder;
    }

    public static SearchUnitGoalState fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        SearchUnitGoalState searchUnitGoalState = new SearchUnitGoalState();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("localShards".equals(currentFieldName)) {
                    Map<String, Map<String, String>> localShards = new HashMap<>();
                    parser.nextToken();
                    while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                        if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                            String indexName = parser.currentName();
                            parser.nextToken();
                            if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                                Map<String, String> shardMap = new HashMap<>();
                                parser.nextToken();
                                while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                                    if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                                        String shardId = parser.currentName();
                                        parser.nextToken();
                                        if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                                            shardMap.put(shardId, parser.text());
                                        }
                                    }
                                    parser.nextToken();
                                }
                                localShards.put(indexName, shardMap);
                            }
                        }
                        parser.nextToken();
                    }
                    searchUnitGoalState.localShards = localShards;
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if ("lastUpdated".equals(currentFieldName)) {
                    searchUnitGoalState.lastUpdated = parser.text();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if ("version".equals(currentFieldName)) {
                    searchUnitGoalState.version = parser.longValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return searchUnitGoalState;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SearchUnitGoalState that = (SearchUnitGoalState) obj;
        return version == that.version
            && java.util.Objects.equals(localShards, that.localShards)
            && java.util.Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(localShards, lastUpdated, version);
    }
}
