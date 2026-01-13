/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.store;

import static org.opensearch.cluster.controller.config.Constants.PATH_ASSIGNED;
import static org.opensearch.cluster.controller.config.Constants.PATH_CLUSTERS;
import static org.opensearch.cluster.controller.config.Constants.PATH_CONTROLLERS;
import static org.opensearch.cluster.controller.config.Constants.PATH_COORDINATORS;
import static org.opensearch.cluster.controller.config.Constants.PATH_CTL_TASKS;
import static org.opensearch.cluster.controller.config.Constants.PATH_HEARTBEAT;
import static org.opensearch.cluster.controller.config.Constants.PATH_INDICES;
import static org.opensearch.cluster.controller.config.Constants.PATH_LEADER_ELECTION;
import static org.opensearch.cluster.controller.config.Constants.PATH_LOCKS;
import static org.opensearch.cluster.controller.config.Constants.PATH_METADATA;
import static org.opensearch.cluster.controller.config.Constants.PATH_MULTI_CLUSTER;
import static org.opensearch.cluster.controller.config.Constants.PATH_SEARCH_UNITS;
import static org.opensearch.cluster.controller.config.Constants.PATH_TEMPLATES;
import static org.opensearch.cluster.controller.config.Constants.SUFFIX_ACTUAL_ALLOCATION;
import static org.opensearch.cluster.controller.config.Constants.SUFFIX_ACTUAL_STATE;
import static org.opensearch.cluster.controller.config.Constants.SUFFIX_CONF;
import static org.opensearch.cluster.controller.config.Constants.SUFFIX_GOAL_STATE;
import static org.opensearch.cluster.controller.config.Constants.SUFFIX_MAPPINGS;
import static org.opensearch.cluster.controller.config.Constants.SUFFIX_PLANNED_ALLOCATION;
import static org.opensearch.cluster.controller.config.Constants.SUFFIX_SETTINGS;

/// Centralized etcd path resolver for all metadata keys with multi-cluster support.
/// Provides consistent path structure for tasks, search units, indices, and other cluster metadata.
/// All methods accept dynamic cluster names to support multi-cluster operations.
/// Stateless singleton - no cluster-specific state stored.
public class EtcdPathResolver {

    private static final String PATH_DELIMITER = "/";

    // Singleton instance - stateless
    private static final EtcdPathResolver INSTANCE = new EtcdPathResolver();

    private EtcdPathResolver() {
        // Private constructor for singleton
    }

    public static EtcdPathResolver getInstance() {
        return INSTANCE;
    }

    // =================================================================
    // CONTROLLER TASKS PATHS
    // =================================================================

    /// Get prefix for all controller tasks
    /// Pattern: /<cluster-name>/ctl-tasks
    public String getControllerTasksPrefix(String clusterName) {
        return String.join(PATH_DELIMITER, "", clusterName, PATH_CTL_TASKS);
    }

    /// Get path for specific controller task
    /// Pattern: /<cluster-name>/ctl-tasks/<task-name>
    public String getControllerTaskPath(String clusterName, String taskName) {
        return String.join(PATH_DELIMITER, getControllerTasksPrefix(clusterName), taskName);
    }

    // =================================================================
    // SEARCH UNIT PATHS
    // =================================================================

    /// Get prefix for all search units
    /// Pattern: /<cluster-name>/search-unit
    public String getSearchUnitsPrefix(String clusterName) {
        return String.join(PATH_DELIMITER, "", clusterName, PATH_SEARCH_UNITS);
    }

    /// Get search unit configuration path
    /// Pattern: /<cluster-name>/search-unit/<unit-name>/conf
    public String getSearchUnitConfPath(String clusterName, String unitName) {
        return String.join(PATH_DELIMITER, getSearchUnitsPrefix(clusterName), unitName, SUFFIX_CONF);
    }

    /// Get search unit goal state path
    /// Pattern: /<cluster-name>/search-unit/<unit-name>/goal-state
    public String getSearchUnitGoalStatePath(String clusterName, String unitName) {
        return String.join(PATH_DELIMITER, getSearchUnitsPrefix(clusterName), unitName, SUFFIX_GOAL_STATE);
    }

