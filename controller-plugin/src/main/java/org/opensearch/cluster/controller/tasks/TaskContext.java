package org.opensearch.cluster.controller.tasks;

import org.opensearch.cluster.controller.allocation.ActualAllocationUpdater;
import org.opensearch.cluster.controller.allocation.ShardAllocator;
import org.opensearch.cluster.controller.discovery.Discovery;
import org.opensearch.cluster.controller.orchestration.GoalStateOrchestrator;

/**
 * Context object providing access to shared components for task execution.
 * 
 * This is a singleton that contains stateless or cluster-agnostic services.
 * The cluster-specific context (clusterId) is passed separately to task execution.
 * 
 * TODO: IndexManager was removed - needs to be reimplemented without Jackson dependencies
 */
public class TaskContext {
    
    private final ShardAllocator shardAllocator;
    private final ActualAllocationUpdater actualAllocationUpdater;
    private final GoalStateOrchestrator goalStateOrchestrator;
    private final Discovery discovery;
    
    public TaskContext(ShardAllocator shardAllocator, ActualAllocationUpdater actualAllocationUpdater,
                       GoalStateOrchestrator goalStateOrchestrator, Discovery discovery) {
        this.shardAllocator = shardAllocator;
        this.actualAllocationUpdater = actualAllocationUpdater;
        this.goalStateOrchestrator = goalStateOrchestrator;
        this.discovery = discovery;
    }
    
    public ShardAllocator getShardAllocator() {
        return shardAllocator;
    }
    
    public ActualAllocationUpdater getActualAllocationUpdater() {
        return actualAllocationUpdater;
    }
    
    public GoalStateOrchestrator getGoalStateOrchestrator() {
        return goalStateOrchestrator;
    }
    
    public Discovery getDiscovery() {
        return discovery;
    }
}




