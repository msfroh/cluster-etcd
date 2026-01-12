package org.opensearch.cluster.controller.allocation.deciders;

import org.opensearch.cluster.controller.config.Constants;
import org.opensearch.cluster.controller.enums.Decision;
import org.opensearch.cluster.controller.enums.HealthState;
import org.opensearch.cluster.controller.enums.NodeRole;
import org.opensearch.cluster.controller.models.SearchUnit;

/**
 * Decider that filters nodes based on health status.
 * 
 * Rejects nodes with stateAdmin != "NORMAL" or statePulled == RED.
 */
public class HealthDecider implements AllocationDecider {
    private boolean enabled = true;
    
    @Override
    public Decision canAllocate(String shardId, SearchUnit node, String indexName, NodeRole targetRole) {
        String stateAdmin = node.getStateAdmin();
        HealthState statePulled = node.getStatePulled();
        
        if (stateAdmin == null || !Constants.ADMIN_STATE_NORMAL.equalsIgnoreCase(stateAdmin)) {
            return Decision.NO;
        }
        
        if (statePulled == HealthState.RED) {
            return Decision.NO;
        }
        
        return Decision.YES;
    }
    
    @Override
    public String getName() { return "HealthDecider"; }
    
    @Override
    public boolean isEnabled() { return enabled; }
    
    @Override
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
