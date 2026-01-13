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

/// ShardAllocation represents planned shard allocation stored in etcd at:
/// <cluster-name>/indices/<index-name>/<shard_id>/planned-allocation
public class ShardAllocation implements ToXContentObject {
    private String shardId;
    private String indexName;
    private List<String> ingestSUs = new ArrayList<>();
    private List<String> searchSUs = new ArrayList<>();
    private long allocationTimestamp;

    public ShardAllocation() {}

    public ShardAllocation(String shardId, String indexName) {
        this.shardId = shardId;
        this.indexName = indexName;
        this.allocationTimestamp = System.currentTimeMillis();
    }

    public String getShardId() {
        return shardId;
    }

    public void setShardId(String shardId) {
        this.shardId = shardId;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public List<String> getIngestSUs() {
        return ingestSUs;
    }

    // Custom setters to maintain null safety
    public void setIngestSUs(List<String> ingestSUs) {
        this.ingestSUs = ingestSUs != null ? ingestSUs : new ArrayList<>();
    }

    public List<String> getSearchSUs() {
        return searchSUs;
    }

    public void setSearchSUs(List<String> searchSUs) {
        this.searchSUs = searchSUs != null ? searchSUs : new ArrayList<>();
    }

    public long getAllocationTimestamp() {
        return allocationTimestamp;
    }

    public void setAllocationTimestamp(long allocationTimestamp) {
        this.allocationTimestamp = allocationTimestamp;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (shardId != null) {
            builder.field("shardId", shardId);
        }
        if (indexName != null) {
            builder.field("indexName", indexName);
        }
        if (ingestSUs != null && !ingestSUs.isEmpty()) {
            builder.field("ingestSUs", ingestSUs);
        }
        if (searchSUs != null && !searchSUs.isEmpty()) {
            builder.field("searchSUs", searchSUs);
        }
        builder.field("allocationTimestamp", allocationTimestamp);
        builder.endObject();
        return builder;
    }

    public static ShardAllocation fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        ShardAllocation shardAllocation = new ShardAllocation();
        String currentFieldName = null;
        while (parser.currentToken() != XContentParser.Token.END_OBJECT) {
            if (parser.currentToken() == XContentParser.Token.FIELD_NAME) {
                currentFieldName = parser.currentName();
            } else if (parser.currentToken() == XContentParser.Token.START_ARRAY) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid array, expected field name");
                }
                switch (currentFieldName) {
                    case "ingestSUs":
                        List<String> ingestSUs = new ArrayList<>();
                        parser.nextToken();
                        while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                            if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                                ingestSUs.add(parser.text());
                            }
                            parser.nextToken();
                        }
                        shardAllocation.ingestSUs = ingestSUs;
                        break;
                    case "searchSUs":
                        List<String> searchSUs = new ArrayList<>();
                        parser.nextToken();
                        while (parser.currentToken() != XContentParser.Token.END_ARRAY) {
                            if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                                searchSUs.add(parser.text());
                            }
                            parser.nextToken();
                        }
                        shardAllocation.searchSUs = searchSUs;
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected array for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_STRING) {
                if (currentFieldName == null) {
                    throw new ParsingException(parser.getTokenLocation(), "Invalid string, expected field name");
                }
                switch (currentFieldName) {
                    case "shardId":
                        shardAllocation.shardId = parser.text();
                        break;
                    case "indexName":
                        shardAllocation.indexName = parser.text();
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if ("allocationTimestamp".equals(currentFieldName)) {
                    shardAllocation.allocationTimestamp = parser.longValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return shardAllocation;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ShardAllocation that = (ShardAllocation) obj;
        return allocationTimestamp == that.allocationTimestamp
            && java.util.Objects.equals(shardId, that.shardId)
            && java.util.Objects.equals(indexName, that.indexName)
            && java.util.Objects.equals(ingestSUs, that.ingestSUs)
            && java.util.Objects.equals(searchSUs, that.searchSUs);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(shardId, indexName, ingestSUs, searchSUs, allocationTimestamp);
    }
}
