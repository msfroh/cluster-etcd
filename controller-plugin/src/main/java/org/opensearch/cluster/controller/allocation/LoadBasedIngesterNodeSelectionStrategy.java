/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.allocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensearch.cluster.controller.models.NodesGroup;
import org.opensearch.cluster.controller.models.SearchUnit;
import java.util.List;

/**
 * Load-based node selection for ingester allocation.
 *
 * TODO: NOT YET IMPLEMENTED
 *
 * When implemented, this should:
 * - Count shards allocated to each node in the eligible list
 * - Pick the node with the lowest load (least shards)
 * - Break ties randomly or by node name (lexicographic)
 */
public class LoadBasedIngesterNodeSelectionStrategy implements IngesterNodeSelectionStrategy {
    private static final Logger log = LogManager.getLogger(LoadBasedIngesterNodeSelectionStrategy.class);

    @Override
    public SearchUnit selectNode(List<SearchUnit> eligibleNodes, NodesGroup group, String shardId, String indexName) {
        log.error("LoadBasedIngesterNodeSelection is not yet implemented. Returning null.");
        // TODO: Implement load-based selection:
        // 1. Query actual state for each eligible node
        // 2. Count shards per node
        // 3. Pick node with lowest load
        // 4. Break ties deterministically (e.g., lexicographic by node name)
        return null;
    }

    @Override
    public String getStrategyName() {
        return "LoadBased";
    }
}