    /// Get search unit actual state path
    /// Pattern: /<cluster-name>/search-unit/<unit-name>/actual-state
    public String getSearchUnitActualStatePath(String clusterName, String unitName) {
        return String.join(PATH_DELIMITER, getSearchUnitsPrefix(clusterName), unitName, SUFFIX_ACTUAL_STATE);
    }

    // =================================================================
    // INDEX PATHS
    // =================================================================

    /// Get prefix for all indices
    /// Pattern: /<cluster-name>/indices
    public String getIndicesPrefix(String clusterName) {
        return String.join(PATH_DELIMITER, "", clusterName, PATH_INDICES);
    }

    /// Get prefix for a specific index (for prefix deletion)
    /// Pattern: /<cluster-name>/indices/<index-name>
    public String getIndexPrefix(String clusterName, String indexName) {
        return String.join(PATH_DELIMITER, getIndicesPrefix(clusterName), indexName);
    }

    /// Get index configuration path
    /// Pattern: /<cluster-name>/indices/<index-name>/conf
    public String getIndexConfPath(String clusterName, String indexName) {
        return String.join(PATH_DELIMITER, getIndicesPrefix(clusterName), indexName, SUFFIX_CONF);
    }

    /// Get index mappings path
    /// Pattern: /<cluster-name>/indices/<index-name>/mappings
    public String getIndexMappingsPath(String clusterName, String indexName) {
        return String.join(PATH_DELIMITER, getIndicesPrefix(clusterName), indexName, SUFFIX_MAPPINGS);
    }

    /// Get index settings path
    /// Pattern: /<cluster-name>/indices/<index-name>/settings
    public String getIndexSettingsPath(String clusterName, String indexName) {
        return String.join(PATH_DELIMITER, getIndicesPrefix(clusterName), indexName, SUFFIX_SETTINGS);
    }

    // =================================================================
    // ALIAS PATHS
    // =================================================================

    /// Get prefix for all aliases
    /// Pattern: /<cluster-name>/aliases
    public String getAliasesPrefix(String clusterName) {
        return String.join(PATH_DELIMITER, "", clusterName, "aliases");
    }

    /// Get alias configuration path
    /// Pattern: /<cluster-name>/aliases/<alias-name>/conf
    public String getAliasConfPath(String clusterName, String aliasName) {
        return String.join(PATH_DELIMITER, getAliasesPrefix(clusterName), aliasName, SUFFIX_CONF);
    }

    // =================================================================
    // TEMPLATE PATHS
    // =================================================================

    /// Get prefix for all templates
    /// Pattern: /<cluster-name>/templates
    public String getTemplatesPrefix(String clusterName) {
        return String.join(PATH_DELIMITER, "", clusterName, PATH_TEMPLATES);
    }

    /// Get template configuration path
    /// Pattern: /<cluster-name>/templates/<template-name>/conf
    public String getTemplateConfPath(String clusterName, String templateName) {
        return String.join(PATH_DELIMITER, getTemplatesPrefix(clusterName), templateName, SUFFIX_CONF);
    }

    // =================================================================
    // SHARD ALLOCATION PATHS
    // =================================================================

    /// Get shard planned allocation path
    /// Pattern: /<cluster-name>/indices/<index-name>/<shard-id>/planned-allocation
    public String getShardPlannedAllocationPath(String clusterName, String indexName, String shardId) {
        return String.join(PATH_DELIMITER, getIndicesPrefix(clusterName), indexName, shardId, SUFFIX_PLANNED_ALLOCATION);
    }

    /// Get shard actual allocation path
    /// Pattern: /<cluster-name>/indices/<index-name>/<shard-id>/actual-allocation
    public String getShardActualAllocationPath(String clusterName, String indexName, String shardId) {
        return String.join(PATH_DELIMITER, getIndicesPrefix(clusterName), indexName, shardId, SUFFIX_ACTUAL_ALLOCATION);
    }

    // =================================================================
    // COORDINATOR PATHS
    // =================================================================

