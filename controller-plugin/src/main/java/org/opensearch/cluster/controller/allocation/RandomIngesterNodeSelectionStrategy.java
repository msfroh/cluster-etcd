package org.opensearch.cluster.controller.allocation;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensearch.cluster.controller.allocation.IngesterNodeSelectionStrategy;
import org.opensearch.cluster.controller.models.NodesGroup;
import org.opensearch.cluster.controller.models.SearchUnit;
import java.util.List;
import java.util.Random;

/**
 * Random node selection for ingester allocation.
 * 
 * Simply picks a random node from the eligible list.
 */
public class RandomIngesterNodeSelectionStrategy implements IngesterNodeSelectionStrategy {
    private static final Logger log = LogManager.getLogger(RandomIngesterNodeSelectionStrategy.class);

    
    private final Random random;
    
    public RandomIngesterNodeSelectionStrategy() {
        this.random = new Random();
    }
    
    /**
     * Constructor with seed for testing purposes.
     * Allows deterministic random selection in tests.
     */
    public RandomIngesterNodeSelectionStrategy(long seed) {
        this.random = new Random(seed);
    }
    
    @Override
    public SearchUnit selectNode(
        List<SearchUnit> eligibleNodes,
        NodesGroup group,
        String shardId,
        String indexName
    ) {
        if (eligibleNodes == null || eligibleNodes.isEmpty()) {
            log.warn("RandomIngesterNodeSelection: No eligible nodes to select from for group {} shard {}/{}", 
                    group.getGroupId(), indexName, shardId);
            return null;
        }
        
        // Randomly pick one node
        int randomIndex = random.nextInt(eligibleNodes.size());
        SearchUnit selectedNode = eligibleNodes.get(randomIndex);
        
        log.info("RandomIngesterNodeSelection: Selected node {} (index {}/{}) from group {} (zone={})", 
                selectedNode.getName(), randomIndex, eligibleNodes.size(), 
                group.getGroupId(), selectedNode.getZone());
        
        return selectedNode;
    }
    
    @Override
    public String getStrategyName() {
        return "Random";
    }
}

