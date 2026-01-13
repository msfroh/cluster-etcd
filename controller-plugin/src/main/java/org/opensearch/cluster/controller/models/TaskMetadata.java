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
import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;
import static org.opensearch.cluster.controller.config.Constants.TASK_STATUS_PENDING;

/**
 * Task metadata representing task state and information stored in metadata store.
 */
public class TaskMetadata implements ToXContentObject {

    private String name;
    private String status;
    private int priority; // 0 = highest priority
    private String schedule;
    private String input;
    private String output;
    private OffsetDateTime lastUpdated;
    private OffsetDateTime createdAt;

    public TaskMetadata() {
        this.createdAt = OffsetDateTime.now(UTC);
        this.lastUpdated = OffsetDateTime.now(UTC);
        this.status = TASK_STATUS_PENDING;
    }

    public TaskMetadata(String name, int priority) {
        this();
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(OffsetDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        if (name != null) {
            builder.field("name", name);
        }
        if (status != null) {
            builder.field("status", status);
        }
        builder.field("priority", priority);
        if (schedule != null) {
            builder.field("schedule", schedule);
        }
        if (input != null) {
            builder.field("input", input);
        }
        if (output != null) {
            builder.field("output", output);
        }
        if (lastUpdated != null) {
            builder.field("lastUpdated", lastUpdated.toString());
        }
        if (createdAt != null) {
            builder.field("createdAt", createdAt.toString());
        }
        builder.endObject();
        return builder;
    }

    public static TaskMetadata fromXContent(XContentParser parser) throws IOException {
        if (parser.currentToken() == null) {
            parser.nextToken();
        }
        if (parser.currentToken() == XContentParser.Token.START_OBJECT) {
            parser.nextToken();
        }
        TaskMetadata taskMetadata = new TaskMetadata();
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
                        taskMetadata.name = parser.text();
                        break;
                    case "status":
                        taskMetadata.status = parser.text();
                        break;
                    case "schedule":
                        taskMetadata.schedule = parser.text();
                        break;
                    case "input":
                        taskMetadata.input = parser.text();
                        break;
                    case "output":
                        taskMetadata.output = parser.text();
                        break;
                    case "lastUpdated":
                        taskMetadata.lastUpdated = OffsetDateTime.parse(parser.text());
                        break;
                    case "createdAt":
                        taskMetadata.createdAt = OffsetDateTime.parse(parser.text());
                        break;
                    default:
                        throw new ParsingException(parser.getTokenLocation(), "Unexpected string for field " + currentFieldName);
                }
            } else if (parser.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                if ("priority".equals(currentFieldName)) {
                    taskMetadata.priority = parser.intValue();
                } else {
                    throw new ParsingException(parser.getTokenLocation(), "Unexpected number for field " + currentFieldName);
                }
            }
            parser.nextToken();
        }
        return taskMetadata;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TaskMetadata that = (TaskMetadata) obj;
        return priority == that.priority
            && java.util.Objects.equals(name, that.name)
            && java.util.Objects.equals(status, that.status)
            && java.util.Objects.equals(schedule, that.schedule)
            && java.util.Objects.equals(input, that.input)
            && java.util.Objects.equals(output, that.output)
            && java.util.Objects.equals(lastUpdated, that.lastUpdated)
            && java.util.Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, status, priority, schedule, input, output, lastUpdated, createdAt);
    }
}
