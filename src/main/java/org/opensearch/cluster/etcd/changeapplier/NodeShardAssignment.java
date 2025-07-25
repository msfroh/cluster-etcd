/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.etcd.changeapplier;

/**
 * Represents the assignment of a shard to a node, from the coordinator's perspective.
 */
public record NodeShardAssignment(String nodeId, ShardRole shardRole) {
}
