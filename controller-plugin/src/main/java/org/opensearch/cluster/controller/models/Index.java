package org.opensearch.cluster.controller.models;

import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Index represents index configuration stored in etcd at:
 * <cluster-name>/indices/<index-name>/conf
 */
public class Index implements ToXContentObject {
    private String id = "";
    private String indexName;
    private String createdAt = java.time.OffsetDateTime.now().toString(); // ISO timestamp for proper ordering
    private IndexSettings settings;
    private TypeMapping mappings;
    private Map<String, Object> aliases = new HashMap<>();
    
    public Index() {
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getIndexName() {
        return indexName;
    }
    
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public IndexSettings getSettings() {
        return settings;
    }
    
    public void setSettings(IndexSettings settings) {
        this.settings = settings;
    }
    
    public TypeMapping getMappings() {
        return mappings;
    }
    
    public void setMappings(TypeMapping mappings) {
        this.mappings = mappings;
    }
    
    public Map<String, Object> getAliases() {
        return aliases;
    }
    
    public void setAliases(Map<String, Object> aliases) {
        this.aliases = aliases;
    }
    
    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (id != null) {
            builder.field("id", id);
        }
        if (indexName != null) {
            builder.field("indexName", indexName);
        }
        if (createdAt != null) {
            builder.field("createdAt", createdAt);
        }
        if (settings != null) {
            builder.field("settings");
            settings.toXContent(builder, params);
        }
        if (mappings != null) {
            builder.field("mappings");
            mappings.toXContent(builder, params);
        }
        if (aliases != null && !aliases.isEmpty()) {
            builder.field("aliases", aliases);
        }
        builder.endObject();
        return builder;
    }
    
    public static Index fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        Index index = new Index();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid object, expected field name");
                }
                switch (currentFieldName) {
                    case "settings":
                        index.settings = IndexSettings.fromXContent(parser);
                        break;
                    case "mappings":
                        index.mappings = TypeMapping.fromXContent(parser);
                        break;
                    case "aliases":
                        index.aliases = parser.map();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                }
                switch (currentFieldName) {
                    case "id":
                        index.id = parser.text();
                        break;
                    case "indexName":
                        index.indexName = parser.text();
                        break;
                    case "createdAt":
                        index.createdAt = parser.text();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return index;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Index index = (Index) obj;
        return java.util.Objects.equals(id, index.id) &&
               java.util.Objects.equals(indexName, index.indexName) &&
               java.util.Objects.equals(createdAt, index.createdAt) &&
               java.util.Objects.equals(settings, index.settings) &&
               java.util.Objects.equals(mappings, index.mappings) &&
               java.util.Objects.equals(aliases, index.aliases);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, indexName, createdAt, settings, mappings, aliases);
    }
}
