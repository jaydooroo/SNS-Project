package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Named;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.service.UserService;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class FollowsDAODynamo extends FollowsDAO {

//    ProfileCredentialsProvider credentialsProvider;
//    Region region;
//    DynamoDbClient ddb;
//    DynamoDbEnhancedClient enhancedClient;
//    DynamoDbTable<Follow> followTable;
    private static String followLast;
    private static String followeeLast;

    public FollowsDAODynamo (@Named("DynamoDBRegion")  Region region,
                             @Named("DynamoDbCredential") ProfileCredentialsProvider credentialsProvider) {
        super(credentialsProvider,region);

    }

    @Override
    public Integer getFollowersCount (User followee) {
        assert  followee != null;

        return 20;
    }


    @Override
    public FollowersResponse getFollowers(FollowersRequest request) {
//         TODO: Generates dummy data. Replace with a real implementation.
        assert request.getLimit() > 0;
        assert request.getFolloweeAlias() != null;

        List<User> allFollowers = getDummyFollowers();
        List<User> responseFollowers = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allFollowers != null) {
                int followersIndex = getFollowersStartingIndex(request.getLastFollowersAlias(), allFollowers);

                for(int limitCounter = 0; followersIndex < allFollowers.size() && limitCounter < request.getLimit(); followersIndex++, limitCounter++) {
                    responseFollowers.add(allFollowers.get(followersIndex));
                }

                hasMorePages = followersIndex < allFollowers.size();
            }
        }



        return new FollowersResponse(hasMorePages, responseFollowers);
    }


    private int getFollowersStartingIndex(String lastFollowerAlias, List<User> allFollowers) {

        int followersIndex = 0;

        if(lastFollowerAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollowerAlias.equals(allFollowers.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                    break;
                }
            }
        }

        return followersIndex;
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
        assert follower != null;
//        return getDummyFollowees().size();
        return 20;
    }

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */

    @Override
    public FollowingResponse getFollowees(FollowingRequest request) {

        try {
            connectDB();

            DynamoDbClient ddb = DynamoDbClient.builder().credentialsProvider(credentialsProvider).region(region).build();
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();
            DynamoDbTable<Follow> followTable = enhancedClient.table("follows", TableSchema.fromBean(Follow.class));

            Key key = Key.builder().partitionValue(request.getFollowerAlias()).build();

            QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder().queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true).limit(request.getLimit());

            if(followLast != null) {
                Map<String, AttributeValue> startKey = new HashMap<>();
                startKey.put("follower_handle", AttributeValue.builder().s(request.getFollowerAlias()).build());
                startKey.put("followee_handle", AttributeValue.builder().s(followeeLast).build());

                requestBuilder.exclusiveStartKey(startKey);
            }

            List<Follow> follows = new ArrayList<>();

            SdkIterable<Page<Follow>> result = followTable.query(requestBuilder.build());
            PageIterable<Follow> pages = PageIterable.create(result);

            pages.stream().limit(1).forEach(followPage -> followPage.items().forEach(f -> follows.add(f)));

            boolean hasMorePages = false;
            if(follows.size() < 10) {
                followeeLast = null;
            }
            else {
                followeeLast = follows.get(follows.size()-1).getFollowee_handle();
                hasMorePages = true;
            }

            // converting into User List by using userservice.
//            UserDAO userDAO = new UserDAODynamo();
//            List<User> users = new ArrayList<>();

            // need mimgage
            //1.
            //2. return back to the servvice and servie get the user from the table.
            //quick get s3

            List<User> followees = new ArrayList<>();

            for(Follow elem : follows) {
                  User user = new User();
                  user.setAlias(elem.getFollowee_handle());
                  followees.add(user);
//                GetUserRequest getUserRequest = new GetUserRequest(elem.getFollowee_handle(), request.getAuthToken());
//                GetUserResponse response = userDAO.getUser(getUserRequest);
//                users.add(response.getUser());
            }

            disconnectDB();
            //emty users that only have alias of the users.
           return new  FollowingResponse(followees, hasMorePages);


        } catch (Exception e) {

            disconnectDB();
            throw new RuntimeException("[Server Error] " + e.getMessage());



        }

//        // TODO: Generates dummy data. Replace with a real implementation.
//        assert request.getLimit() > 0;
//        assert request.getFollowerAlias() != null;
//
//        List<User> allFollowees = getDummyFollowees();
//        List<User> responseFollowees = new ArrayList<>(request.getLimit());
//
//        boolean hasMorePages = false;
//
//        if(request.getLimit() > 0) {
//            if (allFollowees != null) {
//                int followeesIndex = getFolloweesStartingIndex(request.getLastFolloweeAlias(), allFollowees);
//
//                for(int limitCounter = 0; followeesIndex < allFollowees.size() && limitCounter < request.getLimit(); followeesIndex++, limitCounter++) {
//                    responseFollowees.add(allFollowees.get(followeesIndex));
//                }
//
//                hasMorePages = followeesIndex < allFollowees.size();
//            }
//        }
//
//        return new FollowingResponse(responseFollowees, hasMorePages);
    }

    @Override
    public FollowResponse follow(FollowRequest request) {


        //jaycheck: need to figure out how to get the current user alias.

//      x`  Follow follow = new Follow( , request.getFollowee());

//        followTable.putItem(follow);

        return new FollowResponse(true);

    }

    @Override
    public UnfollowResponse unfollow(UnfollowRequest request) {

        return new UnfollowResponse(true);
    }

    @Override
    public IsFollowerResponse isFollower(IsFollowerRequest request) {

        boolean isFollower = new Random().nextInt() > 0;
        return new IsFollowerResponse(isFollower);
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
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
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


    //jaycheck need to figure out a way to upload the image file into the s3 bucket. and download it.
    public boolean downloadFile(String bucketName, String keyName,
                              InputStream content, long contentLength, String mimeType) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            metadata.setContentType(mimeType);

            s3.putObject(new PutObjectRequest(bucketName, keyName, content, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return true;
        } catch (AmazonServiceException e) {
            System.out.println(e.getErrorMessage());
            e.printStackTrace();
            return false;
        }

    }


    protected void connectDB () {

        if(this.ddb == null) {
            this.ddb = DynamoDbClient.builder().credentialsProvider(credentialsProvider).region(region).build();
        }
        if(this.enhancedClient == null) {
            this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();
        }
        if(this.followTable == null) {
            this.followTable = enhancedClient.table("follows", TableSchema.fromBean(Follow.class));
        }
    }
}
