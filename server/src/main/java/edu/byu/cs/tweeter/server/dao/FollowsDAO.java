package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
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
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public interface FollowsDAO {



    public abstract Integer getFollowersCount(User followee);
    public abstract Integer getFolloweeCount(User follower);

    public abstract FollowersResponse getFollowers(String followerAlias, int limit, String lastFolloweeAlias);
    public abstract FollowingResponse getFollowees(String followerAlias, int limit, String lastFolloweeAlias);

    public abstract FollowResponse follow(User followee, AuthToken authToken);
    public abstract UnfollowResponse unfollow(User followee, AuthToken authToken);

    public abstract IsFollowerResponse isFollower(User follower, User followee );
    public abstract List<Follow> getUnlimitedFollowers (String followee_alias);
    public abstract List<Follow> getUnlimitedFollowees (String follower_alias);


}
