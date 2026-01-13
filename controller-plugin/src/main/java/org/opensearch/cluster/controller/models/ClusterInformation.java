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
import java.util.Map;

/**
 * Model representing OpenSearch Cluster Information response.
 *
 * Based on OpenSearch Cluster Information API:
 * https://docs.opensearch.org/latest/api-reference/cluster-api/info/
 *
 * This is returned by the root endpoint (/) and provides version, build details,
 * and cluster identification information.
 */
public class ClusterInformation implements ToXContentObject {

    /**
     * The name of the node that served the request.
     */
    private String name;

    /**
     * The name of the cluster.
     */
    private String clusterName;

    /**
     * The universally unique identifier (UUID) of the cluster.
     */
    private String clusterUuid;

    /**
     * Version and build metadata.
     */
    private Version version;

    /**
     * The tagline string.
     */
    private String tagline = "The OpenSearch Project: https://opensearch.org/";

    public ClusterInformation() {}

    public ClusterInformation(String name, String clusterName, String clusterUuid, Version version, String tagline) {
        this.name = name;
        this.clusterName = clusterName;
        this.clusterUuid = clusterUuid;
        this.version = version;
        this.tagline = tagline;
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

    public String getClusterUuid() {
        return clusterUuid;
    }

    public void setClusterUuid(String clusterUuid) {
        this.clusterUuid = clusterUuid;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (name != null) {
            builder.field("name", name);
        }
        if (clusterName != null) {
            builder.field("cluster_name", clusterName);
        }
        if (clusterUuid != null) {
            builder.field("cluster_uuid", clusterUuid);
        }
        if (version != null) {
            builder.field("version");
            version.toXContent(builder, params);
        }
        if (tagline != null) {
            builder.field("tagline", tagline);
        }
        builder.endObject();
        return builder;
    }

    public static ClusterInformation fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        ClusterInformation clusterInformation = new ClusterInformation();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                if ("version".equals(currentFieldName)) {
                    clusterInformation.version = Version.fromXContent(parser);
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected object for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                }
                switch (currentFieldName) {
                    case "name":
                        clusterInformation.name = parser.text();
                        break;
                    case "cluster_name":
                        clusterInformation.clusterName = parser.text();
                        break;
                    case "cluster_uuid":
                        clusterInformation.clusterUuid = parser.text();
                        break;
                    case "tagline":
                        clusterInformation.tagline = parser.text();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return clusterInformation;
    }

    /**
     * Nested class representing version and build information.
     */
    public static class Version implements ToXContentObject {

        /**
         * The distribution identifier, typically "opensearch".
         */
        private String distribution;

        /**
         * The OpenSearch version number (e.g., "3.2.0").
         */
        private String number;

        /**
         * The distribution type (e.g., "tar", "rpm", "deb").
         */
        private String buildType;

        /**
         * The commit hash the build was created from.
         */
        private String hash;

        /**
         * The build timestamp in ISO 8601 format.
         */
        private String buildDate;

        /**
         * Whether the build is a snapshot build.
         */
        private Boolean buildSnapshot;

        /**
         * The Lucene version used by this build.
         */
        private String luceneVersion;

        /**
         * The minimum compatible transport protocol version.
         */
        private String minimumWireCompatibilityVersion;

        /**
         * The minimum index version that can be read.
         */
        private String minimumIndexCompatibilityVersion;

        public Version() {}

        public Version(
            String distribution,
            String number,
            String buildType,
            String hash,
            String buildDate,
            Boolean buildSnapshot,
            String luceneVersion,
            String minimumWireCompatibilityVersion,
            String minimumIndexCompatibilityVersion
        ) {
            this.distribution = distribution;
            this.number = number;
            this.buildType = buildType;
            this.hash = hash;
            this.buildDate = buildDate;
            this.buildSnapshot = buildSnapshot;
            this.luceneVersion = luceneVersion;
            this.minimumWireCompatibilityVersion = minimumWireCompatibilityVersion;
            this.minimumIndexCompatibilityVersion = minimumIndexCompatibilityVersion;
        }

        public String getDistribution() {
            return distribution;
        }

        public void setDistribution(String distribution) {
            this.distribution = distribution;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getBuildType() {
            return buildType;
        }

        public void setBuildType(String buildType) {
            this.buildType = buildType;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getBuildDate() {
            return buildDate;
        }

        public void setBuildDate(String buildDate) {
            this.buildDate = buildDate;
        }

        public Boolean getBuildSnapshot() {
            return buildSnapshot;
        }

        public void setBuildSnapshot(Boolean buildSnapshot) {
            this.buildSnapshot = buildSnapshot;
        }

        public String getLuceneVersion() {
            return luceneVersion;
        }

        public void setLuceneVersion(String luceneVersion) {
            this.luceneVersion = luceneVersion;
        }

        public String getMinimumWireCompatibilityVersion() {
            return minimumWireCompatibilityVersion;
        }

        public void setMinimumWireCompatibilityVersion(String minimumWireCompatibilityVersion) {
            this.minimumWireCompatibilityVersion = minimumWireCompatibilityVersion;
        }

        public String getMinimumIndexCompatibilityVersion() {
            return minimumIndexCompatibilityVersion;
        }

        public void setMinimumIndexCompatibilityVersion(String minimumIndexCompatibilityVersion) {
            this.minimumIndexCompatibilityVersion = minimumIndexCompatibilityVersion;
        }

        public static Version fromMap(Map<String, Object> map) {
            Version version = new Version();
            if (map.containsKey("distribution")) {
                version.setDistribution((String) map.get("distribution"));
            }
            if (map.containsKey("number")) {
                version.setNumber((String) map.get("number"));
            }
            if (map.containsKey("buildType")) {
                version.setBuildType((String) map.get("buildType"));
            }
            if (map.containsKey("hash")) {
                version.setHash((String) map.get("hash"));
            }
            if (map.containsKey("buildDate")) {
                version.setBuildDate((String) map.get("buildDate"));
            }
            if (map.containsKey("buildSnapshot")) {
                version.setBuildSnapshot((Boolean) map.get("buildSnapshot"));
            }
            if (map.containsKey("luceneVersion")) {
                version.setLuceneVersion((String) map.get("luceneVersion"));
            }
            if (map.containsKey("minimumWireCompatibilityVersion")) {
                version.setMinimumWireCompatibilityVersion((String) map.get("minimumWireCompatibilityVersion"));
            }
            if (map.containsKey("minimumIndexCompatibilityVersion")) {
                version.setMinimumIndexCompatibilityVersion((String) map.get("minimumIndexCompatibilityVersion"));
            }
            return version;
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            if (distribution != null) {
                builder.field("distribution", distribution);
            }
            if (number != null) {
                builder.field("number", number);
            }
            if (buildType != null) {
                builder.field("build_type", buildType);
            }
            if (hash != null) {
                builder.field("build_hash", hash);
            }
            if (buildDate != null) {
                builder.field("build_date", buildDate);
            }
            if (buildSnapshot != null) {
                builder.field("build_snapshot", buildSnapshot);
            }
            if (luceneVersion != null) {
                builder.field("lucene_version", luceneVersion);
            }
            if (minimumWireCompatibilityVersion != null) {
                builder.field("minimum_wire_compatibility_version", minimumWireCompatibilityVersion);
            }
            if (minimumIndexCompatibilityVersion != null) {
                builder.field("minimum_index_compatibility_version", minimumIndexCompatibilityVersion);
            }
            builder.endObject();
            return builder;
        }

        public static Version fromXContent(XContentParser parser) throws IOException {
            if (parser.currentToken() == null) {
                parser.nextToken();
            }
            if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
                parser.nextToken();
            }
            Version version = new Version();
            String currentFieldName = null;
            while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
                if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                    if (currentFieldName == null) {
                        throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                    }
                    switch (currentFieldName) {
                        case "distribution":
                            version.distribution = parser.text();
                            break;
                        case "number":
                            version.number = parser.text();
                            break;
                        case "build_type":
                            version.buildType = parser.text();
                            break;
                        case "build_hash":
                            version.hash = parser.text();
                            break;
                        case "build_date":
                            version.buildDate = parser.text();
                            break;
                        case "lucene_version":
                            version.luceneVersion = parser.text();
                            break;
                        case "minimum_wire_compatibility_version":
                            version.minimumWireCompatibilityVersion = parser.text();
                            break;
                        case "minimum_index_compatibility_version":
                            version.minimumIndexCompatibilityVersion = parser.text();
                            break;
                        default:
                            throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                    }
                } else if (parser.currentToken() == XContentParser.Token.VALUE_BOOLEAN) {
                    if ("build_snapshot".equals(currentFieldName)) {
                        version.buildSnapshot = parser.booleanValue();
                    } else {
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected boolean for field " + currentFieldName);
                    }
                }
                parser.nextToken();
            }
            return version;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Version version1 = (Version) obj;
            return java.util.Objects.equals(distribution, version1.distribution)
                && java.util.Objects.equals(number, version1.number)
                && java.util.Objects.equals(buildType, version1.buildType)
                && java.util.Objects.equals(hash, version1.hash)
                && java.util.Objects.equals(buildDate, version1.buildDate)
                && java.util.Objects.equals(buildSnapshot, version1.buildSnapshot)
                && java.util.Objects.equals(luceneVersion, version1.luceneVersion)
                && java.util.Objects.equals(minimumWireCompatibilityVersion, version1.minimumWireCompatibilityVersion)
                && java.util.Objects.equals(minimumIndexCompatibilityVersion, version1.minimumIndexCompatibilityVersion);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(
                distribution,
                number,
                buildType,
                hash,
                buildDate,
                buildSnapshot,
                luceneVersion,
                minimumWireCompatibilityVersion,
                minimumIndexCompatibilityVersion
            );
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClusterInformation that = (ClusterInformation) obj;
        return java.util.Objects.equals(name, that.name)
            && java.util.Objects.equals(clusterName, that.clusterName)
            && java.util.Objects.equals(clusterUuid, that.clusterUuid)
            && java.util.Objects.equals(version, that.version)
            && java.util.Objects.equals(tagline, that.tagline);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, clusterName, clusterUuid, version, tagline);
    }
}
