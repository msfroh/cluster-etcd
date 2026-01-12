package org.opensearch.cluster.controller.allocation.deciders;

import org.opensearch.cluster.controller.enums.Decision;
import org.opensearch.cluster.controller.enums.NodeRole;
import org.opensearch.cluster.controller.models.SearchUnit;

/**
 * Simple decider that accepts all nodes.
 * 
 * Used as fallback when no specific filtering is needed.
 */
public class AllNodesDecider implements AllocationDecider {
    private boolean enabled = true;
    
    @Override
    public Decision canAllocate(String shardId, SearchUnit node, String indexName, NodeRole targetRole) {
        return Decision.YES;
    }
    
    @Override
    public String getName() { return "AllNodesDecider"; }
    
    @Override
    public boolean isEnabled() { return enabled; }
    
    @Override
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
