package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class FeedDAODynamo extends DynamoDAO implements FeedDAO {

    DynamoDbTable<Status> feedTable;

    public List<Status> getFeeds (String feedFollowerAlias, int limit, Status lastFeed) {

        try {
            connectDB();

            Key key = Key.builder().partitionValue(feedFollowerAlias).build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder().
                    queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(false).limit(limit);

            if(lastFeed != null) {
                Map<String, AttributeValue> startKey = new HashMap<>();

                startKey.put("user_alias", AttributeValue.builder().s(feedFollowerAlias).build());

                startKey.put("timestamp", AttributeValue.builder().s(lastFeed.getTimestamp()).build());

                requestBuilder.exclusiveStartKey(startKey);
            }

            List<Status> statuses = new ArrayList<>();

            SdkIterable<Page<Status>> result = feedTable.query(requestBuilder.build());

            PageIterable<Status> pages = PageIterable.create(result);

            pages.stream().limit(1).forEach(followPage -> followPage.items().forEach(f -> statuses.add(f)));

            disconnectDB();

            //emty users that only have alias of the users.
            System.out.println("statuses: " + statuses);

            return statuses;

        } catch (Exception e) {

            disconnectDB();
            throw new RuntimeException("[Server Error] " + e.getMessage());

        }

    }


    public void postFeed(Status status){
        try {
            connectDB();


            this.feedTable.putItem(status);

            disconnectDB();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }

    }

    public void postFeedsSQS(List<Status> statuses) {

        connectDB();
        System.out.println(statuses);
        if(statuses.size() > 25)
            throw new RuntimeException("[Server Error] Too many users to write");

        WriteBatch.Builder<Status> writeBuilder = WriteBatch.builder(Status.class).mappedTableResource(this.feedTable);
        for (Status item : statuses) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(this.feedTable).size() > 0) {
                postFeedsSQS(result.unprocessedPutItemsForTable(this.feedTable));
            }

        } catch (DynamoDbException e) {
            e.printStackTrace();
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }

    }

    List<Status> getDummyFeeds() {
        return getFakeData().getFakeStatuses();
    }

    FakeData getFakeData() {
        return FakeData.getInstance();
    }

    @Override
    protected void connectTable() {
        if(this.feedTable  == null) {
            this.feedTable = enhancedClient.table("feed", TableSchema.fromBean(Status.class));
        }
    }

    @Override
    protected void disconnectTable() {
        this.feedTable = null;
    }


}
