package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Named;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

public class FollowsDAODynamo extends DynamoDAO implements FollowsDAO{

//    ProfileCredentialsProvider credentialsProvider;
//    Region region;
//    DynamoDbClient ddb;
//    DynamoDbEnhancedClient enhancedClient;
//    DynamoDbTable<Follow> followTable;
//    private static String followerLast;
//    private static String followeeLast;

    DynamoDbTable<Follow> followTable;

    @Override
    public Integer getFollowersCount (User followee) {

        try{
            connectDB();

//            Key key = Key.builder().sortValue(followee.getUser_alias()).build();

//            ScanEnhancedRequest scanEnhancedRequest = new ScanRequest.Builder().filterExpression("")
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(followee.getUser_alias()).build());

            SdkIterable<Page<Follow>> result =  this.followTable.index("follows_index").query(queryConditional);
            PageIterable<Follow> pages = PageIterable.create(result);
            int count = (int) pages.items().stream().count();

//            this.followTable.scan()
            disconnectDB();
            return count;

        } catch (Exception e) {
            e.printStackTrace();
            disconnectDB();
            return -1;
        }

    }

    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param follower the User whose count of how many following is desired.
     * @return said count.
     */
    @Override
    public Integer getFolloweeCount(User follower) {
        // TODO: uses the dummy data.  Replace with a real implementation.
//        assert follower != null;
////        return getDummyFollowees().size();
//        return 20;

        try{
            connectDB();

            Key key = Key.builder().partitionValue(follower.getUser_alias()).build();


//            ScanEnhancedRequest scanEnhancedRequest = new ScanRequest.Builder().filterExpression("")
            QueryConditional queryConditional = QueryConditional.keyEqualTo(key.toBuilder().partitionValue(follower.getUser_alias()).build());

            int count = (int) this.followTable.query(queryConditional).items().stream().count();

//            this.followTable.scan()
            disconnectDB();
            return count;

        } catch (Exception e) {
            e.printStackTrace();
            disconnectDB();
            return -1;
        }


    }


    @Override
    public FollowersResponse getFollowers(String followeeAlias,int limit, String lastFollowersAlias ) {

        try {
            connectDB();

            Key key = Key.builder().partitionValue(followeeAlias).build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true).limit(limit);

            if(lastFollowersAlias != null) {

                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("follower_handle", AttributeValue.builder().s(lastFollowersAlias).build());
                startKey.put("followee_handle", AttributeValue.builder().s(followeeAlias).build());

                requestBuilder.exclusiveStartKey(startKey);
            }

            List<Follow> follows = new ArrayList<>();

            SdkIterable<Page<Follow>> result = followTable.index("follows_index").query(requestBuilder.build());
            PageIterable<Follow> pages = PageIterable.create(result);

            pages.stream().limit(1).forEach(followPage -> followPage.items().forEach(f -> follows.add(f)));


            boolean hasMorePages = false;
            if(!(follows.size() < limit)) {

                hasMorePages = true;

            }

            List<User> followers = new ArrayList<>();

            for(Follow elem:  follows) {
                User user = new User();
                user.setUser_alias(elem.getFollower_handle());
                followers.add(user);
            }

            disconnectDB();

            return new FollowersResponse(hasMorePages, followers);
        } catch (Exception e) {
            e.printStackTrace();
            disconnectDB();
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }

    public List<Follow> getUnlimitedFollowers (String followee_alias) {

        try {
            connectDB();

            Key key = Key.builder().partitionValue(followee_alias).build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true);

            List<Follow> follows = new ArrayList<>();

            SdkIterable<Page<Follow>> result = followTable.index("follows_index").query(requestBuilder.build());
            PageIterable<Follow> pages = PageIterable.create(result);

            pages.stream().limit(1).forEach(followPage -> followPage.items().forEach(f -> follows.add(f)));

            disconnectDB();
            return follows;


        } catch (Exception e) {
            e.printStackTrace();
            disconnectDB();
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }

    @Override
    public List<Follow> getUnlimitedFollowees(String follower_alias) {

        try {
            connectDB();

            Key key = Key.builder().partitionValue(follower_alias).build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true);

            List<Follow> follows = new ArrayList<>();

            SdkIterable<Page<Follow>> result = followTable.query(requestBuilder.build());
            PageIterable<Follow> pages = PageIterable.create(result);

            pages.stream().limit(1).forEach(followPage -> followPage.items().forEach(f -> follows.add(f)));

            disconnectDB();
            return follows;


        } catch (Exception e) {
            e.printStackTrace();
            disconnectDB();
            throw new RuntimeException("[Server Error] " + e.getMessage());
        }
    }


    @Override
    public FollowingResponse getFollowees(String followerAlias, int limit, String lastFolloweeAlias) {

        try {
            connectDB();

            Key key = Key.builder().partitionValue(followerAlias).build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder().queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true).limit(limit);

            if(lastFolloweeAlias != null) {
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("follower_handle", AttributeValue.builder().s(followerAlias).build());
                startKey.put("followee_handle", AttributeValue.builder().s(lastFolloweeAlias).build());

                requestBuilder.exclusiveStartKey(startKey);
            }

            List<Follow> follows = new ArrayList<>();

            SdkIterable<Page<Follow>> result = followTable.query(requestBuilder.build());
            PageIterable<Follow> pages = PageIterable.create(result);

            pages.stream().limit(1).forEach(followPage -> followPage.items().forEach(f -> follows.add(f)));

            boolean hasMorePages = false;
            if(!(follows.size() < limit)) {

            hasMorePages = true;

            }

            List<User> followees = new ArrayList<>();

            for(Follow elem : follows) {
                  User user = new User();
                  user.setUser_alias(elem.getFollowee_handle());
                  followees.add(user);}

            disconnectDB();

            //emty users that only have alias of the users.
           return new  FollowingResponse(followees, hasMorePages);

        } catch (Exception e) {

            disconnectDB();
            throw new RuntimeException("[Server Error] " + e.getMessage());

        }

    }



    @Override
    public FollowResponse follow(User followee, AuthToken authToken) {

        try {
            connectDB();
            Follow follow = new Follow(authToken.getUser_alias(), followee.getUser_alias());
            this.followTable.putItem(follow);

            disconnectDB();
            return new FollowResponse(true);
        } catch (Exception e) {
            e.printStackTrace();
            disconnectDB();
            return new FollowResponse(e.getMessage());
        }
    }

    @Override
    public UnfollowResponse unfollow(User followee, AuthToken authToken) {

        try{
            connectDB();

            Key key = Key.builder().partitionValue(authToken.getUser_alias()).sortValue(followee.getUser_alias()).build();
            this.followTable.deleteItem(key);
            disconnectDB();
            return new UnfollowResponse(true);

        } catch (Exception e) {
            e.printStackTrace();
            disconnectDB();
            return new UnfollowResponse(e.getMessage());
        }

    }

    @Override
    public IsFollowerResponse isFollower(User follower, User followee) {



        try{
            connectDB();

            Key key = Key.builder().partitionValue(follower.getUser_alias()).sortValue(followee.getUser_alias()).build();
            Follow follow = this.followTable.getItem(key);

            if(follow != null&& follow.getFollower_handle() != null && follow.getFollowee_handle() != null ) {
                disconnectDB();
                return new IsFollowerResponse(true);
            }
            else {
                disconnectDB();
                return new IsFollowerResponse(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
            disconnectDB();
            return new IsFollowerResponse(e.getMessage());

        }

    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getUser_alias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<User> getDummyFollowees() {
        return getFakeData().getFakeUsers();
    }

    List<User> getDummyFollowers() {
        return getFakeData().getFakeUsers();
    }


    FakeData getFakeData() {
        return FakeData.getInstance();
    }



    @Override
    protected void connectTable() {
        if(this.followTable == null) {
            this.followTable = enhancedClient.table("follows", TableSchema.fromBean(Follow.class));
        }
    }

    @Override
    protected void disconnectTable() {
        this.followTable = null;
    }
}
