package org.opensearch.cluster.controller.models;

import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Index metadata configuration including template type, aliases, and ingestion sources
 */
public class IndexMetadata implements ToXContentObject {
    
    private boolean isIndexTemplateType = false;
    private List<AliasConfig> aliases;
    private String idField;
    private String versionField;
    private BatchIngestionSource batchIngestionSource;
    private LiveIngestionSource liveIngestionSource;
    
    public IndexMetadata() {
    }
    
    public IndexMetadata(boolean isIndexTemplateType, List<AliasConfig> aliases, String idField, 
                         String versionField, BatchIngestionSource batchIngestionSource, 
                         LiveIngestionSource liveIngestionSource) {
        this.isIndexTemplateType = isIndexTemplateType;
        this.aliases = aliases;
        this.idField = idField;
        this.versionField = versionField;
        this.batchIngestionSource = batchIngestionSource;
        this.liveIngestionSource = liveIngestionSource;
    }
    
    public boolean isIndexTemplateType() {
        return isIndexTemplateType;
    }
    
    public void setIndexTemplateType(boolean isIndexTemplateType) {
        this.isIndexTemplateType = isIndexTemplateType;
    }
    
    public List<AliasConfig> getAliases() {
        return aliases;
    }
    
    public void setAliases(List<AliasConfig> aliases) {
        this.aliases = aliases;
    }
    
    public String getIdField() {
        return idField;
    }
    
    public void setIdField(String idField) {
        this.idField = idField;
    }
    
    public String getVersionField() {
        return versionField;
    }
    
    public void setVersionField(String versionField) {
        this.versionField = versionField;
    }
    
    public BatchIngestionSource getBatchIngestionSource() {
        return batchIngestionSource;
    }
    
    public void setBatchIngestionSource(BatchIngestionSource batchIngestionSource) {
        this.batchIngestionSource = batchIngestionSource;
    }
    
    public LiveIngestionSource getLiveIngestionSource() {
        return liveIngestionSource;
    }
    
