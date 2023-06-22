package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

public class Decorator implements FollowsDAO{

    FollowsDAO followsDAO;
    int extra;

    public Decorator(FollowsDAO followsDAO, int extra) {
        this.followsDAO = followsDAO;
        this.extra = extra;
    }

    @Override
    public Integer getFollowersCount(User followee) {
        return null;
    }

    @Override
    public Integer getFolloweeCount(User follower) {
        return null;
    }

    @Override
    public FollowersResponse getFollowers(String followerAlias, int limit, String lastFolloweeAlias) {
        return this.followsDAO.getFollowers(followerAlias,limit,lastFolloweeAlias);
    }

    @Override
    public FollowingResponse getFollowees(String followerAlias, int limit, String lastFolloweeAlias) {
        return null;
    }

    @Override
    public FollowResponse follow(User followee, AuthToken authToken) {
        return null;
    }

    @Override
    public UnfollowResponse unfollow(User followee, AuthToken authToken) {
        return null;
    }

    @Override
    public IsFollowerResponse isFollower(User follower, User followee) {
        return null;
    }

    @Override
    public List<Follow> getUnlimitedFollowers(String followee_alias) {
        return null;
    }

    @Override
    public List<Follow> getUnlimitedFollowees(String follower_alias) {
        return null;
    }
}
