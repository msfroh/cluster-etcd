/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.models;

import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Request model for index template creation.
 * <p>
 * Example usage:
 * <pre>
 * {
 *   "index_patterns": ["logs-*", "metrics-*"],
 *   "priority": 100,
 *   "template": {
 *     "settings": {
 *       "number_of_shards": 2,
 *       "number_of_replicas": 1
 *     },
 *     "mappings": {
 *       "properties": {
 *         "timestamp": {"type": "date"},
 *         "message": {"type": "text"}
 *       }
 *     },
 *     "aliases": {
 *       "my_logs": {}
 *     }
 *   },
 *   "instance_name": "prod-cluster",
 *   "region": "us-west-2"
 * }
 * </pre>
 */
public class TemplateRequest implements ToXContentObject {
    private List<String> indexPatterns;
    private Integer priority;
    private TemplateDefinition template;

    // Optional cluster-specific fields
    private String instanceName;
    private String region;

    public TemplateRequest() {}

    public TemplateRequest(List<String> indexPatterns, Integer priority, TemplateDefinition template, String instanceName, String region) {
        this.indexPatterns = indexPatterns;
        this.priority = priority;
        this.template = template;
        this.instanceName = instanceName;
        this.region = region;
    }

    public List<String> getIndexPatterns() {
        return indexPatterns;
    }

    public void setIndexPatterns(List<String> indexPatterns) {
        this.indexPatterns = indexPatterns;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public TemplateDefinition getTemplate() {
        return template;
    }

    public void setTemplate(TemplateDefinition template) {
        this.template = template;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (indexPatterns != null && !indexPatterns.isEmpty()) {
            builder.field("index_patterns", indexPatterns);
        }
        if (priority != null) {
            builder.field("priority", priority);
        }
        if (template != null) {
            builder.field("template");
            template.toXContent(builder, params);
        }
        if (instanceName != null) {
            builder.field("instance_name", instanceName);
        }
        if (region != null) {
            builder.field("region", region);
        }
        builder.endObject();
        return builder;
    }

    public static TemplateRequest fromXContent(XContentParser parser) throws IOException {
        TemplateRequest templateRequest = new TemplateRequest();
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("template".equals(currentFieldName)) {
                    templateRequest.template = TemplateDefinition.fromXContent(parser);
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                if ("index_patterns".equals(currentFieldName)) {
                    List<String> indexPatterns = new ArrayList<>();
                    parser.nextToken();
                    while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                        if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                            indexPatterns.add(parser.text());
                        } else {
                            throw new ParsingException(
                                parser.getTokenLocation(),
                                "Unexpected value " + parser.currentToken() + "for field " + currentFieldName
                            );
                        }
                        parser.nextToken();
                    }
                    templateRequest.indexPatterns = indexPatterns;
                    parser.nextToken();
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if ("instance_name".equals(currentFieldName)) {
                    templateRequest.instanceName = parser.text();
                } else if ("region".equals(currentFieldName)) {
                    templateRequest.region = parser.text();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected value for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if ("priority".equals(currentFieldName)) {
                    templateRequest.priority = parser.intValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected value for field " + currentFieldName);
                }
            } else {
                throw new ParsingException(parser.getTokenLocation(), "Unexpected token " + parser.currentToken());
            }
            parser.nextToken();
        }
        parser.nextToken();
        return templateRequest;
    }

    public static class TemplateDefinition implements ToXContentObject {
        private Map<String, Object> settings;
        private Map<String, Object> mappings;
        private Map<String, Object> aliases;

        public TemplateDefinition() {}

        public TemplateDefinition(Map<String, Object> settings, Map<String, Object> mappings, Map<String, Object> aliases) {
            this.settings = settings;
            this.mappings = mappings;
            this.aliases = aliases;
        }

        public Map<String, Object> getSettings() {
            return settings;
        }

        public void setSettings(Map<String, Object> settings) {
            this.settings = settings;
        }

        public Map<String, Object> getMappings() {
            return mappings;
        }

        public void setMappings(Map<String, Object> mappings) {
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
            if (settings != null && !settings.isEmpty()) {
                builder.field("settings", settings);
            }
            if (mappings != null && !mappings.isEmpty()) {
                builder.field("mappings", mappings);
            }
            if (aliases != null && !aliases.isEmpty()) {
                builder.field("aliases", aliases);
            }
            builder.endObject();
            return builder;
        }

        public static TemplateDefinition fromXContent(XContentParser parser) throws IOException {
            TemplateDefinition templateDefinition = new TemplateDefinition();
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                parser.nextToken();
            }
            String currentFieldName = null;
            while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                    switch (currentFieldName) {
                        case "settings" -> templateDefinition.settings = parser.map();
                        case "mappings" -> templateDefinition.mappings = parser.map();
                        case "aliases" -> templateDefinition.aliases = parser.map();
                        case null, default -> throw new ParsingException(
                            parser.getTokenLocation(),
                            "Unexpected map for field " + currentFieldName
                        );
                    }
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected token " + parser.currentToken());
                }
                parser.nextToken();
            }
            parser.nextToken();
            return templateDefinition;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TemplateDefinition that = (TemplateDefinition) obj;
            return java.util.Objects.equals(settings, that.settings)
                && java.util.Objects.equals(mappings, that.mappings)
                && java.util.Objects.equals(aliases, that.aliases);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(settings, mappings, aliases);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TemplateRequest that = (TemplateRequest) obj;
        return java.util.Objects.equals(indexPatterns, that.indexPatterns)
            && java.util.Objects.equals(priority, that.priority)
            && java.util.Objects.equals(template, that.template)
            && java.util.Objects.equals(instanceName, that.instanceName)
            && java.util.Objects.equals(region, that.region);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(indexPatterns, priority, template, instanceName, region);
    }
}
