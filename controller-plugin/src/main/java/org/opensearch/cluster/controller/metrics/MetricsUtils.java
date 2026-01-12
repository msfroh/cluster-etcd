package org.opensearch.cluster.controller.metrics;

import org.opensearch.cluster.controller.enums.NodeRole;

import java.util.HashMap;
import java.util.Map;

import static org.opensearch.cluster.controller.metrics.MetricsConstants.CLUSTER_ID_TAG;
import static org.opensearch.cluster.controller.metrics.MetricsConstants.INDEX_NAME_TAG;
import static org.opensearch.cluster.controller.metrics.MetricsConstants.ROLE_TAG;
import static org.opensearch.cluster.controller.metrics.MetricsConstants.SHARD_ID_TAG;

/**
 * Utility class for handling metrics.
 */
public class MetricsUtils {
    /**
     * Builds a map of metrics tags including cluster ID, index name, shard ID, and node role.
     *
     * @param clusterId the cluster ID
     * @param indexName the index name
     * @param shardId the shard ID
     * @param role the node role
     * @return a map of metrics tags
     */
    public static Map<String, String> buildMetricsTagsByRole(String clusterId, String indexName, String shardId, NodeRole role) {
        Map<String, String> tags = buildMetricsTags(clusterId, indexName, shardId);
        tags.put(ROLE_TAG, role.getValue());
        return tags;
    }

    /**
     * Builds a map of metrics tags including cluster ID, index name, and shard ID.
     *
     * @param clusterId the cluster ID
     * @param indexName the index name
     * @param shardId the shard ID
     * @return a map of metrics tags
     */
    public static Map<String, String> buildMetricsTags(String clusterId, String indexName, String shardId) {
        Map<String, String> tags = new HashMap<>();
        tags.put(CLUSTER_ID_TAG, clusterId);
        tags.put(INDEX_NAME_TAG, indexName);
        tags.put(SHARD_ID_TAG, shardId);
        return tags;
    }
}
