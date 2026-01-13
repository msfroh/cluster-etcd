/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.models;

import org.opensearch.cluster.controller.enums.HealthState;
import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;

/**
 * Health information for a specific shard
 */
public class ShardHealthInfo implements ToXContentObject {

    private int shardId;
    private HealthState status;
    private boolean primaryActive;
    private int activeReplicas;
    private int relocatingReplicas;
    private int initializingReplicas;
    private int unassignedReplicas;

    public ShardHealthInfo() {}

    public int getShardId() {
        return shardId;
    }

    public void setShardId(int shardId) {
        this.shardId = shardId;
    }

    public HealthState getStatus() {
        return status;
    }

    public void setStatus(HealthState status) {
        this.status = status;
    }

    public boolean isPrimaryActive() {
        return primaryActive;
    }

    public void setPrimaryActive(boolean primaryActive) {
        this.primaryActive = primaryActive;
    }

    public int getActiveReplicas() {
        return activeReplicas;
    }

    public void setActiveReplicas(int activeReplicas) {
        this.activeReplicas = activeReplicas;
    }

    public int getRelocatingReplicas() {
        return relocatingReplicas;
    }

    public void setRelocatingReplicas(int relocatingReplicas) {
        this.relocatingReplicas = relocatingReplicas;
    }

    public int getInitializingReplicas() {
        return initializingReplicas;
    }

    public void setInitializingReplicas(int initializingReplicas) {
        this.initializingReplicas = initializingReplicas;
    }

    public int getUnassignedReplicas() {
        return unassignedReplicas;
    }

    public void setUnassignedReplicas(int unassignedReplicas) {
        this.unassignedReplicas = unassignedReplicas;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field("shardId", shardId);
        if (status != null) {
            builder.field("status", status.getValue());
        }
        builder.field("primaryActive", primaryActive);
        builder.field("activeReplicas", activeReplicas);
        builder.field("relocatingReplicas", relocatingReplicas);
        builder.field("initializingReplicas", initializingReplicas);
        builder.field("unassignedReplicas", unassignedReplicas);
        builder.endObject();
        return builder;
    }

    public static ShardHealthInfo fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        ShardHealthInfo shardHealthInfo = new ShardHealthInfo();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if ("status".equals(currentFieldName)) {
                    shardHealthInfo.status = HealthState.fromString(parser.text());
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string value for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_BOOLEAN) {
                if ("primaryActive".equals(currentFieldName)) {
                    shardHealthInfo.primaryActive = parser.booleanValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid boolean value for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid number, expected field name");
                }
                switch (currentFieldName) {
                    case "shardId":
                        shardHealthInfo.shardId = parser.intValue();
                        break;
                    case "activeReplicas":
                        shardHealthInfo.activeReplicas = parser.intValue();
                        break;
                    case "relocatingReplicas":
                        shardHealthInfo.relocatingReplicas = parser.intValue();
                        break;
                    case "initializingReplicas":
                        shardHealthInfo.initializingReplicas = parser.intValue();
                        break;
                    case "unassignedReplicas":
                        shardHealthInfo.unassignedReplicas = parser.intValue();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Invalid number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return shardHealthInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ShardHealthInfo that = (ShardHealthInfo) obj;
        return shardId == that.shardId
            && primaryActive == that.primaryActive
            && activeReplicas == that.activeReplicas
            && relocatingReplicas == that.relocatingReplicas
            && initializingReplicas == that.initializingReplicas
            && unassignedReplicas == that.unassignedReplicas
            && status == that.status;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
            shardId,
            status,
            primaryActive,
            activeReplicas,
            relocatingReplicas,
            initializingReplicas,
            unassignedReplicas
        );
    }
}
