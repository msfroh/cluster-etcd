/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.orchestration;

/**
 * Strategy interface for orchestrating goal states from planned allocations
 */
public interface GoalStateOrchestrationStrategy {

    /**
     * Orchestrate goal states for all indexes and shards in one controller task iteration
     *
     * @param clusterId the cluster ID to orchestrate goal states for
     */
    void orchestrate(String clusterId);
}
