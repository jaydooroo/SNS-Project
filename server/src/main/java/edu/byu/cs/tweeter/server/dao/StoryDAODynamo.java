package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class StoryDAODynamo extends DynamoDAO implements StoryDAO {



    DynamoDbTable<Status> storyTable;

    public StoryResponse getStories (String targetUserAlias, int limit, Status lastFeed) {

        try {
            connectDB();

            Key key = Key.builder().partitionValue(targetUserAlias).build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder().queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(false).limit(limit);

            if(lastFeed != null) {
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("user_alias", AttributeValue.builder().s(targetUserAlias).build());
                startKey.put("timestamp", AttributeValue.builder().s(lastFeed.getTimestamp()).build());

                requestBuilder.exclusiveStartKey(startKey);
            }

            List<Status> statuses = new ArrayList<>();

            SdkIterable<Page<Status>> result = storyTable.query(requestBuilder.build());
            PageIterable<Status> pages = PageIterable.create(result);

            pages.stream().limit(1).forEach(followPage -> followPage.items().forEach(f -> statuses.add(f)));

            boolean hasMorePages = false;
            if(!(statuses.size() < limit)) {

                hasMorePages = true;

            }

            disconnectDB();

            //emty users that only have alias of the users.
            System.out.println("statuses: " + statuses);
            return new StoryResponse(hasMorePages, statuses);

        } catch (Exception e) {

            disconnectDB();
            throw new RuntimeException("[Server Error] " + e.getMessage());

        }

    }


    public List<Status> getUnlimitedStories(String target_alias) {
        try {
            connectDB();

            Key key = Key.builder().partitionValue(target_alias).build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true);

            List<Status> statuses = new ArrayList<>();

            SdkIterable<Page<Status>> result = storyTable.query(requestBuilder.build());
            PageIterable<Status> pages = PageIterable.create(result);

            pages.stream().limit(1).forEach(followPage -> followPage.items().forEach(f -> statuses.add(f)));

            disconnectDB();

            return statuses;


        } catch (Exception e) {
            e.printStackTrace();
            disconnectDB();
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }

    private int getStoriesStartingIndex(Status lastFeed, List<Status> allFeeds) {
        int feedsIndex = 0;

        if(lastFeed != null) {
            for(int i = 0; i < allFeeds.size(); i++) {
                if(lastFeed.equals(allFeeds.get(i))) {


                    feedsIndex = i +1;
                    break;
                }

            }
        }
        return feedsIndex;

    }

    public void postStory(Status status) {

        try {
            connectDB();

            status.setUser_alias(status.getUser().getUser_alias());

            this.storyTable.putItem(status);

            disconnectDB();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }

    }


    List<Status> getDummyStories() {
        return getFakeData().getFakeStatuses();
    }

    FakeData getFakeData() {
        return FakeData.getInstance();
    }


    @Override
    protected void connectTable() {
        if(this.storyTable == null) {
            this.storyTable = enhancedClient.table("story", TableSchema.fromBean(Status.class));
        }
    }

    @Override
    protected void disconnectTable() {
        this.storyTable = null;
    }
}
