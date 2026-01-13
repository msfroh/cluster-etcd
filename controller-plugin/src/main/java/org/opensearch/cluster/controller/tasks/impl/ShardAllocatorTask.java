/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.tasks.impl;

import org.opensearch.cluster.controller.allocation.AllocationStrategy;
import org.opensearch.cluster.controller.tasks.Task;
import org.opensearch.cluster.controller.tasks.TaskContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.opensearch.cluster.controller.config.Constants.TASK_STATUS_COMPLETED;
import static org.opensearch.cluster.controller.config.Constants.TASK_STATUS_FAILED;

/**
 * Task implementation for shard allocation.
 */
public class ShardAllocatorTask implements Task {
    private static final Logger log = LogManager.getLogger(ShardAllocatorTask.class);

    private final String name;
    private final int priority;
    private final String input;
    private final String schedule;

    public ShardAllocatorTask(String name, int priority, String input, String schedule) {
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
        log.info("Executing shard allocator task: {} for cluster: {}", name, clusterId);

        try {
            AllocationStrategy strategy = AllocationStrategy.USE_ALL_AVAILABLE_NODES; // TODO: Pick from config later
            context.getShardAllocator().planShardAllocation(clusterId, strategy);
            return TASK_STATUS_COMPLETED;
        } catch (Exception e) {
            log.error("Failed to execute shard allocator task for cluster {}: {}", clusterId, e.getMessage(), e);
            return TASK_STATUS_FAILED;
        }
    }
}
