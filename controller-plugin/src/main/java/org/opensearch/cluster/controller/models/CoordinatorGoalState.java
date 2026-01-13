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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Goal state for coordinator nodes.
 * Specifies which indices, shards, aliases, and remote clusters this coordinator should be aware of.
 */
public class CoordinatorGoalState implements ToXContentObject {

    private RemoteShards remoteShards;
    private String lastUpdated;
    private long version;

    public CoordinatorGoalState() {
        this.remoteShards = new RemoteShards();
        this.version = 1;
    }

    public RemoteShards getRemoteShards() {
        return remoteShards;
    }

    public void setRemoteShards(RemoteShards remoteShards) {
        this.remoteShards = remoteShards != null ? remoteShards : new RemoteShards();
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

    /**
     * Remote shards structure for coordinator nodes
     */
    public static class RemoteShards implements ToXContentObject {
        /**
         * Map of indices with their shard routing information
         * index-name -> shard routing configuration
         */
        private Map<String, IndexShardRouting> indices;

        /**
         * Map of aliases to their target indices
         * alias-name -> index-name (String) or List<String> for multi-index aliases
         */
        private Map<String, Object> aliases;

        public RemoteShards() {
            this.indices = new HashMap<>();
            this.aliases = new HashMap<>();
        }

        public Map<String, IndexShardRouting> getIndices() {
            return indices;
        }

        public void setIndices(Map<String, IndexShardRouting> indices) {
            this.indices = indices;
        }

        public Map<String, Object> getAliases() {
            return aliases;
        }

        public void setAliases(Map<String, Object> aliases) {
            this.aliases = aliases;
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            if (indices != null && !indices.isEmpty()) {
                builder.field("indices");
                builder.startObject();
                for (Map.Entry<String, IndexShardRouting> entry : indices.entrySet()) {
                    builder.field(entry.getKey());
                    entry.getValue().toXContent(builder, params);
                }
                builder.endObject();
            }
            if (aliases != null && !aliases.isEmpty()) {
                builder.field("aliases", aliases);
            }
            builder.endObject();
            return builder;
        }

        public static RemoteShards fromXContent(XContentParser parser) throws IOException {
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                parser.nextToken();
            }
            RemoteShards remoteShards = new RemoteShards();
            String currentFieldName = null;
            while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                    if ("indices".equals(currentFieldName)) {
                        remoteShards.indices = parser.map(HashMap::new, IndexShardRouting::fromXContent);
                    } else if ("aliases".equals(currentFieldName)) {
                        remoteShards.aliases = parser.map();
                    } else {
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                    }
                }
                parser.nextToken();
            }
            return remoteShards;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            RemoteShards that = (RemoteShards) obj;
            return java.util.Objects.equals(indices, that.indices) && java.util.Objects.equals(aliases, that.aliases);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(indices, aliases);
        }
    }

    /**
     * Shard routing information for an index
     */
    public static class IndexShardRouting implements ToXContentObject {
        /**
         * Array of shard replicas, where each element is an array of node assignments
         * Each inner array represents all replicas for that shard number
         */
        private List<List<ShardNodeAssignment>> shardRouting;

        public List<List<ShardNodeAssignment>> getShardRouting() {
            return shardRouting;
        }

        public void setShardRouting(List<List<ShardNodeAssignment>> shardRouting) {
            this.shardRouting = shardRouting;
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            if (shardRouting != null && !shardRouting.isEmpty()) {
                builder.startArray("shardRouting");
                for (List<ShardNodeAssignment> shardReplicas : shardRouting) {
                    builder.startArray();
                    for (ShardNodeAssignment assignment : shardReplicas) {
                        assignment.toXContent(builder, params);
                    }
                    builder.endArray();
                }
                builder.endArray();
            }
            builder.endObject();
            return builder;
        }

        public static IndexShardRouting fromXContent(XContentParser parser) throws IOException {
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                parser.nextToken();
            }
            IndexShardRouting indexShardRouting = new IndexShardRouting();
            String currentFieldName = null;
            while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                    if ("shardRouting".equals(currentFieldName)) {
                        List<List<ShardNodeAssignment>> shardRouting = new ArrayList<>();
                        parser.nextToken();
                        while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                            if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                                List<ShardNodeAssignment> assignments = new ArrayList<>();
                                parser.nextToken();
                                while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                                    assignments.add(ShardNodeAssignment.fromXContent(parser));
                                    parser.nextToken();
                                }
                                shardRouting.add(assignments);
                            }
                            parser.nextToken();
                        }
                        indexShardRouting.shardRouting = shardRouting;
                    } else {
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected array for field " + currentFieldName);
                    }
                }
                parser.nextToken();
            }
            return indexShardRouting;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            IndexShardRouting that = (IndexShardRouting) obj;
            return java.util.Objects.equals(shardRouting, that.shardRouting);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(shardRouting);
        }
    }

    /**
     * Node assignment for a specific shard replica
     */
    public static class ShardNodeAssignment implements ToXContentObject {
        private String nodeName;
        private Boolean primary; // null/absent = search replica, true = primary

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public Boolean getPrimary() {
            return primary;
        }

        public void setPrimary(Boolean primary) {
            this.primary = primary;
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            if (nodeName != null) {
                builder.field("nodeName", nodeName);
            }
            if (primary != null) {
                builder.field("primary", primary);
            }
            builder.endObject();
            return builder;
        }

        public static ShardNodeAssignment fromXContent(XContentParser parser) throws IOException {
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                parser.nextToken();
            }
            ShardNodeAssignment shardNodeAssignment = new ShardNodeAssignment();
            String currentFieldName = null;
            while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                    if ("nodeName".equals(currentFieldName)) {
                        shardNodeAssignment.nodeName = parser.text();
                    } else {
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                    }
                } else if (parser.currentToken() == XContentParser.Token.VALUE_BOOLEAN) {
                    if ("primary".equals(currentFieldName)) {
                        shardNodeAssignment.primary = parser.booleanValue();
                    } else {
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected boolean for field " + currentFieldName);
                    }
                }
                parser.nextToken();
            }
            return shardNodeAssignment;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ShardNodeAssignment that = (ShardNodeAssignment) obj;
            return java.util.Objects.equals(nodeName, that.nodeName) && java.util.Objects.equals(primary, that.primary);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(nodeName, primary);
        }
    }

    @Override
    public String toString() {
        return "CoordinatorGoalState{"
            + "remoteShards="
            + remoteShards
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
        if (remoteShards != null) {
            builder.field("remoteShards");
            remoteShards.toXContent(builder, params);
        }
        if (lastUpdated != null) {
            builder.field("lastUpdated", lastUpdated);
        }
        builder.field("version", version);
        builder.endObject();
        return builder;
    }

    public static CoordinatorGoalState fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        CoordinatorGoalState coordinatorGoalState = new CoordinatorGoalState();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("remoteShards".equals(currentFieldName)) {
                    coordinatorGoalState.remoteShards = RemoteShards.fromXContent(parser);
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if ("lastUpdated".equals(currentFieldName)) {
                    coordinatorGoalState.lastUpdated = parser.text();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if ("version".equals(currentFieldName)) {
                    coordinatorGoalState.version = parser.longValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return coordinatorGoalState;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CoordinatorGoalState that = (CoordinatorGoalState) obj;
        return version == that.version
            && java.util.Objects.equals(remoteShards, that.remoteShards)
            && java.util.Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(remoteShards, lastUpdated, version);
    }
}
