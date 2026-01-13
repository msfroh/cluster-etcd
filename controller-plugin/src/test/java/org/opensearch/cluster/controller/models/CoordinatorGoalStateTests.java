/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.cluster.controller.models;

import org.opensearch.common.xcontent.json.JsonXContent;
import org.opensearch.core.common.bytes.BytesReference;
import org.opensearch.core.xcontent.DeprecationHandler;
import org.opensearch.core.xcontent.NamedXContentRegistry;
import org.opensearch.core.xcontent.ToXContent;
import org.opensearch.core.xcontent.XContentHelper;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.test.OpenSearchTestCase;

import java.io.IOException;

public class CoordinatorGoalStateTests extends OpenSearchTestCase {
    public void testSerializationEmpty() throws IOException {
        CoordinatorGoalState coordinatorGoalState = new CoordinatorGoalState();

        BytesReference bytesRef = XContentHelper.toXContent(
            coordinatorGoalState,
            JsonXContent.jsonXContent.mediaType(),
            ToXContent.EMPTY_PARAMS,
            false
        );
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(
            NamedXContentRegistry.EMPTY,
            DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
            bytes
        );
        CoordinatorGoalState deserializedCoordinatorGoalState = CoordinatorGoalState.fromXContent(parser);
        assertEquals(coordinatorGoalState, deserializedCoordinatorGoalState);
    }

    public void testRemoteShardsSerializationEmpty() throws IOException {
        CoordinatorGoalState.RemoteShards remoteShards = new CoordinatorGoalState.RemoteShards();

        BytesReference bytesRef = XContentHelper.toXContent(
            remoteShards,
            JsonXContent.jsonXContent.mediaType(),
            ToXContent.EMPTY_PARAMS,
            false
        );
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(
            NamedXContentRegistry.EMPTY,
            DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
            bytes
        );
        CoordinatorGoalState.RemoteShards deserializedRemoteShards = CoordinatorGoalState.RemoteShards.fromXContent(parser);
        assertEquals(remoteShards, deserializedRemoteShards);
    }

    public void testIndexShardRoutingSerializationEmpty() throws IOException {
        CoordinatorGoalState.IndexShardRouting indexShardRouting = new CoordinatorGoalState.IndexShardRouting();

        BytesReference bytesRef = XContentHelper.toXContent(
            indexShardRouting,
            JsonXContent.jsonXContent.mediaType(),
            ToXContent.EMPTY_PARAMS,
            false
        );
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(
            NamedXContentRegistry.EMPTY,
            DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
            bytes
        );
        CoordinatorGoalState.IndexShardRouting deserializedIndexShardRouting = CoordinatorGoalState.IndexShardRouting.fromXContent(parser);
        assertEquals(indexShardRouting, deserializedIndexShardRouting);
    }

    public void testShardNodeAssignmentSerializationEmpty() throws IOException {
        CoordinatorGoalState.ShardNodeAssignment shardNodeAssignment = new CoordinatorGoalState.ShardNodeAssignment();

        BytesReference bytesRef = XContentHelper.toXContent(
            shardNodeAssignment,
            JsonXContent.jsonXContent.mediaType(),
            ToXContent.EMPTY_PARAMS,
            false
        );
        byte[] bytes = BytesReference.toBytes(bytesRef);
        XContentParser parser = JsonXContent.jsonXContent.createParser(
            NamedXContentRegistry.EMPTY,
            DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
            bytes
        );
        CoordinatorGoalState.ShardNodeAssignment deserializedShardNodeAssignment = CoordinatorGoalState.ShardNodeAssignment.fromXContent(
            parser
        );
        assertEquals(shardNodeAssignment, deserializedShardNodeAssignment);
    }
}
