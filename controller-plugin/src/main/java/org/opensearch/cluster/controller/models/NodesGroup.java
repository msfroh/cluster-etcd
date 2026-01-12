package org.opensearch.cluster.controller.models;

import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a logical group of nodes.
 * 
 * A GROUP is a fixed set of nodes (typically 3 or 5 nodes) with the same role
 * that are treated as a single allocation unit for bin-packing purposes.
 * 
 * All nodes in a group must:
 * - Have the same role (PRIMARY or REPLICA)
 * - Have the same GROUP ID (currently extracted from shard pool)
 */
public class NodesGroup implements ToXContentObject {
    
    /**
     * Unique identifier for this group (extracted from nodes' GROUP ID)
     */
    private String groupId;
    
    /**
     * Role of all nodes in this group (e.g., "PRIMARY", "SEARCH_REPLICA")
     */
    private String role;
    
    /**
     * Shard ID that this group belongs to
     */
    private String shardId;
    
    /**
     * List of SearchUnit node IDs in this group
     */
    private List<String> nodeIds;
    
    /**
     * List of actual SearchUnit objects in this group
     */
    private List<SearchUnit> nodes;
    
    /**
     * Current load on this group (number of shards allocated to this group)
     * Used for bin-packing: select groups with lower load
     */
    private int currentLoad;
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getShardId() {
        return shardId;
    }
    
    public void setShardId(String shardId) {
        this.shardId = shardId;
    }
    
    public List<String> getNodeIds() {
        return nodeIds;
    }
    
    public void setNodeIds(List<String> nodeIds) {
        this.nodeIds = nodeIds;
    }
    
    public List<SearchUnit> getNodes() {
        return nodes;
    }
    
    public void setNodes(List<SearchUnit> nodes) {
        this.nodes = nodes;
    }
    
    public int getCurrentLoad() {
        return currentLoad;
    }
    
    public void setCurrentLoad(int currentLoad) {
        this.currentLoad = currentLoad;
    }
    
    /**
     * Group size (number of nodes)
     */
    public int size() {
        return nodes != null ? nodes.size() : 0;
    }
    
    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (groupId != null) {
            builder.field("groupId", groupId);
        }
        if (role != null) {
            builder.field("role", role);
        }
        if (shardId != null) {
            builder.field("shardId", shardId);
        }
        if (nodeIds != null && !nodeIds.isEmpty()) {
            builder.field("nodeIds", nodeIds);
        }
        if (nodes != null && !nodes.isEmpty()) {
            builder.startArray("nodes");
            for (SearchUnit node : nodes) {
                node.toXContent(builder, params);
            }
            builder.endArray();
        }
        builder.field("currentLoad", currentLoad);
        builder.endObject();
        return builder;
    }
    
    public static NodesGroup fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        NodesGroup nodesGroup = new NodesGroup();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid array, expected field name");
                }
                switch (currentFieldName) {
                    case "nodeIds":
                        List<String> nodeIds = new ArrayList<>();
                        parser.nextToken();
                        while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                            if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                                nodeIds.add(parser.text());
                            }
                            parser.nextToken();
                        }
                        nodesGroup.nodeIds = nodeIds;
                        break;
                    case "nodes":
                        List<SearchUnit> nodes = new ArrayList<>();
                        parser.nextToken();
                        while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                            nodes.add(SearchUnit.fromXContent(parser));
                            parser.nextToken();
                        }
                        nodesGroup.nodes = nodes;
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected array for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                }
                switch (currentFieldName) {
                    case "groupId":
                        nodesGroup.groupId = parser.text();
                        break;
                    case "role":
                        nodesGroup.role = parser.text();
                        break;
                    case "shardId":
                        nodesGroup.shardId = parser.text();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if ("currentLoad".equals(currentFieldName)) {
                    nodesGroup.currentLoad = parser.intValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return nodesGroup;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NodesGroup that = (NodesGroup) obj;
        return currentLoad == that.currentLoad &&
               java.util.Objects.equals(groupId, that.groupId) &&
               java.util.Objects.equals(role, that.role) &&
               java.util.Objects.equals(shardId, that.shardId) &&
               java.util.Objects.equals(nodeIds, that.nodeIds) &&
               java.util.Objects.equals(nodes, that.nodes);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(groupId, role, shardId, nodeIds, nodes, currentLoad);
    }
}
