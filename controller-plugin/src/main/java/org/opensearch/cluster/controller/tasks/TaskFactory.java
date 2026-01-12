package org.opensearch.cluster.controller.tasks;

import org.opensearch.cluster.controller.models.TaskMetadata;
import org.opensearch.cluster.controller.tasks.Task;
import org.opensearch.cluster.controller.tasks.impl.ActualAllocationUpdaterTask;
import org.opensearch.cluster.controller.tasks.impl.DiscoveryTask;
import org.opensearch.cluster.controller.tasks.impl.GoalStateOrchestratorTask;
import org.opensearch.cluster.controller.tasks.impl.ShardAllocatorTask;
import org.opensearch.cluster.controller.tasks.impl.UnknownTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.opensearch.cluster.controller.config.Constants.*;

/**
 * Factory for creating Task implementations from TaskMetadata.
 */
public class TaskFactory {
    private static final Logger log = LogManager.getLogger(TaskFactory.class);
    
    /**
     * Create a Task implementation from TaskMetadata
     */
    public static Task createTask(TaskMetadata metadata) {
        String taskName = metadata.getName();
        
        return switch (taskName) {
            case TASK_ACTION_DISCOVERY -> new DiscoveryTask(
                metadata.getName(),
                metadata.getPriority(),
                metadata.getInput(),
                metadata.getSchedule()
            );
            case TASK_ACTION_SHARD_ALLOCATOR -> new ShardAllocatorTask(
                metadata.getName(),
                metadata.getPriority(),
                metadata.getInput(),
                metadata.getSchedule()
            );
            case TASK_ACTION_ACTUAL_ALLOCATION_UPDATER -> new ActualAllocationUpdaterTask(
                metadata.getName(),
                metadata.getPriority(),
                metadata.getInput(),
                metadata.getSchedule()
            );
            case TASK_ACTION_GOAL_STATE_ORCHESTRATOR -> new GoalStateOrchestratorTask(
                metadata.getName(),
                metadata.getPriority(),
                metadata.getInput(),
                metadata.getSchedule()
            );
            default -> {
                log.warn("Unknown task type: {}", taskName);
                yield new UnknownTask(metadata.getName(), metadata.getPriority(), metadata.getInput(), metadata.getSchedule());
            }
        };
    }
}



