package org.opensearch.cluster.controller.models;

import org.opensearch.cluster.controller.enums.HealthState;
import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Health information for a specific index
 */
public class IndexHealthInfo implements ToXContentObject {
    private HealthState status;
    private int numberOfShards;
    private int numberOfReplicas;
    private int activeShards;
    private int relocatingShards;
    private int initializingShards;
    private int unassignedShards;
    private Map<String, ShardHealthInfo> shards = new HashMap<>(); // Only populated for shard-level detail

    public IndexHealthInfo() {
    }

    public HealthState getStatus() {
        return status;
    }

    public void setStatus(HealthState status) {
        this.status = status;
    }

    public int getNumberOfShards() {
        return numberOfShards;
    }

    public void setNumberOfShards(int numberOfShards) {
        this.numberOfShards = numberOfShards;
    }

    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public void setNumberOfReplicas(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public int getActiveShards() {
        return activeShards;
    }

    public void setActiveShards(int activeShards) {
        this.activeShards = activeShards;
    }

    public int getRelocatingShards() {
        return relocatingShards;
    }

    public void setRelocatingShards(int relocatingShards) {
        this.relocatingShards = relocatingShards;
    }

    public int getInitializingShards() {
        return initializingShards;
    }

    public void setInitializingShards(int initializingShards) {
        this.initializingShards = initializingShards;
    }

    public int getUnassignedShards() {
        return unassignedShards;
    }

    public void setUnassignedShards(int unassignedShards) {
        this.unassignedShards = unassignedShards;
    }

    public Map<String, ShardHealthInfo> getShards() {
        return shards;
    }

    public void setShards(Map<String, ShardHealthInfo> shards) {
        this.shards = shards;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (status != null) {
            builder.field("status", status.getValue());
        }
        builder.field("numberOfShards", numberOfShards);
        builder.field("numberOfReplicas", numberOfReplicas);
        builder.field("activeShards", activeShards);
        builder.field("relocatingShards", relocatingShards);
        builder.field("initializingShards", initializingShards);
        builder.field("unassignedShards", unassignedShards);
        builder.field("shards").map(shards);
        builder.endObject();
        return builder;
    }

    public static IndexHealthInfo fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        IndexHealthInfo indexHealthInfo = new IndexHealthInfo();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("shards".equals(currentFieldName)) {
                    indexHealthInfo.shards = parser.map(HashMap::new, ShardHealthInfo::fromXContent);
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid start object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if ("status".equals(currentFieldName)) {
                    indexHealthInfo.status = HealthState.fromString(parser.text());
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string value for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid number, expected field name");
                }
                switch (currentFieldName) {
                    case "numberOfShards":
                        indexHealthInfo.numberOfShards = parser.intValue();
                        break;
                    case "numberOfReplicas":
                        indexHealthInfo.numberOfReplicas = parser.intValue();
                        break;
                    case "activeShards":
                        indexHealthInfo.activeShards = parser.intValue();
                        break;
                    case "relocatingShards":
                        indexHealthInfo.relocatingShards = parser.intValue();
                        break;
                    case "initializingShards":
                        indexHealthInfo.initializingShards = parser.intValue();
                        break;
                    case "unassignedShards":
                        indexHealthInfo.unassignedShards = parser.intValue();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Invalid number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return indexHealthInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IndexHealthInfo that = (IndexHealthInfo) obj;
        return numberOfShards == that.numberOfShards &&
                numberOfReplicas == that.numberOfReplicas &&
                activeShards == that.activeShards &&
                relocatingShards == that.relocatingShards &&
                initializingShards == that.initializingShards &&
                unassignedShards == that.unassignedShards &&
                status == that.status &&
                java.util.Objects.equals(shards, that.shards);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(status, numberOfShards, numberOfReplicas, activeShards,
                relocatingShards, initializingShards, unassignedShards, shards);
    }
}
