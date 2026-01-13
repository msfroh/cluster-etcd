/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.plugin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.support.ActionFilter;
import org.opensearch.cluster.controller.allocation.ActualAllocationUpdater;
import org.opensearch.cluster.controller.allocation.ShardAllocator;
import org.opensearch.cluster.controller.discovery.Discovery;
import org.opensearch.cluster.controller.indices.AliasManager;
import org.opensearch.cluster.controller.indices.IndexManager;
import org.opensearch.cluster.controller.metrics.MetricsProvider;
import org.opensearch.cluster.controller.orchestration.GoalStateOrchestrator;
import org.opensearch.cluster.controller.store.EtcdMetadataStore;
import org.opensearch.cluster.controller.store.MetadataStore;
import org.opensearch.cluster.controller.tasks.TaskContext;
import org.opensearch.cluster.controller.tasks.TaskManager;
import org.opensearch.cluster.controller.templates.TemplateManager;
import org.opensearch.cluster.metadata.IndexNameExpressionResolver;
import org.opensearch.cluster.node.DiscoveryNodes;
import org.opensearch.cluster.service.ClusterService;
import org.opensearch.common.settings.ClusterSettings;
import org.opensearch.common.settings.IndexScopedSettings;
import org.opensearch.common.settings.Setting;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.settings.SettingsFilter;
import org.opensearch.core.common.Strings;
import org.opensearch.core.common.io.stream.NamedWriteableRegistry;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.env.Environment;
import org.opensearch.env.NodeEnvironment;
import org.opensearch.plugins.ActionPlugin;
import org.opensearch.plugins.ClusterPlugin;
import org.opensearch.plugins.Plugin;
import org.opensearch.repositories.RepositoriesService;
import org.opensearch.rest.RestController;
import org.opensearch.rest.RestHandler;
import org.opensearch.script.ScriptService;
import org.opensearch.threadpool.ExecutorBuilder;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.watcher.ResourceWatcherService;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ClusterETCDControllerPlugin extends Plugin implements ActionPlugin {
    private final Logger log = LogManager.getLogger(getClass());
    public static final Setting<String> ETCD_ENDPOINT_SETTING = Setting.simpleString("cluster.etcd.endpoint", Setting.Property.NodeScope);
    public static final Setting<String> MANAGED_CLUSTER_ID_SETTING = Setting.simpleString(
        "cluster.etcd.managed_cluster_id",
        Setting.Property.NodeScope
    );

    private ClusterManagerActionInterceptor clusterManagerActionInterceptor;
    private TaskManager taskManager;
    private MetadataStore metadataStore;

    @Override
    public Collection<Object> createComponents(
        org.opensearch.transport.client.Client client,
        ClusterService clusterService,
        ThreadPool threadPool,
        ResourceWatcherService resourceWatcherService,
        ScriptService scriptService,
        NamedXContentRegistry xContentRegistry,
        Environment environment,
        NodeEnvironment nodeEnvironment,
        NamedWriteableRegistry namedWriteableRegistry,
        IndexNameExpressionResolver indexNameExpressionResolver,
        Supplier<RepositoriesService> repositoriesServiceSupplier
    ) {
        ClusterSettings clusterSettings = clusterService.getClusterSettings();
        String endpointSetting = clusterSettings.get(ETCD_ENDPOINT_SETTING);
        if (Strings.isNullOrEmpty(endpointSetting)) {
            throw new IllegalStateException(ETCD_ENDPOINT_SETTING.getKey() + " has not been set");
        }
        String managedClusterId = clusterSettings.get(MANAGED_CLUSTER_ID_SETTING);
        if (Strings.isNullOrEmpty(managedClusterId)) {
            throw new IllegalStateException(MANAGED_CLUSTER_ID_SETTING.getKey() + " has not been set");
        }

        // Initialize the etcd client. Supports comma-separated endpoints.
        String[] endpoints = endpointSetting.split(",");
        String nodeName = clusterService.getNodeName();
        metadataStore = EtcdMetadataStore.getInstance(endpoints, nodeName);
        TemplateManager templateManager = new TemplateManager(metadataStore);
        clusterManagerActionInterceptor = new ClusterManagerActionInterceptor(
                managedClusterId,
                templateManager,
                new IndexManager(metadataStore, templateManager),
                new AliasManager(metadataStore)
        );
        MetricsProvider metricsProvider = new MetricsProvider(nodeName);
        TaskContext taskContext = new TaskContext(
                new ShardAllocator(metadataStore, metricsProvider),
                new ActualAllocationUpdater(metadataStore, metricsProvider),
                new GoalStateOrchestrator(metadataStore, metricsProvider),
                new Discovery(metadataStore, metricsProvider)
        );
        taskManager = new TaskManager(metadataStore, taskContext, managedClusterId, 10L, threadPool);
        taskManager.start();
        return Collections.emptySet();
    }

    @Override
    public void close() throws IOException {
        taskManager.stop();
        metadataStore.close();
        super.close();
    }

    @Override
    public List<ActionFilter> getActionFilters() {
        return List.of(clusterManagerActionInterceptor);
    }

    @Override
    public List<RestHandler> getRestHandlers(
        Settings settings,
        RestController restController,
        ClusterSettings clusterSettings,
        IndexScopedSettings indexScopedSettings,
        SettingsFilter settingsFilter,
        IndexNameExpressionResolver indexNameExpressionResolver,
        Supplier<DiscoveryNodes> nodesInCluster
    ) {
        return List.of();
    }

    @Override
    public List<Setting<?>> getSettings() {
        return List.of(ETCD_ENDPOINT_SETTING, MANAGED_CLUSTER_ID_SETTING);
    }

    @Override
    public List<ExecutorBuilder<?>> getExecutorBuilders(Settings settings) {
        return List.of(TaskManager.createExecutorBuilder(settings));
    }
}