    /// Get prefix for all coordinators
    /// Pattern: /<cluster-name>/coordinators
    public String getCoordinatorsPrefix(String clusterName) {
        return String.join(PATH_DELIMITER, "", clusterName, PATH_COORDINATORS);
    }

    /// Get coordinator goal state path using configured group and unit
    /// Pattern: /<cluster-name>/<search_unit_group>/<search_unit>/goal-state
    public String getCoordinatorGoalStatePath(String clusterName, String searchUnitGroup, String searchUnit) {
        return String.join(PATH_DELIMITER, "", clusterName, searchUnitGroup, searchUnit, SUFFIX_GOAL_STATE);
    }

    /// Get coordinator actual state path (per-coordinator reporting)
    /// Pattern: /<cluster-name>/coordinators/<coordinator-name>/actual-state
    public String getCoordinatorActualStatePath(String clusterName, String coordinatorName) {
        return String.join(PATH_DELIMITER, getCoordinatorsPrefix(clusterName), coordinatorName, SUFFIX_ACTUAL_STATE);
    }

    // =================================================================
    // LEADER ELECTION PATHS
    // =================================================================

    /// Get leader election path
    /// Pattern: /<cluster-name>/leader-election
    public String getLeaderElectionPath(String clusterName) {
        return String.join(PATH_DELIMITER, "", clusterName, PATH_LEADER_ELECTION);
    }

    // =================================================================
    // UTILITY METHODS
    // =================================================================

    /// Get cluster root path
    /// Pattern: /<cluster-name>
    public String getClusterRoot(String clusterName) {
        return String.join(PATH_DELIMITER, "", clusterName);
    }

    // =================================================================
    // MULTI-CLUSTER COORDINATION PATHS
    // =================================================================

    /// Get multi-cluster root path
    /// Pattern: /multi-cluster
    public String getMultiClusterRoot() {
        return String.join(PATH_DELIMITER, "", PATH_MULTI_CLUSTER);
    }

    /// Get controller heartbeat path
    /// Pattern: /multi-cluster/controllers/<controller-id>/heartbeat
    public String getControllerHeartbeatPath(String controllerId) {
        return String.join(PATH_DELIMITER, "", PATH_MULTI_CLUSTER, PATH_CONTROLLERS, controllerId, PATH_HEARTBEAT);
    }

    /// Get controller assignment path
    /// Pattern: /multi-cluster/controllers/<controller-id>/assigned/<cluster-id>
    public String getControllerAssignmentPath(String controllerId, String clusterId) {
        return String.join(PATH_DELIMITER, "", PATH_MULTI_CLUSTER, PATH_CONTROLLERS, controllerId, PATH_ASSIGNED, clusterId);
    }

    /// Get cluster lock path
    /// Pattern: /multi-cluster/locks/clusters/<cluster-id>
    public String getClusterLockPath(String clusterId) {
        return String.join(PATH_DELIMITER, "", PATH_MULTI_CLUSTER, PATH_LOCKS, PATH_CLUSTERS, clusterId);
    }

    /// Get cluster registry path
    /// Pattern: /multi-cluster/clusters/<cluster-id>/metadata
    public String getClusterRegistryPath(String clusterId) {
        return String.join(PATH_DELIMITER, "", PATH_MULTI_CLUSTER, PATH_CLUSTERS, clusterId, PATH_METADATA);
    }

    /// Get cluster's assigned controller path (for observability at cluster level)
    /// Pattern: /multi-cluster/clusters/<cluster-id>/assigned-to
    public String getClusterAssignedControllerPath(String clusterId) {
        return String.join(PATH_DELIMITER, "", PATH_MULTI_CLUSTER, PATH_CLUSTERS, clusterId, "assigned-to");
    }

    /// Get controllers prefix for listing
    /// Pattern: /multi-cluster/controllers/
    public String getControllersPrefix() {
        return String.join(PATH_DELIMITER, "", PATH_MULTI_CLUSTER, PATH_CONTROLLERS, "");
    }

    /// Get clusters prefix for listing
    /// Pattern: /multi-cluster/clusters/
    public String getClustersPrefix() {
        return String.join(PATH_DELIMITER, "", PATH_MULTI_CLUSTER, PATH_CLUSTERS, "");
    }
}
