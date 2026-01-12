package org.opensearch.cluster.controller.allocation;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensearch.cluster.controller.allocation.GroupSelectionStrategy;
import org.opensearch.cluster.controller.enums.NodeRole;
import org.opensearch.cluster.controller.models.NodesGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Random group selection strategy.
 * 
 * Selects N random groups from available groups to ensure even distribution
 * and avoid hot spots when multiple indices are being allocated simultaneously.
 */
public class RandomGroupSelectionStrategy implements GroupSelectionStrategy {
    private static final Logger log = LogManager.getLogger(RandomGroupSelectionStrategy.class);

    
    private final Random random = new Random();
    
    @Override
    public List<NodesGroup> selectGroups(List<NodesGroup> groups, NodeRole targetRole, int numGroupsNeeded) {
        if (groups.isEmpty()) {
            log.warn("Random strategy: no groups available for role={}", targetRole);
            return List.of();
        }
        
        // Shuffle groups to randomize selection
        List<NodesGroup> shuffled = new ArrayList<>(groups);
        Collections.shuffle(shuffled, random);
        
        // Select up to numGroupsNeeded groups
        List<NodesGroup> selected = shuffled.stream()
            .limit(numGroupsNeeded)
            .collect(Collectors.toList());
        
        log.debug("Random strategy selected {} out of {} available groups for role={}", 
                 selected.size(), groups.size(), targetRole);
        
        return selected;
    }
    
    @Override
    public String getStrategyName() {
        return "Random";
    }
}

