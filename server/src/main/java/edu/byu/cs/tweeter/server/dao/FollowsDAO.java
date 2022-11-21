package edu.byu.cs.tweeter.server.dao;

import java.util.List;

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

public abstract class FollowsDAO extends DAO {

    DynamoDbTable<Follow> followTable;

    public FollowsDAO(ProfileCredentialsProvider credentialsProvider, Region region) {
        super(credentialsProvider, region);

    }

    public abstract FollowersResponse getFollowers(FollowersRequest request);

    public abstract Integer getFollowersCount(User followee);
    public abstract Integer getFolloweeCount(User follower);

    public abstract FollowingResponse getFollowees(FollowingRequest request);

    public abstract FollowResponse follow(FollowRequest request);
    public abstract UnfollowResponse unfollow(UnfollowRequest request);

    public abstract IsFollowerResponse isFollower(IsFollowerRequest request);


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
