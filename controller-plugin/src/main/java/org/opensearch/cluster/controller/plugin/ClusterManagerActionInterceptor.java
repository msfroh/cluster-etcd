/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.controller.plugin;

import org.opensearch.action.ActionRequest;
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.opensearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.opensearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.opensearch.action.support.ActionFilter;
import org.opensearch.action.support.clustermanager.ClusterManagerNodeRequest;
import org.opensearch.cluster.controller.indices.AliasManager;
import org.opensearch.cluster.controller.indices.IndexManager;
import org.opensearch.cluster.controller.templates.TemplateManager;
import org.opensearch.core.action.ActionListener;

public class ClusterManagerActionInterceptor extends ActionFilter.Simple {
    private final TemplateManager templateManager;
    private final IndexManager indexManager;
    private final AliasManager aliasManager;

    public ClusterManagerActionInterceptor(TemplateManager templateManager, IndexManager indexManager, AliasManager aliasManager) {
        this.templateManager = templateManager;
        this.indexManager = indexManager;
        this.aliasManager = aliasManager;
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    protected boolean apply(String action, ActionRequest request, ActionListener<?> listener) {
        if (request instanceof ClusterManagerNodeRequest<?>) {
            switch (request) {
                case CreateIndexRequest createIndexRequest:
                    break;
                case PutMappingRequest putMappingRequest:
                    break;
                case UpdateSettingsRequest updateSettingsRequest:
                    break;
                case PutIndexTemplateRequest putIndexTemplateRequest:
                    break;
                case IndicesAliasesRequest indicesAliasesRequest:
                    break;
                default:
                    listener.onFailure(new IllegalArgumentException("Unsupported action: " + action));
            }
            return false;
        }

        return true;
    }
}