    public void setLiveIngestionSource(LiveIngestionSource liveIngestionSource) {
        this.liveIngestionSource = liveIngestionSource;
    }
    
    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field("isIndexTemplateType", isIndexTemplateType);
        if (aliases != null && !aliases.isEmpty()) {
            builder.startArray("aliases");
            for (AliasConfig alias : aliases) {
                alias.toXContent(builder, params);
            }
            builder.endArray();
        }
        if (idField != null) {
            builder.field("idField", idField);
        }
        if (versionField != null) {
            builder.field("versionField", versionField);
        }
        if (batchIngestionSource != null) {
            builder.field("batchIngestionSource");
            batchIngestionSource.toXContent(builder, params);
        }
        if (liveIngestionSource != null) {
            builder.field("liveIngestionSource");
            liveIngestionSource.toXContent(builder, params);
        }
        builder.endObject();
        return builder;
    }
    
    public static IndexMetadata fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        IndexMetadata indexMetadata = new IndexMetadata();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                if ("aliases".equals(currentFieldName)) {
                    List<AliasConfig> aliases = new ArrayList<>();
                    parser.nextToken();
                    while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                        aliases.add(AliasConfig.fromXContent(parser));
                        parser.nextToken();
                    }
                    indexMetadata.aliases = aliases;
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected array for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("batchIngestionSource".equals(currentFieldName)) {
                    indexMetadata.batchIngestionSource = BatchIngestionSource.fromXContent(parser);
                } else if ("liveIngestionSource".equals(currentFieldName)) {
                    indexMetadata.liveIngestionSource = LiveIngestionSource.fromXContent(parser);
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_BOOLEAN) {
                if ("isIndexTemplateType".equals(currentFieldName)) {
                    indexMetadata.isIndexTemplateType = parser.booleanValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected boolean for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                }
                switch (currentFieldName) {
                    case "idField":
                        indexMetadata.idField = parser.text();
                        break;
                    case "versionField":
                        indexMetadata.versionField = parser.text();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return indexMetadata;
    }
    
    /**
     * Alias configuration with name and match strategy
     */
    public static class AliasConfig implements ToXContentObject {
        private String name;
        private String matchStrategy;  // LATEST, PREVIOUS
        
        public AliasConfig() {
        }
        
        public AliasConfig(String name, String matchStrategy) {
            this.name = name;
            this.matchStrategy = matchStrategy;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getMatchStrategy() {
            return matchStrategy;
        }
        
        public void setMatchStrategy(String matchStrategy) {
            this.matchStrategy = matchStrategy;
        }
        
        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            if (name != null) {
                builder.field("name", name);
            }
            if (matchStrategy != null) {
                builder.field("matchStrategy", matchStrategy);
            }
            builder.endObject();
            return builder;
        }
        
        public static AliasConfig fromXContent(XContentParser parser) throws IOException {
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                parser.nextToken();
            }
            AliasConfig aliasConfig = new AliasConfig();
            String currentFieldName = null;
            while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                    if (currentFieldName == null) {
                        throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                    }
                    switch (currentFieldName) {
                        case "name":
                            aliasConfig.name = parser.text();
                            break;
                        case "matchStrategy":
                            aliasConfig.matchStrategy = parser.text();
                            break;
                        default:
                            throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                    }
                }
                parser.nextToken();
            }
            return aliasConfig;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            AliasConfig that = (AliasConfig) obj;
            return java.util.Objects.equals(name, that.name) &&
                   java.util.Objects.equals(matchStrategy, that.matchStrategy);
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(name, matchStrategy);
        }
    }
    
    /**
     * Batch ingestion source configuration for Hive-based ingestion
     */
    public static class BatchIngestionSource implements ToXContentObject {
        private String hiveTable;
        private List<String> partitionKeys;
        private String sql;
        private String primaryKey;
        
        public BatchIngestionSource() {
        }
        
        public BatchIngestionSource(String hiveTable, List<String> partitionKeys, String sql, String primaryKey) {
            this.hiveTable = hiveTable;
            this.partitionKeys = partitionKeys;
            this.sql = sql;
            this.primaryKey = primaryKey;
        }
        
        public String getHiveTable() {
            return hiveTable;
        }
        
        public void setHiveTable(String hiveTable) {
            this.hiveTable = hiveTable;
        }
        
        public List<String> getPartitionKeys() {
            return partitionKeys;
        }
        
        public void setPartitionKeys(List<String> partitionKeys) {
            this.partitionKeys = partitionKeys;
        }
        
        public String getSql() {
            return sql;
        }
        
        public void setSql(String sql) {
            this.sql = sql;
        }
        
        public String getPrimaryKey() {
            return primaryKey;
        }
        
        public void setPrimaryKey(String primaryKey) {
            this.primaryKey = primaryKey;
        }
        
        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            if (hiveTable != null) {
                builder.field("hiveTable", hiveTable);
            }
            if (partitionKeys != null && !partitionKeys.isEmpty()) {
                builder.field("partitionKeys", partitionKeys);
            }
            if (sql != null) {
                builder.field("sql", sql);
            }
            if (primaryKey != null) {
                builder.field("primaryKey", primaryKey);
            }
            builder.endObject();
            return builder;
        }
        
        public static BatchIngestionSource fromXContent(XContentParser parser) throws IOException {
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                parser.nextToken();
            }
            BatchIngestionSource batchIngestionSource = new BatchIngestionSource();
            String currentFieldName = null;
            while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                    if ("partitionKeys".equals(currentFieldName)) {
                        List<String> partitionKeys = new ArrayList<>();
                        parser.nextToken();
                        while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                            if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                                partitionKeys.add(parser.text());
                            }
                            parser.nextToken();
                        }
                        batchIngestionSource.partitionKeys = partitionKeys;
                    } else {
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected array for field " + currentFieldName);
                    }
                } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                    if (currentFieldName == null) {
                        throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                    }
                    switch (currentFieldName) {
                        case "hiveTable":
                            batchIngestionSource.hiveTable = parser.text();
                            break;
                        case "sql":
                            batchIngestionSource.sql = parser.text();
                            break;
                        case "primaryKey":
                            batchIngestionSource.primaryKey = parser.text();
                            break;
                        default:
                            throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                    }
                }
                parser.nextToken();
            }
            return batchIngestionSource;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BatchIngestionSource that = (BatchIngestionSource) obj;
            return java.util.Objects.equals(hiveTable, that.hiveTable) &&
                   java.util.Objects.equals(partitionKeys, that.partitionKeys) &&
                   java.util.Objects.equals(sql, that.sql) &&
                   java.util.Objects.equals(primaryKey, that.primaryKey);
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(hiveTable, partitionKeys, sql, primaryKey);
        }
    }
    
    /**
     * Live ingestion source configuration for Kafka-based ingestion
     */
    public static class LiveIngestionSource implements ToXContentObject {
        private String kafkaTopic;
        private String cluster;
        
        public LiveIngestionSource() {
        }
        
        public LiveIngestionSource(String kafkaTopic, String cluster) {
            this.kafkaTopic = kafkaTopic;
            this.cluster = cluster;
        }
        
        public String getKafkaTopic() {
            return kafkaTopic;
        }
        
        public void setKafkaTopic(String kafkaTopic) {
            this.kafkaTopic = kafkaTopic;
        }
        
        public String getCluster() {
            return cluster;
        }
        
        public void setCluster(String cluster) {
            this.cluster = cluster;
        }
        
        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            if (kafkaTopic != null) {
                builder.field("kafkaTopic", kafkaTopic);
            }
            if (cluster != null) {
                builder.field("cluster", cluster);
            }
            builder.endObject();
            return builder;
        }
        
        public static LiveIngestionSource fromXContent(XContentParser parser) throws IOException {
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                parser.nextToken();
            }
            LiveIngestionSource liveIngestionSource = new LiveIngestionSource();
            String currentFieldName = null;
            while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                    if (currentFieldName == null) {
                        throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                    }
                    switch (currentFieldName) {
                        case "kafkaTopic":
                            liveIngestionSource.kafkaTopic = parser.text();
                            break;
                        case "cluster":
                            liveIngestionSource.cluster = parser.text();
                            break;
                        default:
                            throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                    }
                }
                parser.nextToken();
            }
            return liveIngestionSource;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            LiveIngestionSource that = (LiveIngestionSource) obj;
            return java.util.Objects.equals(kafkaTopic, that.kafkaTopic) &&
                   java.util.Objects.equals(cluster, that.cluster);
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(kafkaTopic, cluster);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        IndexMetadata that = (IndexMetadata) obj;
        return isIndexTemplateType == that.isIndexTemplateType &&
               java.util.Objects.equals(aliases, that.aliases) &&
               java.util.Objects.equals(idField, that.idField) &&
               java.util.Objects.equals(versionField, that.versionField) &&
               java.util.Objects.equals(batchIngestionSource, that.batchIngestionSource) &&
               java.util.Objects.equals(liveIngestionSource, that.liveIngestionSource);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(isIndexTemplateType, aliases, idField, versionField,
                batchIngestionSource, liveIngestionSource);
    }
}
