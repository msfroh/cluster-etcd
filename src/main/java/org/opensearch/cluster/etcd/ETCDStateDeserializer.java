/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.cluster.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.cluster.etcd.changeapplier.CoordinatorNodeState;
import org.opensearch.cluster.etcd.changeapplier.DataNodeState;
import org.opensearch.cluster.etcd.changeapplier.NodeShardAssignment;
import org.opensearch.cluster.etcd.changeapplier.NodeState;
import org.opensearch.cluster.etcd.changeapplier.RemoteNode;
import org.opensearch.cluster.etcd.changeapplier.ShardRole;
import org.opensearch.cluster.metadata.IndexMetadata;
import org.opensearch.cluster.metadata.MappingMetadata;
import org.opensearch.cluster.node.DiscoveryNode;
import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.core.index.Index;
import org.opensearch.core.xcontent.DeprecationHandler;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.opensearch.common.settings.Settings;

/**
 * Sample JSON:
 * <pre>
 * {
 *   "local_shards": { // Data node content
 *       "idx1": {
 *           "0" : "PRIMARY",
 *           "1" : "SEARCH_REPLICA"
 *       },
 *       "idx2": {
 *           "2" : "REPLICA"
 *       }
 *   },
 *   "remote_shards": { // Coordinator node content
 *       "indices": { // Map of indices that this coordinator node is aware of.
 *          "idx1": {
 *              "uuid": "index-uuid",
 *              "shard_routing": [ // Must have every shard for the index.
 *                  [
 *                    {"node_name":"node1"},
 *                    {"node_name":"node2", "primary": true } // If we have a primary for one shard, we must have a primary for all shards
 *                  ],
 *                  [ // We don't assume an equal number of replicas for each shard.
 *                    {"node_name":"node1", "primary": true},
 *                    {"node_name":"node2"}, // Any non-primary is assumed to be a search replica.
 *                    {"node_name":"node3"}
 *                  ]
 *              ]
 *          },
 *          "idx2": { ... }
 *       }
 *   }
 * }
 * </pre>
 * 
 * Health check format (stored at {cluster_name}/search-unit/{node_name}/actual-state):
 * <pre>
 * {
 *   "nodeId": "unique-node-id",
 *   "ephemeralId": "ephemeral-id-123",
 *   "address": "xxx.xxx.x.xxx",
 *   "port": xxxx,
 *   "timestamp": 1750099493841,
 *   "heartbeatIntervalSeconds": 5
 * }
 * </pre>
 */
public final class ETCDStateDeserializer {
    private ETCDStateDeserializer() {}
    private static final Logger LOGGER = LogManager.getLogger(ETCDStateDeserializer.class);

