package org.opensearch.cluster.controller.tasks.impl;

import org.opensearch.cluster.controller.tasks.Task;
import org.opensearch.cluster.controller.tasks.TaskContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.opensearch.cluster.controller.config.Constants.*;

/**
 * Task implementation for discovering search units from etcd.
 * This task regularly scans etcd for actual-state updates and updates the SearchUnit inventory.
 */
public class DiscoveryTask implements Task {
    private static final Logger log = LogManager.getLogger(DiscoveryTask.class);
    
    private final String name;
    private final int priority;
    private final String input;
    private final String schedule;
    
    public DiscoveryTask(String name, int priority, String input, String schedule) {
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
        log.info("Executing discovery task: {} for cluster: {}", name, clusterId);
        
        try {
            context.getDiscovery().discoverSearchUnits(clusterId);
            log.info("Discovery task completed successfully for cluster: {}", clusterId);
            return TASK_STATUS_COMPLETED;
        } catch (Exception e) {
            log.error("Failed to execute discovery task for cluster {}: {}", clusterId, e.getMessage(), e);
            return TASK_STATUS_FAILED;
        }
    }
}

