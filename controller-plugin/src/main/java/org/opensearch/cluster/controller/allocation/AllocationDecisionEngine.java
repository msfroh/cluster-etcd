package org.opensearch.cluster.controller.allocation;

import org.opensearch.cluster.controller.enums.NodeRole;
import org.opensearch.cluster.controller.models.Index;
import org.opensearch.cluster.controller.models.SearchUnit;
import org.opensearch.cluster.controller.models.ShardAllocation;

import java.util.List;

/**
 * Engine that decides which nodes are eligible for shard allocation.
 * 
 * Different implementations can use different strategies:
 * - StandardAllocationEngine: Uses deciders (current behavior)
 * - GroupAwareBinPackingEngine: Uses group-based bin-packing (future)
 */
public interface AllocationDecisionEngine {
    
    /**
     * Get available nodes for allocation.
     * 
     * @param shardId Shard ID (0, 1, 2, ...)
     * @param indexName Index name
     * @param indexConfig Index configuration
     * @param allNodes All nodes in the cluster
     * @param targetRole Target role (PRIMARY or REPLICA)
     * @param currentPlanned Current planned allocation (may be null if no existing allocation)
     * @return List of eligible nodes
     */
    List<SearchUnit> getAvailableNodesForAllocation(
        int shardId,
        String indexName,
        Index indexConfig,
        List<SearchUnit> allNodes,
        NodeRole targetRole,
        ShardAllocation currentPlanned
    );
}
