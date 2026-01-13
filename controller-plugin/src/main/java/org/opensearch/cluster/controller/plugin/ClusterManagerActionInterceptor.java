/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.plugin;

import org.opensearch.action.ActionRequest;
import org.opensearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.opensearch.action.admin.indices.create.CreateIndexRequest;
import org.opensearch.action.admin.indices.create.CreateIndexResponse;
import org.opensearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.opensearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.opensearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.opensearch.action.support.ActionFilter;
import org.opensearch.action.support.clustermanager.ClusterManagerNodeReadRequest;
import org.opensearch.action.support.clustermanager.ClusterManagerNodeRequest;
import org.opensearch.cluster.controller.indices.AliasManager;
import org.opensearch.cluster.controller.indices.IndexManager;
import org.opensearch.cluster.controller.templates.TemplateManager;
import org.opensearch.cluster.controller.util.XContentUtils;
import org.opensearch.core.action.ActionListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClusterManagerActionInterceptor extends ActionFilter.Simple {
    private final String clusterId;
    private final TemplateManager templateManager;
    private final IndexManager indexManager;
    private final AliasManager aliasManager;

    public ClusterManagerActionInterceptor(
            String clusterId, TemplateManager templateManager, IndexManager indexManager, AliasManager aliasManager
    ) {
        this.clusterId = clusterId;
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
        if (request instanceof ClusterManagerNodeReadRequest<?>) {
            // TODO: Support GET APIs
            return true;
        } else if (request instanceof ClusterManagerNodeRequest<?>) {
            switch (request) {
                case CreateIndexRequest createIndexRequest:
                    try {
                        handleCreateIndexRequest(createIndexRequest, (ActionListener<CreateIndexResponse>) listener);
                    } catch (Exception e) {
                        listener.onFailure(e);
                    }
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

    private void handleCreateIndexRequest(CreateIndexRequest request, ActionListener<CreateIndexResponse> listener) throws Exception {
        Map<String, Object> settings = new HashMap<>();
        for (String key : request.settings().keySet()) {
            settings.put(key, request.settings().get(key));
        }
        Map<String, Object> mappings = XContentUtils.readMap(request.mappings());
        indexManager.createIndex(clusterId, request.index(), settings, mappings, Collections.emptyMap());
        listener.onResponse(new CreateIndexResponse(true, false, request.index()));
    }
}
