package edu.byu.cs.tweeter.server.service;


import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.Module.DynamoModule;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link edu.byu.cs.tweeter.server.dao.FollowsDAODynamo} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        FollowingResponse response =  getFollowsDAO().getFollowees(request);

        List<User> users = new ArrayList<>();

        for(User elem: response.getItems()) {

            GetUserRequest userRequest = new GetUserRequest(elem.getAlias(), request.getAuthToken());
            GetUserResponse userResponse = getUserDAO().getUser(userRequest);

            users.add(userResponse.getUser());
        }
        response.setItems(users);

        return response;
    }


    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFollowsDAO().getFollowers(request);
    }

    public FollowerCountResponse getFollowerCount (FollowerCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a AuthToken");
        }
        int count = getFollowsDAO().getFollowersCount(request.getTargetUser());
        FollowerCountResponse response = new FollowerCountResponse(count);
        return response;
    }

    public FollowingCountResponse getFollowingCount (FollowingCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a AuthToken");
        }
        int count = getFollowsDAO().getFolloweeCount(request.getTargetUser());
        FollowingCountResponse response = new FollowingCountResponse(count);
        return response;
    }

    public FollowResponse follow(FollowRequest request) {

        if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a authToken");
        }

        return getFollowsDAO().follow(request);
    }

    public UnfollowResponse unfollow (UnfollowRequest request) {

        if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a authToken");
        }

        return getFollowsDAO().unfollow(request);
    }

    public IsFollowerResponse isFollower (IsFollowerRequest request) {

        if(request.getFollower() == null ) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        } else if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a authToken");
        }


        return getFollowsDAO().isFollower(request);
    }


//    /**
//     * Returns an instance of {@link FollowingDAODynamo}. Allows mocking of the FollowDAO class
//     * for testing purposes. All usages of FollowDAO should get their FollowDAO
//     * instance from this method to allow for mocking of the instance.
//     *
//     * @return the instance.
//     */
//    FollowingDAO getFollowingDAO() {
//        Injector injector = Guice.createInjector(new DynamoModule());
//        return injector.getInstance(FollowingDAO.class);
//    }


    FollowsDAO getFollowsDAO() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(FollowsDAO.class);
    }

    UserDAO getUserDAO() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(UserDAO.class);
    }

}
