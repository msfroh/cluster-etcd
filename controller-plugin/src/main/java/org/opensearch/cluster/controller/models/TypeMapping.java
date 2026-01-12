package org.opensearch.cluster.controller.models;

import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the type mapping for an index.
 * Defines the structure and data types of documents in the index.
 * 
 * Based on OpenSearch's TypeMapping structure which includes field definitions,
 * dynamic settings, runtime fields, and metadata field configurations.
 * 
 * @see <a href="https://opensearch.org/docs/latest/field-types/">OpenSearch Field Types</a>
 */
public class TypeMapping implements ToXContentObject {
    
    /**
     * The properties (field definitions) for the index.
     * Maps field names to their type definitions.
     * 
     * Example:
     * {
     *   "title": { "type": "text" },
     *   "age": { "type": "integer" },
     *   "created_at": { "type": "date" }
     * }
     */
    private Map<String, Object> properties = new HashMap<>();
    
    /**
     * Dynamic mapping configuration.
     * Controls how new fields are handled when they are not explicitly defined.
     * 
     * Values: "true" (default), "false", "strict"
     * - true: new fields are automatically added to the mapping
     * - false: new fields are ignored
     * - strict: throws an exception if new fields are encountered
     */
    private Object dynamic;
    
    /**
     * Runtime fields that are evaluated at query time.
     * These fields are not indexed but computed on-the-fly.
     */
    private Map<String, Object> runtime;
    
    /**
     * Source field configuration.
     * Controls how the original JSON document is stored.
     */
    private Map<String, Object> source;
    
    /**
     * Routing configuration for the document.
     * Determines which shard a document is stored in.
     */
    private Map<String, Object> routing;
    
    /**
     * Metadata field configuration.
     * Additional metadata about the document wrapped in "index_metadata" key.
     * Structure: { "index_metadata": { ... } }
     */
    private Map<String, Object> meta;
    
    /**
     * Field names configuration.
     * Controls the _field_names field.
     */
    private Map<String, Object> fieldNames;
    
    /**
     * Date detection configuration.
     * Controls automatic date detection in dynamic mapping.
     */
    private Boolean dateDetection;
    
    /**
     * Numeric detection configuration.
     * Controls automatic numeric detection in dynamic mapping.
     */
    private Boolean numericDetection;
    
    /**
     * Dynamic date formats.
     * Formats to use when detecting date fields dynamically.
     */
    private Object dynamicDateFormats;
    
    /**
     * Dynamic templates for controlling how new fields are mapped.
     */
    private Object dynamicTemplates;
    
    public TypeMapping() {
    }
    
    /**
     * Constructor with properties only (most common use case).
     * 
     * @param properties the field properties/definitions
     */
    public TypeMapping(Map<String, Object> properties) {
        this.properties = properties != null ? properties : new HashMap<>();
    }
    
    public TypeMapping(Map<String, Object> properties, Object dynamic, Map<String, Object> runtime,
                      Map<String, Object> source, Map<String, Object> routing, Map<String, Object> meta,
                      Map<String, Object> fieldNames, Boolean dateDetection, Boolean numericDetection,
                      Object dynamicDateFormats, Object dynamicTemplates) {
        this.properties = properties;
        this.dynamic = dynamic;
        this.runtime = runtime;
        this.source = source;
        this.routing = routing;
        this.meta = meta;
        this.fieldNames = fieldNames;
        this.dateDetection = dateDetection;
        this.numericDetection = numericDetection;
        this.dynamicDateFormats = dynamicDateFormats;
        this.dynamicTemplates = dynamicTemplates;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public Object getDynamic() {
        return dynamic;
    }
    
    public void setDynamic(Object dynamic) {
        this.dynamic = dynamic;
    }
    
    public Map<String, Object> getRuntime() {
        return runtime;
    }
    
    public void setRuntime(Map<String, Object> runtime) {
        this.runtime = runtime;
    }
    
    public Map<String, Object> getSource() {
        return source;
    }
    
    public void setSource(Map<String, Object> source) {
        this.source = source;
    }
    
    public Map<String, Object> getRouting() {
        return routing;
    }
    
    public void setRouting(Map<String, Object> routing) {
        this.routing = routing;
    }
    
    public Map<String, Object> getMeta() {
        return meta;
    }
    
    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }
    
    public Map<String, Object> getFieldNames() {
        return fieldNames;
    }
    
    public void setFieldNames(Map<String, Object> fieldNames) {
        this.fieldNames = fieldNames;
    }
    
    public Boolean getDateDetection() {
        return dateDetection;
    }
    
    public void setDateDetection(Boolean dateDetection) {
        this.dateDetection = dateDetection;
    }
    
    public Boolean getNumericDetection() {
        return numericDetection;
    }
    
    public void setNumericDetection(Boolean numericDetection) {
        this.numericDetection = numericDetection;
    }
    
