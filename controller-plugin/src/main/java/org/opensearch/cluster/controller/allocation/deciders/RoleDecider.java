package org.opensearch.cluster.controller.allocation.deciders;

import org.opensearch.cluster.controller.enums.Decision;
import org.opensearch.cluster.controller.enums.NodeRole;
import org.opensearch.cluster.controller.models.SearchUnit;

/**
 * Decider that filters nodes based on strict role matching.
 * 
 * PRIMARY target: only PRIMARY nodes. REPLICA target: only REPLICA nodes.
 * Always rejects COORDINATOR nodes.
 */
public class RoleDecider implements AllocationDecider {
    private boolean enabled = true;
    
    @Override
    public Decision canAllocate(String shardId, SearchUnit node, String indexName, NodeRole targetRole) {
        String nodeRoleString = node.getRole();
        if (nodeRoleString == null) return Decision.NO;
        
        NodeRole nodeRole = NodeRole.fromString(nodeRoleString);
        if (nodeRole == null) return Decision.NO;
        
        if (nodeRole == NodeRole.COORDINATOR) return Decision.NO;
        
        if (targetRole == NodeRole.PRIMARY) {
            return nodeRole == NodeRole.PRIMARY ? Decision.YES : Decision.NO;
        }
        
        if (targetRole == NodeRole.REPLICA) {
            return nodeRole == NodeRole.REPLICA ? Decision.YES : Decision.NO;
        }
        
        return Decision.NO;
    }
    
    @Override
    public String getName() { return "RoleDecider"; }
    
    @Override
    public boolean isEnabled() { return enabled; }
    
    @Override
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
