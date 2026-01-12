/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.controller.plugin;

import org.opensearch.action.support.ActionFilter;
import org.opensearch.plugins.ActionPlugin;
import org.opensearch.plugins.Plugin;

import java.util.List;

public class ClusterETCDControllerPlugin extends Plugin implements ActionPlugin {

    @Override
    public List<ActionFilter> getActionFilters() {
        return List.of();
    }
}
