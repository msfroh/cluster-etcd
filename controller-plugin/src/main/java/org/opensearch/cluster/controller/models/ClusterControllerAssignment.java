package org.opensearch.cluster.controller.models;

import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;

public class ClusterControllerAssignment implements ToXContentObject {
    private String controller;
    private String cluster;
    private long timestamp;
    private String lease;
    
    public ClusterControllerAssignment() {
    }
    
    public String getController() {
        return controller;
    }
    
    public void setController(String controller) {
        this.controller = controller;
    }
    
    public String getCluster() {
        return cluster;
    }
    
    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getLease() {
        return lease;
    }
    
    public void setLease(String lease) {
        this.lease = lease;
    }
    
    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (controller != null) {
            builder.field("controller", controller);
        }
        if (cluster != null) {
            builder.field("cluster", cluster);
        }
        builder.field("timestamp", timestamp);
        if (lease != null) {
            builder.field("lease", lease);
        }
        builder.endObject();
        return builder;
    }
    
    public static ClusterControllerAssignment fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        ClusterControllerAssignment assignment = new ClusterControllerAssignment();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                }
                switch (currentFieldName) {
                    case "controller":
                        assignment.controller = parser.text();
                        break;
                    case "cluster":
                        assignment.cluster = parser.text();
                        break;
                    case "lease":
                        assignment.lease = parser.text();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Invalid string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if ("timestamp".equals(currentFieldName)) {
                    assignment.timestamp = parser.longValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return assignment;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClusterControllerAssignment that = (ClusterControllerAssignment) obj;
        return timestamp == that.timestamp &&
               java.util.Objects.equals(controller, that.controller) &&
               java.util.Objects.equals(cluster, that.cluster) &&
               java.util.Objects.equals(lease, that.lease);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(controller, cluster, timestamp, lease);
    }
}
