package org.opensearch.cluster.controller.tasks.impl;

import org.opensearch.cluster.controller.tasks.Task;
import org.opensearch.cluster.controller.tasks.TaskContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.opensearch.cluster.controller.config.Constants.*;

/**
 * Task implementation for unknown/unsupported task types.
 */
public class UnknownTask implements Task {
    private static final Logger log = LogManager.getLogger(UnknownTask.class);
    
    private final String name;
    private final int priority;
    private final String input;
    private final String schedule;
    
    public UnknownTask(String name, int priority, String input, String schedule) {
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
        log.warn("Executing unknown task type: {} for cluster: {}", name, clusterId);
        return TASK_STATUS_FAILED;
    }
}
