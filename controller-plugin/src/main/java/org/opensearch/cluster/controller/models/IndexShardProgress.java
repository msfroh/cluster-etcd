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
import java.util.List;

/**
 * IndexShardProgress tracks the progress of an index-shard during rolling updates
 */
public class IndexShardProgress implements ToXContentObject {

    private String indexShard; // e.g., "index1/shard0"
    private int goalStateUpdatedCount; // Number of nodes with goal state updated
    private int actualStateConvergedCount; // Number of nodes with actual state converged
    private int totalNodes; // Total number of nodes for this index-shard
    private List<String> updatedNodes = new ArrayList<>(); // Nodes whose goal state has been updated

    public IndexShardProgress() {}

    public IndexShardProgress(String indexShard, int goalStateUpdatedCount, int actualStateConvergedCount, int totalNodes) {
        this.indexShard = indexShard;
        this.goalStateUpdatedCount = goalStateUpdatedCount;
        this.actualStateConvergedCount = actualStateConvergedCount;
        this.totalNodes = totalNodes;
        this.updatedNodes = new ArrayList<>();
    }

    public IndexShardProgress(
        String indexShard,
        int goalStateUpdatedCount,
        int actualStateConvergedCount,
        int totalNodes,
        List<String> updatedNodes
    ) {
        this.indexShard = indexShard;
        this.goalStateUpdatedCount = goalStateUpdatedCount;
        this.actualStateConvergedCount = actualStateConvergedCount;
        this.totalNodes = totalNodes;
        this.updatedNodes = updatedNodes;
    }

    public String getIndexShard() {
        return indexShard;
    }

    public void setIndexShard(String indexShard) {
        this.indexShard = indexShard;
    }

    public int getGoalStateUpdatedCount() {
        return goalStateUpdatedCount;
    }

    public void setGoalStateUpdatedCount(int goalStateUpdatedCount) {
        this.goalStateUpdatedCount = goalStateUpdatedCount;
    }

    public int getActualStateConvergedCount() {
        return actualStateConvergedCount;
    }

    public void setActualStateConvergedCount(int actualStateConvergedCount) {
        this.actualStateConvergedCount = actualStateConvergedCount;
    }

    public int getTotalNodes() {
        return totalNodes;
    }

    public void setTotalNodes(int totalNodes) {
        this.totalNodes = totalNodes;
    }

    public List<String> getUpdatedNodes() {
        return updatedNodes;
    }

    public void setUpdatedNodes(List<String> updatedNodes) {
        this.updatedNodes = updatedNodes;
    }

    /**
     * Get the number of nodes currently in transit (goal updated but not converged)
     */
    public int getTransitNodesCount() {
        return goalStateUpdatedCount - actualStateConvergedCount;
    }

    /**
     * Get the percentage of nodes currently in transit
     */
    public double getTransitPercentage() {
        return totalNodes > 0 ? (double) getTransitNodesCount() / totalNodes : 0.0;
    }

    /**
     * Check if all nodes have converged
     */
    public boolean isConverged() {
        return goalStateUpdatedCount == totalNodes && actualStateConvergedCount == totalNodes;
    }

    /**
     * Check if we can update more nodes (under the transit limit)
     */
    public boolean canUpdateMoreNodes(double maxTransitPercentage) {
        return getTransitPercentage() < maxTransitPercentage;
    }

    /**
     * Get the number of available slots for updates
     */
    public int getAvailableSlots(double maxTransitPercentage) {
        int maxTransitNodes = (int) Math.ceil(totalNodes * maxTransitPercentage);
        int currentTransitNodes = getTransitNodesCount();
        return Math.max(0, maxTransitNodes - currentTransitNodes);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (indexShard != null) {
            builder.field("indexShard", indexShard);
        }
        builder.field("goalStateUpdatedCount", goalStateUpdatedCount);
        builder.field("actualStateConvergedCount", actualStateConvergedCount);
        builder.field("totalNodes", totalNodes);
        if (updatedNodes != null && !updatedNodes.isEmpty()) {
            builder.field("updatedNodes", updatedNodes);
        }
        builder.endObject();
        return builder;
    }

    public static IndexShardProgress fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        IndexShardProgress indexShardProgress = new IndexShardProgress();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                if ("updatedNodes".equals(currentFieldName)) {
                    List<String> updatedNodes = new ArrayList<>();
                    parser.nextToken();
                    while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                        if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                            updatedNodes.add(parser.text());
                        }
                        parser.nextToken();
                    }
                    indexShardProgress.updatedNodes = updatedNodes;
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected array for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if ("indexShard".equals(currentFieldName)) {
                    indexShardProgress.indexShard = parser.text();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid number, expected field name");
                }
                switch (currentFieldName) {
                    case "goalStateUpdatedCount":
                        indexShardProgress.goalStateUpdatedCount = parser.intValue();
                        break;
                    case "actualStateConvergedCount":
                        indexShardProgress.actualStateConvergedCount = parser.intValue();
                        break;
                    case "totalNodes":
                        indexShardProgress.totalNodes = parser.intValue();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return indexShardProgress;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IndexShardProgress that = (IndexShardProgress) obj;
        return goalStateUpdatedCount == that.goalStateUpdatedCount
            && actualStateConvergedCount == that.actualStateConvergedCount
            && totalNodes == that.totalNodes
            && java.util.Objects.equals(indexShard, that.indexShard)
            && java.util.Objects.equals(updatedNodes, that.updatedNodes);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(indexShard, goalStateUpdatedCount, actualStateConvergedCount, totalNodes, updatedNodes);
    }
}
