package org.opensearch.cluster.controller.allocation.deciders;

import org.opensearch.cluster.controller.enums.Decision;
import org.opensearch.cluster.controller.enums.NodeRole;
import org.opensearch.cluster.controller.models.SearchUnit;

/**
 * Decider that filters nodes based on shard pool assignment.
 * 
 * Only allows allocation to nodes where node.getShardId() matches the target shardId.
 */
public class ShardPoolDecider implements AllocationDecider {
    private boolean enabled = true;
    
    @Override
    public Decision canAllocate(String shardId, SearchUnit node, String indexName, NodeRole targetRole) {
        String nodeShardId = node.getShardId();
        return (nodeShardId != null && nodeShardId.equals(shardId)) ? Decision.YES : Decision.NO;
    }
    
    @Override
    public String getName() { return "ShardPoolDecider"; }
    
    @Override
    public boolean isEnabled() { return enabled; }
    
    @Override
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
