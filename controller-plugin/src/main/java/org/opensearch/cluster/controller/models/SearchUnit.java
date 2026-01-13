/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.models;

import org.opensearch.cluster.controller.config.Constants;
import org.opensearch.cluster.controller.enums.HealthState;
import org.opensearch.core.common.ParsingException;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * SearchUnit entity representing a search unit in the cluster.
 */
public class SearchUnit implements ToXContentObject {

    private String id;
    private String name;
    private String clusterName;
    private String role; // "PRIMARY", "SEARCH_REPLICA", "COORDINATOR"
    private String host;
    private int portHttp = 9200;
    private int portTransport = 9300;
    private String zone;
    private String shardId;
    private String stateAdmin; // "NORMAL", "DRAINED", etc.
    private HealthState statePulled; // GREEN, YELLOW, RED
    private Map<String, Object> nodeAttributes;

    public SearchUnit() {
        this.nodeAttributes = new HashMap<>();
    }

    public SearchUnit(String name, String role, String host) {
        this();
        this.name = name;
        this.role = role;
        this.host = host;
        this.stateAdmin = Constants.ADMIN_STATE_NORMAL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPortHttp() {
        return portHttp;
    }

    public void setPortHttp(int portHttp) {
        this.portHttp = portHttp;
    }

    public int getPortTransport() {
        return portTransport;
    }

    public void setPortTransport(int portTransport) {
        this.portTransport = portTransport;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getShardId() {
        return shardId;
    }

    public void setShardId(String shardId) {
        this.shardId = shardId;
    }

    public String getStateAdmin() {
        return stateAdmin;
    }

    public void setStateAdmin(String stateAdmin) {
        this.stateAdmin = stateAdmin;
    }

    public HealthState getStatePulled() {
        return statePulled;
    }

    public void setStatePulled(HealthState statePulled) {
        this.statePulled = statePulled;
    }

    public Map<String, Object> getNodeAttributes() {
        return nodeAttributes;
    }

    public void setNodeAttributes(Map<String, Object> nodeAttributes) {
        this.nodeAttributes = nodeAttributes;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (id != null) {
            builder.field("id", id);
        }
        if (name != null) {
            builder.field("name", name);
        }
        if (clusterName != null) {
            builder.field("clusterName", clusterName);
        }
        if (role != null) {
            builder.field("role", role);
        }
        if (host != null) {
            builder.field("host", host);
        }
        builder.field("portHttp", portHttp);
        builder.field("portTransport", portTransport);
        if (zone != null) {
            builder.field("zone", zone);
        }
        if (shardId != null) {
            builder.field("shardId", shardId);
        }
        if (stateAdmin != null) {
            builder.field("stateAdmin", stateAdmin);
        }
        if (statePulled != null) {
            builder.field("statePulled", statePulled.getValue());
        }
        if (nodeAttributes != null && !nodeAttributes.isEmpty()) {
            builder.field("nodeAttributes", nodeAttributes);
        }
        builder.endObject();
        return builder;
    }

    public static SearchUnit fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        SearchUnit searchUnit = new SearchUnit();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("nodeAttributes".equals(currentFieldName)) {
                    searchUnit.nodeAttributes = parser.map();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                }
                switch (currentFieldName) {
                    case "id":
                        searchUnit.id = parser.text();
                        break;
                    case "name":
                        searchUnit.name = parser.text();
                        break;
                    case "clusterName":
                        searchUnit.clusterName = parser.text();
                        break;
                    case "role":
                        searchUnit.role = parser.text();
                        break;
                    case "host":
                        searchUnit.host = parser.text();
                        break;
                    case "zone":
                        searchUnit.zone = parser.text();
                        break;
                    case "shardId":
                        searchUnit.shardId = parser.text();
                        break;
                    case "stateAdmin":
                        searchUnit.stateAdmin = parser.text();
                        break;
                    case "statePulled":
                        searchUnit.statePulled = HealthState.fromString(parser.text());
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid number, expected field name");
                }
                switch (currentFieldName) {
                    case "portHttp":
                        searchUnit.portHttp = parser.intValue();
                        break;
                    case "portTransport":
                        searchUnit.portTransport = parser.intValue();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return searchUnit;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SearchUnit that = (SearchUnit) obj;
        return portHttp == that.portHttp
            && portTransport == that.portTransport
            && java.util.Objects.equals(id, that.id)
            && java.util.Objects.equals(name, that.name)
            && java.util.Objects.equals(clusterName, that.clusterName)
            && java.util.Objects.equals(role, that.role)
            && java.util.Objects.equals(host, that.host)
            && java.util.Objects.equals(zone, that.zone)
            && java.util.Objects.equals(shardId, that.shardId)
            && java.util.Objects.equals(stateAdmin, that.stateAdmin)
            && statePulled == that.statePulled
            && java.util.Objects.equals(nodeAttributes, that.nodeAttributes);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
            id,
            name,
            clusterName,
            role,
            host,
            portHttp,
            portTransport,
            zone,
            shardId,
            stateAdmin,
            statePulled,
            nodeAttributes
        );
    }
}
