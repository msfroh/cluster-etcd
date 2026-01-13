/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.orchestration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensearch.cluster.controller.metrics.MetricsProvider;
import org.opensearch.cluster.controller.store.MetadataStore;

/**
 * Main orchestrator that coordinates goal state updates from planned allocations
 */
public class GoalStateOrchestrator {
    private static final Logger log = LogManager.getLogger(GoalStateOrchestrator.class);

    private final MetadataStore metadataStore;
    private final GoalStateOrchestrationStrategy strategy;

    public GoalStateOrchestrator(MetadataStore metadataStore, MetricsProvider metricsProvider) {
        this.metadataStore = metadataStore;
        // TODO: Make orchestration strategy configurable via application properties
        this.strategy = new RollingUpdateOrchestrationStrategy(metadataStore, metricsProvider);
    }

    /**
     * Orchestrate goal states for all indexes and shards in one controller task iteration
     *
     * @param clusterId the cluster ID to orchestrate goal states for
     */
    public void orchestrateGoalStates(String clusterId) {
        log.info("Starting goal state orchestration for cluster: {}", clusterId);

        try {
            // Apply orchestration strategy
            strategy.orchestrate(clusterId);

            log.info("Completed goal state orchestration for cluster: {}", clusterId);
        } catch (Exception e) {
            log.error("Failed to orchestrate goal states for cluster {}: {}", clusterId, e.getMessage(), e);
        }
    }
}
