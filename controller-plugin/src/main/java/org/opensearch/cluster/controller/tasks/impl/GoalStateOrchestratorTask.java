package org.opensearch.cluster.controller.tasks.impl;

import org.opensearch.cluster.controller.tasks.Task;
import org.opensearch.cluster.controller.tasks.TaskContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.opensearch.cluster.controller.config.Constants.*;

/**
 * Task to execute goal state orchestration
 */
public class GoalStateOrchestratorTask implements Task {
    private static final Logger log = LogManager.getLogger(GoalStateOrchestratorTask.class);
    
    private final String name;
    private final int priority;
    private final String input;
    private final String schedule;
    
    public GoalStateOrchestratorTask(String name, int priority, String input, String schedule) {
        this.name = name;
        this.priority = priority;
        this.input = input;
        this.schedule = schedule;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public int getPriority() {
        return priority;
    }
    
    @Override
    public String getInput() {
        return input;
    }
    
    @Override
    public String getSchedule() {
        return schedule;
    }
    
    @Override
    public String execute(TaskContext context, String clusterId) {
        log.info("Executing goal state orchestrator task: {} for cluster: {}", name, clusterId);
        
        try {
            context.getGoalStateOrchestrator().orchestrateGoalStates(clusterId);
            return TASK_STATUS_COMPLETED;
        } catch (Exception e) {
            log.error("Failed to execute goal state orchestrator task for cluster {}: {}", clusterId, e.getMessage(), e);
            return TASK_STATUS_FAILED;
        }
    }
}
