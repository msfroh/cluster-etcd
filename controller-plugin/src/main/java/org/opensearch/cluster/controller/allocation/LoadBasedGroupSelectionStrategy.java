/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.allocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.opensearch.cluster.controller.enums.NodeRole;
import org.opensearch.cluster.controller.models.NodesGroup;
import java.util.List;

/**
 * Load-based group selection strategy (bin-packing).
 *
 * TODO: NOT YET IMPLEMENTED
 *
 * When implemented, this should:
 * - Select N groups with the lowest current load
 * - Implement classic bin-packing to balance load across groups
 * - Sort groups by currentLoad ascending and return top N
 */
public class LoadBasedGroupSelectionStrategy implements GroupSelectionStrategy {
    private static final Logger log = LogManager.getLogger(LoadBasedGroupSelectionStrategy.class);

    @Override
    public List<NodesGroup> selectGroups(List<NodesGroup> groups, NodeRole targetRole, int numGroupsNeeded) {
        // TODO: Implement load-based bin-packing selection
        //
        // List<NodesGroup> selected = groups.stream()
        // .sorted(Comparator.comparingInt(NodesGroup::getCurrentLoad))
        // .limit(numGroupsNeeded)
        // .collect(Collectors.toList());
        //
        // log.debug("LoadBased strategy selected {} groups for role={}", selected.size(), targetRole);
        // return selected;

        log.error("Load-based group selection is not yet implemented. Returning empty list to avoid blocking allocation thread.");
        return List.of();
    }

    @Override
    public String getStrategyName() {
        return "LoadBased";
    }
}
