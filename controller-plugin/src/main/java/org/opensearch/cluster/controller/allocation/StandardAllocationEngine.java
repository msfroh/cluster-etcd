package org.opensearch.cluster.controller.allocation;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensearch.cluster.controller.allocation.deciders.AllocationDecider;
import org.opensearch.cluster.controller.allocation.deciders.HealthDecider;
import org.opensearch.cluster.controller.allocation.deciders.RoleDecider;
import org.opensearch.cluster.controller.allocation.deciders.ShardPoolDecider;
import org.opensearch.cluster.controller.enums.Decision;
import org.opensearch.cluster.controller.enums.NodeRole;
import org.opensearch.cluster.controller.models.Index;
import org.opensearch.cluster.controller.models.SearchUnit;
import org.opensearch.cluster.controller.models.ShardAllocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Standard allocation engine that uses deciders to filter eligible nodes.
 * 
 * This is the current/existing allocation logic extracted into its own class.
 * Uses: RoleDecider, ShardPoolDecider, HealthDecider
 * (Note: AllNodesDecider removed as it always returns YES - useless)
 */
public class StandardAllocationEngine implements AllocationDecisionEngine {
    private static final Logger log = LogManager.getLogger(StandardAllocationEngine.class);

    
    private final List<AllocationDecider> deciders;
    
    public StandardAllocationEngine() {
        // Fixed list of deciders (simplified from old dynamic enable/disable approach)
        this.deciders = Arrays.asList(
            new RoleDecider(),
            new ShardPoolDecider(),
            new HealthDecider()
            // Note: Removed AllNodesDecider - it always returns YES, so it's useless
        );
    }
    
    @Override
    public List<SearchUnit> getAvailableNodesForAllocation(
        int shardId,
        String indexName,
        Index indexConfig,
        List<SearchUnit> allNodes,
        NodeRole targetRole,
        ShardAllocation currentPlanned
    ) {
        // StandardAllocationEngine doesn't use currentPlanned - it always recomputes
        // (This is the existing behavior - no stable allocation logic)
        
        List<SearchUnit> selectedNodes = new ArrayList<>();
        
        // Convert shardId to String for deciders
        String shardIdStr = String.valueOf(shardId);
        
        for (SearchUnit node : allNodes) {
            Decision finalDecision = Decision.YES;
            
            for (AllocationDecider decider : deciders) {
                if (!decider.isEnabled()) continue;
                
                Decision deciderResult = decider.canAllocate(shardIdStr, node, indexName, targetRole);
                finalDecision = finalDecision.merge(deciderResult);
                
                if (finalDecision == Decision.NO) break;
            }
            
            if (finalDecision == Decision.YES) {
                selectedNodes.add(node);
            }
        }
        
        return selectedNodes;
    }
}