    public Object getDynamicDateFormats() {
        return dynamicDateFormats;
    }
    
    public void setDynamicDateFormats(Object dynamicDateFormats) {
        this.dynamicDateFormats = dynamicDateFormats;
    }
    
    public Object getDynamicTemplates() {
        return dynamicTemplates;
    }
    
    public void setDynamicTemplates(Object dynamicTemplates) {
        this.dynamicTemplates = dynamicTemplates;
    }
    
    /**
     * Adds a field property to the mapping.
     * 
     * @param fieldName the name of the field
     * @param fieldDefinition the field definition (type, analyzer, etc.)
     * @return this TypeMapping instance for method chaining
     */
    public TypeMapping addProperty(String fieldName, Map<String, Object> fieldDefinition) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(fieldName, fieldDefinition);
        return this;
    }
    
    /**
     * Adds a simple field with just a type.
     * 
     * @param fieldName the name of the field
     * @param fieldType the type of the field (e.g., "text", "keyword", "integer", "date")
     * @return this TypeMapping instance for method chaining
     */
    public TypeMapping addSimpleProperty(String fieldName, String fieldType) {
        if (this.properties == null) {
            this.properties = new HashMap<>();
        }
        Map<String, Object> fieldDef = new HashMap<>();
        fieldDef.put("type", fieldType);
        this.properties.put(fieldName, fieldDef);
        return this;
    }
    
    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (properties != null && !properties.isEmpty()) {
            builder.field("properties", properties);
        }
        if (dynamic != null) {
            builder.field("dynamic", dynamic);
        }
        if (runtime != null && !runtime.isEmpty()) {
            builder.field("runtime", runtime);
        }
        if (source != null && !source.isEmpty()) {
            builder.field("_source", source);
        }
        if (routing != null && !routing.isEmpty()) {
            builder.field("_routing", routing);
        }
        if (meta != null && !meta.isEmpty()) {
            builder.field("_meta", meta);
        }
        if (fieldNames != null && !fieldNames.isEmpty()) {
            builder.field("_field_names", fieldNames);
        }
        if (dateDetection != null) {
            builder.field("date_detection", dateDetection);
        }
        if (numericDetection != null) {
            builder.field("numeric_detection", numericDetection);
        }
        if (dynamicDateFormats != null) {
            builder.field("dynamic_date_formats", dynamicDateFormats);
        }
        if (dynamicTemplates != null) {
            builder.field("dynamic_templates", dynamicTemplates);
        }
        builder.endObject();
        return builder;
    }
    
    public static TypeMapping fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        TypeMapping typeMapping = new TypeMapping();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid object, expected field name");
                }
                switch (currentFieldName) {
                    case "properties":
                        typeMapping.properties = parser.map();
                        break;
                    case "runtime":
                        typeMapping.runtime = parser.map();
                        break;
                    case "_source":
                        typeMapping.source = parser.map();
                        break;
                    case "_routing":
                        typeMapping.routing = parser.map();
                        break;
                    case "_meta":
                        typeMapping.meta = parser.map();
                        break;
                    case "_field_names":
                        typeMapping.fieldNames = parser.map();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_BOOLEAN) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid boolean, expected field name");
                }
                switch (currentFieldName) {
                    case "dynamic":
                        typeMapping.dynamic = parser.booleanValue();
                        break;
                    case "date_detection":
                        typeMapping.dateDetection = parser.booleanValue();
                        break;
                    case "numeric_detection":
                        typeMapping.numericDetection = parser.booleanValue();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected boolean for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if ("dynamic".equals(currentFieldName)) {
                    typeMapping.dynamic = parser.text();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                if ("dynamic_date_formats".equals(currentFieldName)) {
                    typeMapping.dynamicDateFormats = parser.list();
                } else if ("dynamic_templates".equals(currentFieldName)) {
                    typeMapping.dynamicTemplates = parser.list();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected array for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return typeMapping;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TypeMapping that = (TypeMapping) obj;
        return java.util.Objects.equals(dateDetection, that.dateDetection) &&
               java.util.Objects.equals(numericDetection, that.numericDetection) &&
               java.util.Objects.equals(properties, that.properties) &&
               java.util.Objects.equals(dynamic, that.dynamic) &&
               java.util.Objects.equals(runtime, that.runtime) &&
               java.util.Objects.equals(source, that.source) &&
               java.util.Objects.equals(routing, that.routing) &&
               java.util.Objects.equals(meta, that.meta) &&
               java.util.Objects.equals(fieldNames, that.fieldNames) &&
               java.util.Objects.equals(dynamicDateFormats, that.dynamicDateFormats) &&
               java.util.Objects.equals(dynamicTemplates, that.dynamicTemplates);
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(properties, dynamic, runtime, source, routing, meta,
                fieldNames, dateDetection, numericDetection, dynamicDateFormats, dynamicTemplates);
    }
}
