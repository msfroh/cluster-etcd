/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensearch.cluster.controller.models.TaskMetadata;
import org.opensearch.cluster.controller.store.MetadataStore;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.threadpool.ExecutorBuilder;
import org.opensearch.threadpool.FixedExecutorBuilder;
import org.opensearch.threadpool.Scheduler;
import org.opensearch.threadpool.ThreadPool;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.opensearch.cluster.controller.config.Constants.TASK_ACTION_ACTUAL_ALLOCATION_UPDATER;
import static org.opensearch.cluster.controller.config.Constants.TASK_ACTION_DISCOVERY;
import static org.opensearch.cluster.controller.config.Constants.TASK_ACTION_GOAL_STATE_ORCHESTRATOR;
import static org.opensearch.cluster.controller.config.Constants.TASK_ACTION_SHARD_ALLOCATOR;
import static org.opensearch.cluster.controller.config.Constants.TASK_SCHEDULE_REPEAT;
import static org.opensearch.cluster.controller.config.Constants.TASK_STATUS_FAILED;
import static org.opensearch.cluster.controller.config.Constants.TASK_STATUS_PENDING;
import static org.opensearch.cluster.controller.config.Constants.TASK_STATUS_RUNNING;

/**
 * Generic task manager for scheduling and executing tasks.
 * Agnostic to specific task types - delegates execution to Task implementations.
 */
public class TaskManager {
    private static final Logger log = LogManager.getLogger(TaskManager.class);

    private final MetadataStore metadataStore;
    private final TaskContext taskContext;
    private final String clusterName;

    private final ThreadPool threadPool;
    private final long intervalSeconds;
    private boolean isRunning = false;
    private Scheduler.Cancellable taskLoopCancellable;

    public TaskManager(
        MetadataStore metadataStore,
        TaskContext taskContext,
        String clusterName,
        long intervalSeconds,
        ThreadPool threadPool
    ) {
        this.metadataStore = metadataStore;
        this.taskContext = taskContext;
        this.clusterName = clusterName;
        this.intervalSeconds = intervalSeconds;
        this.threadPool = threadPool;
    }

    public static final String THREAD_POOL_NAME = "etcd-controller-task-manager";

    public static ExecutorBuilder<?> createExecutorBuilder(Settings settings) {
        return new FixedExecutorBuilder(settings, THREAD_POOL_NAME, 1, 100, THREAD_POOL_NAME);
    }

    public TaskMetadata createTask(String taskName, String input, int priority) {
        log.info("Creating task: name={}, priority={}", taskName, priority);
        TaskMetadata taskMetadata = new TaskMetadata(taskName, priority);
        taskMetadata.setInput(input);
        try {
            metadataStore.createTask(clusterName, taskMetadata);
        } catch (Exception e) {
            log.error("Failed to create task: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create task", e);
        }
        return taskMetadata;
    }

    public List<TaskMetadata> getAllTasks() {
        log.debug("Getting all tasks");
        try {
            return metadataStore.getAllTasks(clusterName);
        } catch (Exception e) {
            log.error("Failed to get all tasks: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get tasks", e);
        }
    }

    public Optional<TaskMetadata> getTask(String taskName) {
        log.debug("Getting task: {}", taskName);
        try {
            return metadataStore.getTask(clusterName, taskName);
        } catch (Exception e) {
            log.error("Failed to get task {}: {}", taskName, e.getMessage(), e);
            throw new RuntimeException("Failed to get task", e);
        }
    }

    public void updateTask(TaskMetadata taskMetadata) {
        log.debug("Updating task: {}", taskMetadata.getName());
        try {
            metadataStore.updateTask(clusterName, taskMetadata);
        } catch (Exception e) {
            log.error("Failed to update task {}: {}", taskMetadata.getName(), e.getMessage(), e);
            throw new RuntimeException("Failed to update task", e);
        }
    }

    public void deleteTask(String taskName) {
        log.info("Deleting task: {}", taskName);
        try {
            metadataStore.deleteTask(clusterName, taskName);
        } catch (Exception e) {
            log.error("Failed to delete task {}: {}", taskName, e.getMessage(), e);
            throw new RuntimeException("Failed to delete task", e);
        }
    }

    public void start() {
        log.info("[Cluster: {}] Starting task manager", clusterName);

        // Bootstrap standard recurring tasks if they don't exist
        bootstrapRecurringTasks();

        isRunning = true;
        taskLoopCancellable = threadPool.scheduleWithFixedDelay(
            this::processTaskLoop,
            TimeValue.timeValueSeconds(intervalSeconds),
            THREAD_POOL_NAME
        );
    }

    /**
     * Bootstrap standard recurring tasks needed for normal cluster operation.
     * These tasks are created automatically if they don't already exist.
     */
    private void bootstrapRecurringTasks() {
        log.info("[Cluster: {}] Bootstrapping recurring tasks", clusterName);

        try {
            // 1. Discovery task - discovers search units from actual-state (highest priority)
            ensureRecurringTask(TASK_ACTION_DISCOVERY, 1, "Discover search units from etcd");

            // 2. Shard Allocator - plans shard allocation using USE_ALL_AVAILABLE_NODES strategy (bin-packing)
            ensureRecurringTask(TASK_ACTION_SHARD_ALLOCATOR, 2, "Run shard allocator with bin-packing");

            // 3. Goal State Orchestrator - orchestrates goal states to search units
            ensureRecurringTask(TASK_ACTION_GOAL_STATE_ORCHESTRATOR, 3, "Orchestrate goal states to search units");

            // 4. Actual Allocation Updater - aggregates actual states into actual allocations and coordinator routing
            ensureRecurringTask(TASK_ACTION_ACTUAL_ALLOCATION_UPDATER, 4, "Update actual allocations and coordinator goal states");

            log.info("[Cluster: {}] Successfully bootstrapped recurring tasks", clusterName);
        } catch (Exception e) {
            log.error("[Cluster: {}] Failed to bootstrap recurring tasks: {}", clusterName, e.getMessage(), e);
            throw new RuntimeException("Failed to bootstrap recurring tasks", e);
        }
    }

