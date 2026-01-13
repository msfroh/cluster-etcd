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

/// Index template model stored in etcd at:
/// <cluster-name>/templates/<template-name>/conf
///
/// Template structure:
/// - index_patterns: Array of patterns to match index names (e.g., ["logs-*", "metrics-*"])
/// - priority: Higher priority templates override lower priority ones (default: 0)
/// - template: Contains settings, mappings, and aliases to apply to matching indices
///
/// Additional cluster-specific fields:
/// - instanceName: Target instance name (optional)
/// - region: Target region (optional)
public class Template implements ToXContentObject {

    private List<String> indexPatterns;
    private Integer priority;
    private TemplateDefinition template;

    // Optional cluster-specific fields
    private String instanceName;
    private String region;

    public Template() {}

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

    public static Template fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        Template template = new Template();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                if ("index_patterns".equals(currentFieldName)) {
                    List<String> indexPatterns = new ArrayList<>();
                    parser.nextToken();
                    while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                        if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                            indexPatterns.add(parser.text());
                        }
                        parser.nextToken();
                    }
                    template.indexPatterns = indexPatterns;
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected array for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("template".equals(currentFieldName)) {
                    template.template = TemplateDefinition.fromXContent(parser);
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                }
                switch (currentFieldName) {
                    case "instance_name":
                        template.instanceName = parser.text();
                        break;
                    case "region":
                        template.region = parser.text();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if ("priority".equals(currentFieldName)) {
                    template.priority = parser.intValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return template;
    }

    /**
     * The template definition containing settings, mappings, and aliases.
     */
    public static class TemplateDefinition implements ToXContentObject {
        private Map<String, Object> settings;
        private Map<String, Object> mappings;
        private Map<String, Object> aliases;

        public TemplateDefinition() {}

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
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                parser.nextToken();
            }
            TemplateDefinition templateDefinition = new TemplateDefinition();
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
                            templateDefinition.settings = parser.map();
                            break;
                        case "mappings":
                            templateDefinition.mappings = parser.map();
                            break;
                        case "aliases":
                            templateDefinition.aliases = parser.map();
                            break;
                        default:
                            throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                    }
                }
                parser.nextToken();
            }
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
        Template template1 = (Template) obj;
        return java.util.Objects.equals(indexPatterns, template1.indexPatterns)
            && java.util.Objects.equals(priority, template1.priority)
            && java.util.Objects.equals(template, template1.template)
            && java.util.Objects.equals(instanceName, template1.instanceName)
            && java.util.Objects.equals(region, template1.region);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(indexPatterns, priority, template, instanceName, region);
    }
}
