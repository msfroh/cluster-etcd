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
 * Response class for cluster health information
 */
public class ClusterHealthInfo implements ToXContentObject {
    private String clusterName;
    private HealthState status;
    private boolean timedOut;
    private int numberOfNodes;
    private int numberOfDataNodes;
    private int numberOfCoordinatorNodes;
    private int activeNodes;
    private int numberOfIndices;
    private int activePrimaryShards;
    private int activeShards;
    private int relocatingShards;
    private int initializingShards;
    private int unassignedShards;
    private int delayedUnassignedShards;
    private int failedShards;
    private int totalShards;
    private int activeShardsPercentAsNumber;
    private int numberOfPendingTasks;
    private int numberOfInFlightFetch;
    private int taskMaxWaitingInQueueMillis;
    private Map<HealthState, Integer> nodesByHealth = new HashMap<>();
    private Map<String, IndexHealthInfo> indices = new HashMap<>();

    public ClusterHealthInfo() {
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public HealthState getStatus() {
        return status;
    }

    public void setStatus(HealthState status) {
        this.status = status;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public int getNumberOfDataNodes() {
        return numberOfDataNodes;
    }

    public void setNumberOfDataNodes(int numberOfDataNodes) {
        this.numberOfDataNodes = numberOfDataNodes;
    }

    public int getNumberOfCoordinatorNodes() {
        return numberOfCoordinatorNodes;
    }

    public void setNumberOfCoordinatorNodes(int numberOfCoordinatorNodes) {
        this.numberOfCoordinatorNodes = numberOfCoordinatorNodes;
    }

    public int getActiveNodes() {
        return activeNodes;
    }

    public void setActiveNodes(int activeNodes) {
        this.activeNodes = activeNodes;
    }

    public int getNumberOfIndices() {
        return numberOfIndices;
    }

    public void setNumberOfIndices(int numberOfIndices) {
        this.numberOfIndices = numberOfIndices;
    }

    public int getActivePrimaryShards() {
        return activePrimaryShards;
    }

    public void setActivePrimaryShards(int activePrimaryShards) {
        this.activePrimaryShards = activePrimaryShards;
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

    public int getDelayedUnassignedShards() {
        return delayedUnassignedShards;
    }

    public void setDelayedUnassignedShards(int delayedUnassignedShards) {
        this.delayedUnassignedShards = delayedUnassignedShards;
    }

    public int getFailedShards() {
        return failedShards;
    }

    public void setFailedShards(int failedShards) {
        this.failedShards = failedShards;
    }

    public int getTotalShards() {
        return totalShards;
    }

    public void setTotalShards(int totalShards) {
        this.totalShards = totalShards;
    }

    public int getActiveShardsPercentAsNumber() {
        return activeShardsPercentAsNumber;
    }

    public void setActiveShardsPercentAsNumber(int activeShardsPercentAsNumber) {
        this.activeShardsPercentAsNumber = activeShardsPercentAsNumber;
    }

    public int getNumberOfPendingTasks() {
        return numberOfPendingTasks;
    }

    public void setNumberOfPendingTasks(int numberOfPendingTasks) {
        this.numberOfPendingTasks = numberOfPendingTasks;
    }

    public int getNumberOfInFlightFetch() {
        return numberOfInFlightFetch;
    }

    public void setNumberOfInFlightFetch(int numberOfInFlightFetch) {
        this.numberOfInFlightFetch = numberOfInFlightFetch;
    }

    public int getTaskMaxWaitingInQueueMillis() {
        return taskMaxWaitingInQueueMillis;
    }

    public void setTaskMaxWaitingInQueueMillis(int taskMaxWaitingInQueueMillis) {
        this.taskMaxWaitingInQueueMillis = taskMaxWaitingInQueueMillis;
    }

    public Map<HealthState, Integer> getNodesByHealth() {
        return nodesByHealth;
    }

    public void setNodesByHealth(Map<HealthState, Integer> nodesByHealth) {
        this.nodesByHealth = nodesByHealth;
    }

    public Map<String, IndexHealthInfo> getIndices() {
        return indices;
    }

    public void setIndices(Map<String, IndexHealthInfo> indices) {
        this.indices = indices;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (clusterName != null) {
            builder.field("cluster_name", clusterName);
        }
        if (status != null) {
            builder.field("status", status.getValue());
        }
        builder.field("timed_out", timedOut);
        builder.field("number_of_nodes", numberOfNodes);
        builder.field("number_of_data_nodes", numberOfDataNodes);
        builder.field("number_of_coordinator_nodes", numberOfCoordinatorNodes);
        builder.field("active_nodes", activeNodes);
        builder.field("number_of_indices", numberOfIndices);
        builder.field("active_primary_shards", activePrimaryShards);
        builder.field("active_shards", activeShards);
        builder.field("relocating_shards", relocatingShards);
        builder.field("initializing_shards", initializingShards);
        builder.field("unassigned_shards", unassignedShards);
        builder.field("delayed_unassigned_shards", delayedUnassignedShards);
        builder.field("failed_shards", failedShards);
        builder.field("total_shards", totalShards);
        builder.field("active_shards_percent_as_number", activeShardsPercentAsNumber);
        builder.field("number_of_pending_tasks", numberOfPendingTasks);
        builder.field("number_of_in_flight_fetch", numberOfInFlightFetch);
        builder.field("task_max_waiting_in_queue_millis", taskMaxWaitingInQueueMillis);
        if (nodesByHealth != null && !nodesByHealth.isEmpty()) {
            builder.startObject("nodes_by_health");
            for (Map.Entry<HealthState, Integer> entry : nodesByHealth.entrySet()) {
                builder.field(entry.getKey().getValue(), entry.getValue());
            }
            builder.endObject();
        }
        if (indices != null && !indices.isEmpty()) {
            builder.field("indices").map(indices);
        }
        builder.endObject();
        return builder;
    }

    public static ClusterHealthInfo fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        ClusterHealthInfo clusterHealthInfo = new ClusterHealthInfo();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("nodes_by_health".equals(currentFieldName)) {
                    parser.nextToken();
                    Map<HealthState, Integer> nodesByHealth = new HashMap<>();
                    while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                        if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                            String healthStateStr = parser.currentName();
                            parser.nextToken();
                            if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                                nodesByHealth.put(HealthState.fromString(healthStateStr), parser.intValue());
                            }
                        }
                        parser.nextToken();
                    }
                    clusterHealthInfo.nodesByHealth = nodesByHealth;
                } else if ("indices".equals(currentFieldName)) {
                    clusterHealthInfo.indices = parser.map(HashMap::new, IndexHealthInfo::fromXContent);
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if ("cluster_name".equals(currentFieldName)) {
                    clusterHealthInfo.clusterName = parser.text();
                } else if ("status".equals(currentFieldName)) {
                    clusterHealthInfo.status = HealthState.fromString(parser.text());
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_BOOLEAN) {
                if ("timed_out".equals(currentFieldName)) {
                    clusterHealthInfo.timedOut = parser.booleanValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected boolean for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid number, expected field name");
                }
                switch (currentFieldName) {
                    case "number_of_nodes":
                        clusterHealthInfo.numberOfNodes = parser.intValue();
                        break;
                    case "number_of_data_nodes":
                        clusterHealthInfo.numberOfDataNodes = parser.intValue();
                        break;
                    case "number_of_coordinator_nodes":
                        clusterHealthInfo.numberOfCoordinatorNodes = parser.intValue();
                        break;
                    case "active_nodes":
                        clusterHealthInfo.activeNodes = parser.intValue();
                        break;
                    case "number_of_indices":
                        clusterHealthInfo.numberOfIndices = parser.intValue();
                        break;
                    case "active_primary_shards":
                        clusterHealthInfo.activePrimaryShards = parser.intValue();
                        break;
                    case "active_shards":
                        clusterHealthInfo.activeShards = parser.intValue();
                        break;
                    case "relocating_shards":
                        clusterHealthInfo.relocatingShards = parser.intValue();
                        break;
                    case "initializing_shards":
                        clusterHealthInfo.initializingShards = parser.intValue();
                        break;
                    case "unassigned_shards":
                        clusterHealthInfo.unassignedShards = parser.intValue();
                        break;
                    case "delayed_unassigned_shards":
                        clusterHealthInfo.delayedUnassignedShards = parser.intValue();
                        break;
                    case "failed_shards":
                        clusterHealthInfo.failedShards = parser.intValue();
                        break;
                    case "total_shards":
                        clusterHealthInfo.totalShards = parser.intValue();
                        break;
                    case "active_shards_percent_as_number":
                        clusterHealthInfo.activeShardsPercentAsNumber = parser.intValue();
                        break;
                    case "number_of_pending_tasks":
                        clusterHealthInfo.numberOfPendingTasks = parser.intValue();
                        break;
                    case "number_of_in_flight_fetch":
                        clusterHealthInfo.numberOfInFlightFetch = parser.intValue();
                        break;
                    case "task_max_waiting_in_queue_millis":
                        clusterHealthInfo.taskMaxWaitingInQueueMillis = parser.intValue();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return clusterHealthInfo;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClusterHealthInfo that = (ClusterHealthInfo) obj;
        return timedOut == that.timedOut &&
               numberOfNodes == that.numberOfNodes &&
               numberOfDataNodes == that.numberOfDataNodes &&
               numberOfCoordinatorNodes == that.numberOfCoordinatorNodes &&
               activeNodes == that.activeNodes &&
               numberOfIndices == that.numberOfIndices &&
               activePrimaryShards == that.activePrimaryShards &&
               activeShards == that.activeShards &&
               relocatingShards == that.relocatingShards &&
               initializingShards == that.initializingShards &&
               unassignedShards == that.unassignedShards &&
               delayedUnassignedShards == that.delayedUnassignedShards &&
               failedShards == that.failedShards &&
               totalShards == that.totalShards &&
               activeShardsPercentAsNumber == that.activeShardsPercentAsNumber &&
               numberOfPendingTasks == that.numberOfPendingTasks &&
               numberOfInFlightFetch == that.numberOfInFlightFetch &&
               taskMaxWaitingInQueueMillis == that.taskMaxWaitingInQueueMillis &&
               java.util.Objects.equals(clusterName, that.clusterName) &&
               status == that.status &&
               java.util.Objects.equals(nodesByHealth, that.nodesByHealth) &&
               java.util.Objects.equals(indices, that.indices);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(clusterName, status, timedOut, numberOfNodes, numberOfDataNodes,
                numberOfCoordinatorNodes, activeNodes, numberOfIndices, activePrimaryShards, activeShards,
                relocatingShards, initializingShards, unassignedShards, delayedUnassignedShards, failedShards,
                totalShards, activeShardsPercentAsNumber, numberOfPendingTasks, numberOfInFlightFetch,
                taskMaxWaitingInQueueMillis, nodesByHealth, indices);
    }
}
