package org.opensearch.cluster.controller.models;

import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;


/**
 * Represents the actual state reported by a coordinator group
 * Contains remote_shards with actual routing information for all indices
 */
public class CoordinatorActualState implements ToXContentObject {
    
    /**
     * Remote shards information that this coordinator is currently aware of.
     */
    private CoordinatorGoalState.RemoteShards remoteShards;
    private String lastUpdated;
    private long version;
    
    public CoordinatorActualState() {
        this.remoteShards = new CoordinatorGoalState.RemoteShards();
        this.version = 1;
    }
    
    public CoordinatorActualState(CoordinatorGoalState.RemoteShards remoteShards) {
        this.remoteShards = remoteShards != null ? remoteShards : new CoordinatorGoalState.RemoteShards();
        this.version = 1;
    }
    
    public CoordinatorGoalState.RemoteShards getRemoteShards() {
        return remoteShards;
    }
    
    public void setRemoteShards(CoordinatorGoalState.RemoteShards remoteShards) {
        this.remoteShards = remoteShards;
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
    
    public static CoordinatorActualState fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        CoordinatorActualState coordinatorActualState = new CoordinatorActualState();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("remoteShards".equals(currentFieldName)) {
                    coordinatorActualState.remoteShards = CoordinatorGoalState.RemoteShards.fromXContent(parser);
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if ("lastUpdated".equals(currentFieldName)) {
                    coordinatorActualState.lastUpdated = parser.text();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if ("version".equals(currentFieldName)) {
                    coordinatorActualState.version = parser.longValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return coordinatorActualState;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CoordinatorActualState that = (CoordinatorActualState) obj;
        return version == that.version &&
               java.util.Objects.equals(remoteShards, that.remoteShards) &&
               java.util.Objects.equals(lastUpdated, that.lastUpdated);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(remoteShards, lastUpdated, version);
    }
}
