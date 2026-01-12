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
 * IndexSettings represents the settings for an index.
 * Known fields are typed, unknown fields are captured in additionalProperties.
 */
public class IndexSettings implements ToXContentObject {
    private Integer numberOfShards;
    private List<Integer> shardReplicaCount = new ArrayList<>();
    private List<Integer> numGroupsPerShard = new ArrayList<>();
    private List<Integer> numIngestGroupsPerShard = new ArrayList<>();
    private Boolean pausePullIngestion = false;
    private String refreshInterval;
    private Integer numberOfReplicas;

    // Capture all unknown fields (e.g., ingestion_source, knn, replication.type, etc.)
    private Map<String, Object> additionalProperties = new HashMap<>();
    
    public IndexSettings() {
    }
    
    public Integer getNumberOfShards() {
        return numberOfShards;
    }
    
    public void setNumberOfShards(Integer numberOfShards) {
        this.numberOfShards = numberOfShards;
    }
    
    public List<Integer> getShardReplicaCount() {
        return shardReplicaCount;
    }
    
    public void setShardReplicaCount(List<Integer> shardReplicaCount) {
        this.shardReplicaCount = shardReplicaCount;
    }
    
    public List<Integer> getNumGroupsPerShard() {
        return numGroupsPerShard;
    }
    
    public void setNumGroupsPerShard(List<Integer> numGroupsPerShard) {
        this.numGroupsPerShard = numGroupsPerShard;
    }
    
    public List<Integer> getNumIngestGroupsPerShard() {
        return numIngestGroupsPerShard;
    }
    
    public void setNumIngestGroupsPerShard(List<Integer> numIngestGroupsPerShard) {
        this.numIngestGroupsPerShard = numIngestGroupsPerShard;
    }
    
    public Boolean getPausePullIngestion() {
        return pausePullIngestion;
    }
    
    public void setPausePullIngestion(Boolean pausePullIngestion) {
        this.pausePullIngestion = pausePullIngestion;
    }
    
    public String getRefreshInterval() {
        return refreshInterval;
    }
    
    public void setRefreshInterval(String refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
    
    public Integer getNumberOfReplicas() {
        return numberOfReplicas;
    }
    
    public void setNumberOfReplicas(Integer numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperty(String key, Object value) {
        additionalProperties.put(key, value);
    }


    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("numberOfShards", numberOfShards);
        map.put("shardReplicaCount", shardReplicaCount);
        map.put("numGroupsPerShard", numGroupsPerShard);
        map.put("numIngestGroupsPerShard", numIngestGroupsPerShard);
        map.put("pausePullIngestion", pausePullIngestion);
        map.put("refreshInterval", refreshInterval);
        map.put("numberOfReplicas", numberOfReplicas);
        map.put("additionalProperties", additionalProperties);
        return map;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (numberOfShards != null) {
            builder.field("numberOfShards", numberOfShards);
        }
        if (shardReplicaCount != null && !shardReplicaCount.isEmpty()) {
            builder.field("shardReplicaCount", shardReplicaCount);
        }
        if (numGroupsPerShard != null && !numGroupsPerShard.isEmpty()) {
            builder.field("numGroupsPerShard", numGroupsPerShard);
        }
        if (numIngestGroupsPerShard != null && !numIngestGroupsPerShard.isEmpty()) {
            builder.field("numIngestGroupsPerShard", numIngestGroupsPerShard);
        }
        if (pausePullIngestion != null) {
            builder.field("pausePullIngestion", pausePullIngestion);
        }
        if (refreshInterval != null) {
            builder.field("refreshInterval", refreshInterval);
        }
        if (numberOfReplicas != null) {
            builder.field("numberOfReplicas", numberOfReplicas);
        }
        if (additionalProperties != null && !additionalProperties.isEmpty()) {
            builder.field("additionalProperties", additionalProperties);
        }
        builder.endObject();
        return builder;
    }
    
    public static IndexSettings fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        IndexSettings indexSettings = new IndexSettings();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid array, expected field name");
                }
                switch (currentFieldName) {
                    case "shardReplicaCount":
                        List<Integer> shardReplicaCount = new ArrayList<>();
                        parser.nextToken();
                        while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                            if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                                shardReplicaCount.add(parser.intValue());
                            }
                            parser.nextToken();
                        }
                        indexSettings.shardReplicaCount = shardReplicaCount;
                        break;
                    case "numGroupsPerShard":
                        List<Integer> numGroupsPerShard = new ArrayList<>();
                        parser.nextToken();
                        while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                            if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                                numGroupsPerShard.add(parser.intValue());
                            }
                            parser.nextToken();
                        }
                        indexSettings.numGroupsPerShard = numGroupsPerShard;
                        break;
                    case "numIngestGroupsPerShard":
                        List<Integer> numIngestGroupsPerShard = new ArrayList<>();
                        parser.nextToken();
                        while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                            if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                                numIngestGroupsPerShard.add(parser.intValue());
                            }
                            parser.nextToken();
                        }
                        indexSettings.numIngestGroupsPerShard = numIngestGroupsPerShard;
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected array for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("additionalProperties".equals(currentFieldName)) {
                    indexSettings.additionalProperties = parser.map();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if ("refreshInterval".equals(currentFieldName)) {
                    indexSettings.refreshInterval = parser.text();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_BOOLEAN) {
                if ("pausePullIngestion".equals(currentFieldName)) {
                    indexSettings.pausePullIngestion = parser.booleanValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected boolean for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid number, expected field name");
                }
                switch (currentFieldName) {
                    case "numberOfShards":
                        indexSettings.numberOfShards = parser.intValue();
                        break;
                    case "numberOfReplicas":
                        indexSettings.numberOfReplicas = parser.intValue();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return indexSettings;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IndexSettings that = (IndexSettings) obj;
        return java.util.Objects.equals(numberOfShards, that.numberOfShards) &&
               java.util.Objects.equals(shardReplicaCount, that.shardReplicaCount) &&
               java.util.Objects.equals(numGroupsPerShard, that.numGroupsPerShard) &&
               java.util.Objects.equals(numIngestGroupsPerShard, that.numIngestGroupsPerShard) &&
               java.util.Objects.equals(pausePullIngestion, that.pausePullIngestion) &&
               java.util.Objects.equals(refreshInterval, that.refreshInterval) &&
               java.util.Objects.equals(numberOfReplicas, that.numberOfReplicas) &&
               java.util.Objects.equals(additionalProperties, that.additionalProperties);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(numberOfShards, shardReplicaCount, numGroupsPerShard,
                numIngestGroupsPerShard, pausePullIngestion, refreshInterval, numberOfReplicas,
                additionalProperties);
    }
}
