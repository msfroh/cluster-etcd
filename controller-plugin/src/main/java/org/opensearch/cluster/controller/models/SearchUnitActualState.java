/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.models;

import org.opensearch.cluster.controller.config.Constants;
import org.opensearch.cluster.controller.enums.HealthState;
import org.opensearch.cluster.controller.enums.ShardState;
import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchUnitActualState implements ToXContentObject {

    private String nodeName;
    private String address;
    private int httpPort;
    private int transportPort;
    private String nodeId;
    private String ephemeralId;

    // Resource usage metrics
    private long memoryUsedMB;
    private long memoryMaxMB;
    private int memoryUsedPercent;
    private long heapUsedMB;
    private long heapMaxMB;
    private int heapUsedPercent;
    private long diskTotalMB;
    private long diskAvailableMB;
    private int cpuUsedPercent;

    // Heartbeat and timing
    private long heartbeatIntervalMillis;
    private long timestamp;

    // Shard routing information - the key part for controller logic
    private Map<String, List<ShardRoutingInfo>> nodeRouting; // index-name -> list of shard routing info

    // Node role and shard information (populated by worker)
    private String role; // "PRIMARY", "SEARCH_REPLICA", "COORDINATOR"
    private String shardId; // "shard-1", "shard-2", etc.
    private String clusterName; // "search-cluster", "analytics-cluster", etc.

    public SearchUnitActualState() {
        this.nodeRouting = new HashMap<>();
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getTransportPort() {
        return transportPort;
    }

    public void setTransportPort(int transportPort) {
        this.transportPort = transportPort;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getEphemeralId() {
        return ephemeralId;
    }

    public void setEphemeralId(String ephemeralId) {
        this.ephemeralId = ephemeralId;
    }

    public long getMemoryUsedMB() {
        return memoryUsedMB;
    }

    public void setMemoryUsedMB(long memoryUsedMB) {
        this.memoryUsedMB = memoryUsedMB;
    }

    public long getMemoryMaxMB() {
        return memoryMaxMB;
    }

    public void setMemoryMaxMB(long memoryMaxMB) {
        this.memoryMaxMB = memoryMaxMB;
    }

    public int getMemoryUsedPercent() {
        return memoryUsedPercent;
    }

    public void setMemoryUsedPercent(int memoryUsedPercent) {
        this.memoryUsedPercent = memoryUsedPercent;
    }

    public long getHeapUsedMB() {
        return heapUsedMB;
    }

    public void setHeapUsedMB(long heapUsedMB) {
        this.heapUsedMB = heapUsedMB;
    }

    public long getHeapMaxMB() {
        return heapMaxMB;
    }

    public void setHeapMaxMB(long heapMaxMB) {
        this.heapMaxMB = heapMaxMB;
    }

    public int getHeapUsedPercent() {
        return heapUsedPercent;
    }

    public void setHeapUsedPercent(int heapUsedPercent) {
        this.heapUsedPercent = heapUsedPercent;
    }

    public long getDiskTotalMB() {
        return diskTotalMB;
    }

    public void setDiskTotalMB(long diskTotalMB) {
        this.diskTotalMB = diskTotalMB;
    }

    public long getDiskAvailableMB() {
        return diskAvailableMB;
    }

    public void setDiskAvailableMB(long diskAvailableMB) {
        this.diskAvailableMB = diskAvailableMB;
    }

    public int getCpuUsedPercent() {
        return cpuUsedPercent;
    }

    public void setCpuUsedPercent(int cpuUsedPercent) {
        this.cpuUsedPercent = cpuUsedPercent;
    }

    public long getHeartbeatIntervalMillis() {
        return heartbeatIntervalMillis;
    }

    public void setHeartbeatIntervalMillis(long heartbeatIntervalMillis) {
        this.heartbeatIntervalMillis = heartbeatIntervalMillis;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, List<ShardRoutingInfo>> getNodeRouting() {
        return nodeRouting;
    }

    public void setNodeRouting(Map<String, List<ShardRoutingInfo>> nodeRouting) {
        this.nodeRouting = nodeRouting;
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

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    // ========== UTILITY METHODS ==========

    /**
     * Determine if the search unit is healthy based on node state
     */
    public boolean isHealthy() {
        // Consider node healthy if memory and disk usage are reasonable
        // TODO: come up with more comprehensive health check logic
        return memoryUsedPercent < Constants.HEALTH_CHECK_MEMORY_THRESHOLD_PERCENT
            && diskAvailableMB > Constants.HEALTH_CHECK_DISK_THRESHOLD_MB;
    }

    /**
     * Determines the admin state of this search unit based on its health status.
     *
     * @return NORMAL if the unit is healthy, DRAIN if unhealthy
     */
    public String deriveAdminState() {
        return isHealthy() ? Constants.ADMIN_STATE_NORMAL : Constants.ADMIN_STATE_DRAIN;
    }

    /**
     * Derive node state directly as the final status representation
     * Determined by the health of the node AND the presence of active shards
     * TODO: Determine if we should report RED/YELLOW/GREEN from os directly instead of deriving it from the node state
     * Returns: GREEN (healthy+active), YELLOW (healthy+inactive), RED (unhealthy)
     */
    public HealthState deriveNodeState() {
        // First check if node is healthy based on resource usage
        if (!isHealthy()) {
            return HealthState.RED;
        }

        // Then check routing info for active shards
        if (nodeRouting != null && !nodeRouting.isEmpty()) {
            boolean hasActiveShards = nodeRouting.values()
                .stream()
                .flatMap(List::stream)
                .anyMatch(routing -> ShardState.STARTED.equals(routing.getState()));
            return hasActiveShards ? HealthState.GREEN : HealthState.YELLOW;
        }

        // If healthy but no routing info (e.g., coordinator nodes), consider it green/active
        return HealthState.GREEN;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (nodeName != null) {
            builder.field("nodeName", nodeName);
        }
        if (address != null) {
            builder.field("address", address);
        }
        builder.field("httpPort", httpPort);
        builder.field("transportPort", transportPort);
        if (nodeId != null) {
            builder.field("nodeId", nodeId);
        }
        if (ephemeralId != null) {
            builder.field("ephemeralId", ephemeralId);
        }
        builder.field("memoryUsedMB", memoryUsedMB);
        builder.field("memoryMaxMB", memoryMaxMB);
        builder.field("memoryUsedPercent", memoryUsedPercent);
        builder.field("heapUsedMB", heapUsedMB);
        builder.field("heapMaxMB", heapMaxMB);
        builder.field("heapUsedPercent", heapUsedPercent);
        builder.field("diskTotalMB", diskTotalMB);
        builder.field("diskAvailableMB", diskAvailableMB);
        builder.field("cpuUsedPercent", cpuUsedPercent);
        builder.field("heartbeatIntervalMillis", heartbeatIntervalMillis);
        builder.field("timestamp", timestamp);
        if (nodeRouting != null && !nodeRouting.isEmpty()) {
            builder.field("nodeRouting");
            builder.startObject();
            for (Map.Entry<String, List<ShardRoutingInfo>> entry : nodeRouting.entrySet()) {
                builder.startArray(entry.getKey());
                for (ShardRoutingInfo shardRoutingInfo : entry.getValue()) {
                    shardRoutingInfo.toXContent(builder, params);
                }
                builder.endArray();
            }
            builder.endObject();
        }
        if (role != null) {
            builder.field("role", role);
        }
        if (shardId != null) {
            builder.field("shardId", shardId);
        }
        if (clusterName != null) {
            builder.field("clusterName", clusterName);
        }
        builder.endObject();
        return builder;
    }

    public static SearchUnitActualState fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        SearchUnitActualState searchUnitActualState = new SearchUnitActualState();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("nodeRouting".equals(currentFieldName)) {
                    Map<String, List<ShardRoutingInfo>> nodeRouting = new HashMap<>();
                    parser.nextToken();
                    while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                        if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                            String indexName = parser.currentName();
                            parser.nextToken();
                            if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                                List<ShardRoutingInfo> shardRoutingInfoList = new ArrayList<>();
                                parser.nextToken();
                                while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                                    shardRoutingInfoList.add(ShardRoutingInfo.fromXContent(parser));
                                    parser.nextToken();
                                }
                                nodeRouting.put(indexName, shardRoutingInfoList);
                            }
                        }
                        parser.nextToken();
                    }
                    searchUnitActualState.nodeRouting = nodeRouting;
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                }
                switch (currentFieldName) {
                    case "nodeName":
                        searchUnitActualState.nodeName = parser.text();
                        break;
                    case "address":
                        searchUnitActualState.address = parser.text();
                        break;
                    case "nodeId":
                        searchUnitActualState.nodeId = parser.text();
                        break;
                    case "ephemeralId":
                        searchUnitActualState.ephemeralId = parser.text();
                        break;
                    case "role":
                        searchUnitActualState.role = parser.text();
                        break;
                    case "shardId":
                        searchUnitActualState.shardId = parser.text();
                        break;
                    case "clusterName":
                        searchUnitActualState.clusterName = parser.text();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid number, expected field name");
                }
                switch (currentFieldName) {
                    case "httpPort":
                        searchUnitActualState.httpPort = parser.intValue();
                        break;
                    case "transportPort":
                        searchUnitActualState.transportPort = parser.intValue();
                        break;
                    case "memoryUsedMB":
                        searchUnitActualState.memoryUsedMB = parser.longValue();
                        break;
                    case "memoryMaxMB":
                        searchUnitActualState.memoryMaxMB = parser.longValue();
                        break;
                    case "memoryUsedPercent":
                        searchUnitActualState.memoryUsedPercent = parser.intValue();
                        break;
                    case "heapUsedMB":
                        searchUnitActualState.heapUsedMB = parser.longValue();
                        break;
                    case "heapMaxMB":
                        searchUnitActualState.heapMaxMB = parser.longValue();
                        break;
                    case "heapUsedPercent":
                        searchUnitActualState.heapUsedPercent = parser.intValue();
                        break;
                    case "diskTotalMB":
                        searchUnitActualState.diskTotalMB = parser.longValue();
                        break;
                    case "diskAvailableMB":
                        searchUnitActualState.diskAvailableMB = parser.longValue();
                        break;
                    case "cpuUsedPercent":
                        searchUnitActualState.cpuUsedPercent = parser.intValue();
                        break;
                    case "heartbeatIntervalMillis":
                        searchUnitActualState.heartbeatIntervalMillis = parser.longValue();
                        break;
                    case "timestamp":
                        searchUnitActualState.timestamp = parser.longValue();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return searchUnitActualState;
    }

    /**
     * Shard routing information for a single shard on this node
     */
    public static class ShardRoutingInfo implements ToXContentObject {
        private int shardId;
        private String role; // "primary", "search_replica", "replica"
        private ShardState state; // e.g., STARTED, INITIALIZING, RELOCATING
        private boolean relocating;
        private String relocatingNodeId; // Target node ID when relocating
        private String allocationId;
        private String currentNodeId;
        private String currentNodeName;

        public ShardRoutingInfo() {}

        public ShardRoutingInfo(int shardId, String role, ShardState state) {
            this.shardId = shardId;
            this.role = role;
            this.state = state;
            this.relocating = false;
        }

        public int getShardId() {
            return shardId;
        }

        public void setShardId(int shardId) {
            this.shardId = shardId;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public ShardState getState() {
            return state;
        }

        public void setState(ShardState state) {
            this.state = state;
        }

        public boolean isRelocating() {
            return relocating;
        }

        public void setRelocating(boolean relocating) {
            this.relocating = relocating;
        }

        public String getRelocatingNodeId() {
            return relocatingNodeId;
        }

        public void setRelocatingNodeId(String relocatingNodeId) {
            this.relocatingNodeId = relocatingNodeId;
        }

        public String getAllocationId() {
            return allocationId;
        }

        public void setAllocationId(String allocationId) {
            this.allocationId = allocationId;
        }

        public String getCurrentNodeId() {
            return currentNodeId;
        }

        public void setCurrentNodeId(String currentNodeId) {
            this.currentNodeId = currentNodeId;
        }

        public String getCurrentNodeName() {
            return currentNodeName;
        }

        public void setCurrentNodeName(String currentNodeName) {
            this.currentNodeName = currentNodeName;
        }

        /**
         * Check if this shard is a primary shard
         * @return true if role is "primary", false otherwise
         */
        public boolean isPrimary() {
            return Constants.ROLE_PRIMARY.equalsIgnoreCase(role);
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            builder.field("shardId", shardId);
            if (role != null) {
                builder.field("role", role);
            }
            if (state != null) {
                builder.field("state", state.name());
            }
            builder.field("relocating", relocating);
            if (relocatingNodeId != null) {
                builder.field("relocatingNodeId", relocatingNodeId);
            }
            if (allocationId != null) {
                builder.field("allocationId", allocationId);
            }
            if (currentNodeId != null) {
                builder.field("currentNodeId", currentNodeId);
            }
            if (currentNodeName != null) {
                builder.field("currentNodeName", currentNodeName);
            }
            builder.endObject();
            return builder;
        }

        public static ShardRoutingInfo fromXContent(XContentParser parser) throws IOException {
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                parser.nextToken();
            }
            ShardRoutingInfo shardRoutingInfo = new ShardRoutingInfo();
            String currentFieldName = null;
            while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                    if (currentFieldName == null) {
                        throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                    }
                    switch (currentFieldName) {
                        case "role":
                            shardRoutingInfo.role = parser.text();
                            break;
                        case "state":
                            shardRoutingInfo.state = ShardState.valueOf(parser.text());
                            break;
                        case "relocatingNodeId":
                            shardRoutingInfo.relocatingNodeId = parser.text();
                            break;
                        case "allocationId":
                            shardRoutingInfo.allocationId = parser.text();
                            break;
                        case "currentNodeId":
                            shardRoutingInfo.currentNodeId = parser.text();
                            break;
                        case "currentNodeName":
                            shardRoutingInfo.currentNodeName = parser.text();
                            break;
                        default:
                            throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                    }
                } else if (parser.currentToken() == XContentParser.Token.VALUE_BOOLEAN) {
                    if ("relocating".equals(currentFieldName)) {
                        shardRoutingInfo.relocating = parser.booleanValue();
                    } else {
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected boolean for field " + currentFieldName);
                    }
                } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                    if ("shardId".equals(currentFieldName)) {
                        shardRoutingInfo.shardId = parser.intValue();
                    } else {
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                    }
                }
                parser.nextToken();
            }
            return shardRoutingInfo;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ShardRoutingInfo that = (ShardRoutingInfo) obj;
            return shardId == that.shardId
                && relocating == that.relocating
                && java.util.Objects.equals(role, that.role)
                && state == that.state
                && java.util.Objects.equals(relocatingNodeId, that.relocatingNodeId)
                && java.util.Objects.equals(allocationId, that.allocationId)
                && java.util.Objects.equals(currentNodeId, that.currentNodeId)
                && java.util.Objects.equals(currentNodeName, that.currentNodeName);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(shardId, role, state, relocating, relocatingNodeId, allocationId, currentNodeId, currentNodeName);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SearchUnitActualState that = (SearchUnitActualState) obj;
        return httpPort == that.httpPort
            && transportPort == that.transportPort
            && memoryUsedMB == that.memoryUsedMB
            && memoryMaxMB == that.memoryMaxMB
            && memoryUsedPercent == that.memoryUsedPercent
            && heapUsedMB == that.heapUsedMB
            && heapMaxMB == that.heapMaxMB
            && heapUsedPercent == that.heapUsedPercent
            && diskTotalMB == that.diskTotalMB
            && diskAvailableMB == that.diskAvailableMB
            && cpuUsedPercent == that.cpuUsedPercent
            && heartbeatIntervalMillis == that.heartbeatIntervalMillis
            && timestamp == that.timestamp
            && java.util.Objects.equals(nodeName, that.nodeName)
            && java.util.Objects.equals(address, that.address)
            && java.util.Objects.equals(nodeId, that.nodeId)
            && java.util.Objects.equals(ephemeralId, that.ephemeralId)
            && java.util.Objects.equals(nodeRouting, that.nodeRouting)
            && java.util.Objects.equals(role, that.role)
            && java.util.Objects.equals(shardId, that.shardId)
            && java.util.Objects.equals(clusterName, that.clusterName);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
            nodeName,
            address,
            httpPort,
            transportPort,
            nodeId,
            ephemeralId,
            memoryUsedMB,
            memoryMaxMB,
            memoryUsedPercent,
            heapUsedMB,
            heapMaxMB,
            heapUsedPercent,
            diskTotalMB,
            diskAvailableMB,
            cpuUsedPercent,
            heartbeatIntervalMillis,
            timestamp,
            nodeRouting,
            role,
            shardId,
            clusterName
        );
    }
}