    /**
     * Ensure a recurring task exists, creating it if necessary.
     */
    private void ensureRecurringTask(String taskName, int priority, String description) {
        try {
            if (getTask(taskName).isPresent()) {
                log.debug("[Cluster: {}] Task {} already exists", clusterName, taskName);
                return;
            }

            // Create recurring task
            TaskMetadata task = new TaskMetadata(taskName, priority);
            task.setSchedule(TASK_SCHEDULE_REPEAT);
            task.setInput(description);

            metadataStore.createTask(clusterName, task);
            log.info("[Cluster: {}] Created recurring task: {} (priority: {})", clusterName, taskName, priority);
        } catch (Exception e) {
            log.error("[Cluster: {}] Failed to ensure recurring task {}: {}", clusterName, taskName, e.getMessage());
        }
    }

    public void stop() {
        log.info("[Cluster: {}] Stopping task manager", clusterName);
        isRunning = false;
        if (taskLoopCancellable != null) {
            taskLoopCancellable.cancel();
        }
        // NOTE: Do NOT close metadataStore here - it's a shared resource used by all TaskManagers
        // The metadataStore will be closed when the application shuts down
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void processTaskLoop() {
        try {
            // TODO: Leader check disabled for multi-cluster mode
            // In multi-cluster mode, MultiClusterManager handles cluster ownership via distributed locks
            // If reverting to single-cluster mode, uncomment the following:
            // if (!metadataStore.isLeader()) {
            // log.debug("Skipping task processing - not the leader");
            // return;
            // }

            log.info("[Cluster: {}] Running task processing loop - checking for tasks", clusterName);

            List<TaskMetadata> taskMetadataList = getAllTasks();
            log.info("[Cluster: {}] Found {} tasks in etcd", clusterName, taskMetadataList.size());
            for (TaskMetadata task : taskMetadataList) {
                log.info(
                    "[Cluster: {}] Task: {} status: {} priority: {}",
                    clusterName,
                    task.getName(),
                    task.getStatus(),
                    task.getPriority()
                );
            }

            cleanupOldTasks(taskMetadataList);

            TaskMetadata taskMetadataToProcess = selectNextTask(taskMetadataList);
            if (taskMetadataToProcess != null) {
                log.info("[Cluster: {}] Processing task: {}", clusterName, taskMetadataToProcess.getName());
                String result = executeTask(taskMetadataToProcess);
                log.info("[Cluster: {}] Task {} completed with result: {}", clusterName, taskMetadataToProcess.getName(), result);
            } else {
                log.info("[Cluster: {}] No pending tasks to process", clusterName);
            }
        } catch (Exception e) {
            log.error("[Cluster: {}] Error in task processing loop: {}", clusterName, e.getMessage(), e);
        }
    }

    private String executeTask(TaskMetadata taskMetadata) {
        try {
            taskMetadata.setStatus(TASK_STATUS_RUNNING);
            updateTask(taskMetadata);

            log.info("Executing task: {}", taskMetadata.getName());

            // Create Task implementation from metadata and execute
            Task task = TaskFactory.createTask(taskMetadata);
            String result = task.execute(taskContext, clusterName);

            // Make repeat tasks eligible again by resetting status to pending
            if (TASK_SCHEDULE_REPEAT.equalsIgnoreCase(taskMetadata.getSchedule())) {
                taskMetadata.setStatus(TASK_STATUS_PENDING);
            } else {
                taskMetadata.setStatus(result);
            }

            // Update timestamp so task selection considers recency
            taskMetadata.setLastUpdated(OffsetDateTime.now(ZoneOffset.UTC));
            updateTask(taskMetadata);
            return result;

        } catch (Exception e) {
            log.error("Failed to execute task {}: {}", taskMetadata.getName(), e.getMessage(), e);
            taskMetadata.setStatus(TASK_STATUS_FAILED);
            try {
                updateTask(taskMetadata);
            } catch (Exception updateException) {
                log.error("Failed to update task status to failed: {}", updateException.getMessage());
            }
            return TASK_STATUS_FAILED;
        }
    }

    private TaskMetadata selectNextTask(List<TaskMetadata> tasks) {
        // TODO: Implement advanced task selection logic based on priority and lastUpdated

        // Select tasks based on "effective time" = lastUpdated + priority weight
        // This allows repeat tasks to alternate naturally based on priority + age
        // Lower effective time = higher priority (should run sooner)
        return tasks.stream()
            .filter(t -> TASK_SCHEDULE_REPEAT.equals(t.getSchedule()) || TASK_STATUS_PENDING.equals(t.getStatus()))
            .min(Comparator.comparingLong(t -> {
                long lastUpdated = t.getLastUpdated() != null ? t.getLastUpdated().toInstant().toEpochMilli() : 0;
                // Weight priority: higher priority (lower number) = run sooner
                // Each priority level adds 1 second (1000ms) delay
                return lastUpdated + t.getPriority() * 1000L;
            }))
            .orElse(null);
    }

    private void cleanupOldTasks(List<TaskMetadata> tasks) {
        // TODO: Implement task cleanup logic
        log.debug("Cleaning up old tasks");
    }
}