    /**
     * Deserializes the node configuration stored in ETCD. Will also read the k/v pairs for each index
     * referenced from a data node.
     * <p>
     * For now, let's assume that we store JSON bytes in ETCD.
     *
     * @param localNode the local discovery node
     * @param byteSequence the serialized node state
     * @param etcdClient the ETCD client that we'll use to retrieve index metadata for local shards
     * @param clusterName the cluster name used to build paths for health lookups
     * @return the relevant node state
     */
    @SuppressWarnings("unchecked")
    public static NodeState deserializeNodeState(DiscoveryNode localNode, ByteSequence byteSequence, Client etcdClient, String clusterName) throws IOException {
        Map<String, Object> map;
        try (XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, byteSequence.getBytes())) {
            map = parser.map();
        }
        if (map.containsKey("local_shards")) {
            if (map.containsKey("remote_shards")) {
                // TODO: For now, assume a node is either a data node or a coordinator node.
                throw new IllegalStateException("Both local and remote shards are present in the node state. This is not yet supported.");
            }
            return readDataNodeState(localNode, etcdClient, (Map<String, Map<String, String>>) map.get("local_shards"), clusterName);
        } else if (map.containsKey("remote_shards")) {
            return readCoordinatorNodeState(localNode, etcdClient, (Map<String, Object>) map.get("remote_shards"), clusterName);
        }
        throw new IllegalStateException("Neither local nor remote shards are present in the node state. Node state should have been removed.");
    }

    @SuppressWarnings("unchecked")
    private static CoordinatorNodeState readCoordinatorNodeState(DiscoveryNode localNode, Client etcdClient, Map<String, Object> remoteShards, String clusterName) throws IOException {
        Map<String, Object> indices = (Map<String, Object>) remoteShards.get("indices");
        Map<String, NodeHealthInfo> nodeHealthMap = new HashMap<>();
        
        for (Map.Entry<String, Object> indexEntry : indices.entrySet()) {
            Map<String, Object> indexConfig = (Map<String, Object>) indexEntry.getValue();
            List<List<Map<String, Object>>> shardRouting = (List<List<Map<String, Object>>>) indexConfig.get("shard_routing");
            
            for (List<Map<String, Object>> shardEntry : shardRouting) {
                for (Map<String, Object> nodeEntry : shardEntry) {
                    String nodeName = (String) nodeEntry.get("node_name");
                    if (nodeName != null && !nodeHealthMap.containsKey(nodeName)) {
                        nodeHealthMap.put(nodeName, null); 
                    }
                }
            }
        }
        
        lookupNodeHealthInfo(etcdClient, nodeHealthMap, clusterName);
        
        List<RemoteNode> remoteNodes = new ArrayList<>();
        for (Map.Entry<String, NodeHealthInfo> entry : nodeHealthMap.entrySet()) {
            NodeHealthInfo healthInfo = entry.getValue();
            if (healthInfo != null) {
                remoteNodes.add(new RemoteNode(healthInfo.nodeId, healthInfo.ephemeralId, healthInfo.address, healthInfo.port));
            } else {
                LOGGER.warn("Health information not found for node: {}", entry.getKey());
            }
        }

        Map<Index, List<List<NodeShardAssignment>>> remoteShardAssignment = new HashMap<>();
        for (Map.Entry<String, Object> indexEntry : indices.entrySet()) {
            Map<String, Object> indexConfig = (Map<String, Object>) indexEntry.getValue();
            String uuid = (String) indexConfig.get("uuid");
            List<List<Map<String, Object>>> shardRouting = (List<List<Map<String, Object>>>) indexConfig.get("shard_routing");
            List<List<NodeShardAssignment>> shardAssignments = new ArrayList<>(shardRouting.size());
            
            for (List<Map<String, Object>> shardEntry : shardRouting) {
                List<NodeShardAssignment> nodeShardAssignments = new ArrayList<>();
                for (Map<String, Object> nodeEntry : shardEntry) {
                    String nodeName = (String) nodeEntry.get("node_name");
                    boolean isPrimary = nodeEntry.containsKey("primary") && (Boolean) nodeEntry.get("primary");
                    
                    NodeHealthInfo healthInfo = nodeHealthMap.get(nodeName);
                    if (healthInfo != null) {
                        nodeShardAssignments.add(new NodeShardAssignment(healthInfo.nodeId, isPrimary ? ShardRole.PRIMARY : ShardRole.SEARCH_REPLICA));
                    } else {
                        LOGGER.error("Cannot resolve node name '{}' to node ID - health info not available", nodeName);
                        throw new IllegalStateException("Cannot resolve node name '" + nodeName + "' to node ID");
                    }
                }
                shardAssignments.add(nodeShardAssignments);
            }
            remoteShardAssignment.put(new Index(indexEntry.getKey(), uuid), shardAssignments);
        }
        
        return new CoordinatorNodeState(localNode, remoteNodes, remoteShardAssignment);
    }

    private static DataNodeState readDataNodeState(DiscoveryNode localNode, Client etcdClient, Map<String, Map<String, String>> localShards, String clusterName) throws IOException {
        Map<String, Map<Integer, ShardRole>> localShardAssignment = new HashMap<>();
        Map<String, IndexMetadata> indexMetadataMap = new HashMap<>();
        try (KV kvClient = etcdClient.getKVClient()) {
            // Prepare futures for fetching settings and mappings separately
            List<CompletableFuture<GetResponse>> settingsFutures = new ArrayList<>();
            List<CompletableFuture<GetResponse>> mappingsFutures = new ArrayList<>();
            List<String> indexNames = new ArrayList<>();
            
            for (Map.Entry<String, Map<String, String>> entry : localShards.entrySet()) {
                String indexName = entry.getKey();
                indexNames.add(indexName);
                
                // Fetch settings and mappings from separate etcd paths
                String indexSettingsPath = ETCDPathUtils.buildIndexSettingsPath(clusterName, indexName);
                String indexMappingsPath = ETCDPathUtils.buildIndexMappingsPath(clusterName, indexName);
                
                settingsFutures.add(kvClient.get(ByteSequence.from(indexSettingsPath, StandardCharsets.UTF_8)));
                mappingsFutures.add(kvClient.get(ByteSequence.from(indexMappingsPath, StandardCharsets.UTF_8)));
                
                // Process shard assignments
                Map<String, String> shards = entry.getValue();
                for (Map.Entry<String, String> shardEntry : shards.entrySet()) {
                    String shardId = shardEntry.getKey();
                    String shardType = shardEntry.getValue();
                    localShardAssignment.computeIfAbsent(indexName, k -> new HashMap<>()).put(Integer.parseInt(shardId), ShardRole.valueOf(shardType));
                }
            }
            
            // Process the results
            for (int i = 0; i < indexNames.size(); i++) {
                String indexName = indexNames.get(i);
                IndexMetadata indexMetadata = buildIndexMetadataFromSeparateParts(
                    indexName, 
                    settingsFutures.get(i), 
                    mappingsFutures.get(i)
                );
                indexMetadataMap.put(indexName, indexMetadata);
            }
        }
        return new DataNodeState(localNode, indexMetadataMap, localShardAssignment);
    }
    
    /**
     * Builds IndexMetadata from separate settings and mappings etcd responses.
     * Also populates constant values that don't need to be stored in etcd.
     */
    private static IndexMetadata buildIndexMetadataFromSeparateParts(
        String indexName, 
        CompletableFuture<GetResponse> settingsFuture, 
        CompletableFuture<GetResponse> mappingsFuture
    ) throws IOException {
        Settings indexSettings = null;
        MappingMetadata mappingMetadata = null;
        
        try {
            // Fetch and parse settings
            GetResponse settingsResponse = settingsFuture.get();
            if (settingsResponse.getKvs().isEmpty()) {
                throw new IllegalStateException("Settings response is empty");
            }
            KeyValue settingsKv = settingsResponse.getKvs().get(0);
            try (XContentParser parser = JsonXContent.jsonXContent.createParser(
                NamedXContentRegistry.EMPTY, 
                DeprecationHandler.THROW_UNSUPPORTED_OPERATION, 
                settingsKv.getValue().getBytes()
            )) {
                indexSettings = Settings.fromXContent(parser);
            }
        
            
            // Fetch and parse mappings
            GetResponse mappingsResponse = mappingsFuture.get();
            if (mappingsResponse.getKvs().isEmpty()) {
                throw new IllegalStateException("Mappings response is empty");
            }
            KeyValue mappingsKv = mappingsResponse.getKvs().get(0);
            try (XContentParser parser = JsonXContent.jsonXContent.createParser(
                NamedXContentRegistry.EMPTY, 
                DeprecationHandler.THROW_UNSUPPORTED_OPERATION, 
                mappingsKv.getValue().getBytes()
            )) {
                // Parse the mapping JSON and create MappingMetadata
                Map<String, Object> mappingMap = parser.map();
                if (!mappingMap.isEmpty()) {
                    // Assume single mapping type for simplicity 
                    mappingMetadata = new MappingMetadata("_doc", mappingMap);
                }
            }
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to fetch index metadata parts from etcd", e);
        }
        
        // Build IndexMetadata with both etcd-sourced and constant values
        return buildIndexMetadataWithConstants(indexName, indexSettings, mappingMetadata);
    }
    
    /**
     * Builds IndexMetadata with settings and mappings from etcd, plus constant values 
     * that are populated in the plugin rather than stored in etcd.
     */
    private static IndexMetadata buildIndexMetadataWithConstants(
        String indexName, 
        Settings indexSettings, 
        MappingMetadata mappingMetadata
    ) {
        // Start with etcd-sourced settings
        Settings.Builder settingsBuilder = indexSettings != null ? 
            Settings.builder().put(indexSettings) : 
            Settings.builder();
      

        // This is just an example of how constants could be populated in the plugin.
        // Build IndexMetadata
        Settings finalSettings = settingsBuilder.build();
        IndexMetadata.Builder metadataBuilder = IndexMetadata.builder(indexName)
            .settings(finalSettings);
            
        // Add mapping if present
        if (mappingMetadata != null) {
            metadataBuilder.putMapping(mappingMetadata);
        }
        
        // Set primary terms for each shard (these are constants that don't need to be in etcd)
        // Get number of shards from settings
        int numberOfShards = finalSettings.getAsInt(IndexMetadata.SETTING_NUMBER_OF_SHARDS, 1);
        for (int i = 0; i < numberOfShards; i++) {
            metadataBuilder.primaryTerm(i, 1); 
        }
        
        return metadataBuilder.build();
    }


    private static void lookupNodeHealthInfo(Client etcdClient, Map<String, NodeHealthInfo> nodeHealthMap, String clusterName) throws IOException {
        try (KV kvClient = etcdClient.getKVClient()) {
            List<CompletableFuture<GetResponse>> futures = new ArrayList<>();
            List<String> nodeNames = new ArrayList<>();
            
            for (String nodeName : nodeHealthMap.keySet()) {
                String healthKey = ETCDPathUtils.buildNodeActualStatePath(clusterName, nodeName);
                futures.add(kvClient.get(ByteSequence.from(healthKey, StandardCharsets.UTF_8)));
                nodeNames.add(nodeName);
            }
            
            for (int i = 0; i < futures.size(); i++) {
                String nodeName = nodeNames.get(i);
                try {
                    GetResponse response = futures.get(i).get();
                    if (!response.getKvs().isEmpty()) {
                        KeyValue kv = response.getKvs().get(0);
                        NodeHealthInfo healthInfo = parseHealthInfo(kv);
                        nodeHealthMap.put(nodeName, healthInfo);
                        LOGGER.debug("Resolved node '{}' to ID '{}' with ephemeral ID '{}'", nodeName, healthInfo.nodeId, healthInfo.ephemeralId);
                    } else {
                        LOGGER.warn("No health information found for node: {}", nodeName);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error("Failed to lookup health info for node: {}", nodeName, e);
                    throw new IOException("Failed to lookup health info for node: " + nodeName, e);
                }
            }
        }
    }
    
    /**
     * Parses health information from ETCD key-value pair.
     */
    @SuppressWarnings("unchecked")
    private static NodeHealthInfo parseHealthInfo(KeyValue kv) throws IOException {
        try (XContentParser parser = JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, kv.getValue().getBytes())) {
            Map<String, Object> healthMap = parser.map();
            
            String nodeId = (String) healthMap.get("nodeId");
            String ephemeralId = (String) healthMap.get("ephemeralId");
            String address = (String) healthMap.get("address");
            int port = ((Number) healthMap.get("port")).intValue();
            
            return new NodeHealthInfo(nodeId, ephemeralId, address, port);
        }
    }
    

    private static class NodeHealthInfo {
        final String nodeId;
        final String ephemeralId;
        final String address;
        final int port;
        
        NodeHealthInfo(String nodeId, String ephemeralId, String address, int port) {
            this.nodeId = nodeId;
            this.ephemeralId = ephemeralId;
            this.address = address;
            this.port = port;
        }
    }
}

