package edu.byu.cs.tweeter.server.service;


import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusSQSRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.request.UpdateFeedSQSRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.Json.JsonSerializer;
import edu.byu.cs.tweeter.server.Module.DynamoModule;
import edu.byu.cs.tweeter.server.dao.AuthDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
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

        if( !getAuthDAO().authenticate(request.getAuthToken())) {
            LogoutRequest logoutRequest = new LogoutRequest(request.getAuthToken());
            getUserDAO().logout(logoutRequest);
            throw new RuntimeException("[Bad Request] auth token has been expired");

        }

        FollowingResponse response =  getFollowsDAO().getFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias() );

        if(!response.isSuccess()) {
            throw new RuntimeException("[Bad Request] failed to get followees");
        }

        List<User> users = new ArrayList<>();

        for(User elem: response.getItems()) {

            GetUserResponse userResponse = getUserDAO().getUser(elem.getUser_alias());
            if(userResponse.isSuccess()) {
                users.add(userResponse.getUser());
            }
            else {
                throw new RuntimeException("[Bad Request] failed to get users");
            }
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

        if(!getAuthDAO().authenticate(request.getAuthToken())) {
            LogoutRequest logoutRequest = new LogoutRequest(request.getAuthToken());
            getUserDAO().logout(logoutRequest);
            throw new RuntimeException("[Bad Request] auth token has been expired");
        }

        FollowersResponse response =  getFollowsDAO().getFollowers(request.getFolloweeAlias(), request.getLimit(), request.getLastFollowersAlias());

        if(!response.isSuccess()) {
            throw  new RuntimeException("[Bad Request] failed to get followers");
        }

        List<User> users = new ArrayList<>();

        for(User elem: response.getItems()) {

            GetUserResponse userResponse = getUserDAO().getUser(elem.getUser_alias());
            if(userResponse.isSuccess()) {
                users.add(userResponse.getUser());
            }
            else {
                throw new RuntimeException("[Bad Request] failed to get users");
            }
        }
        response.setItems(users);

        return response;

    }

    public FollowerCountResponse getFollowerCount (FollowerCountRequest request) {

        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a AuthToken");
        }

        if(!getAuthDAO().authenticate(request.getAuthToken())) {
            LogoutRequest logoutRequest = new LogoutRequest(request.getAuthToken());
            getUserDAO().logout(logoutRequest);
            throw new RuntimeException("[Bad Request] Expired Authtoken");
            //ToDo:Logout
        }

        int count = getFollowsDAO().getFollowersCount(request.getTargetUser());

        if (count <0 ) {
            FollowerCountResponse response = new FollowerCountResponse("Failed to retrieve count");
            return response;
        } else {
            FollowerCountResponse response = new FollowerCountResponse(count);
            return response;
        }
    }

    public FollowingCountResponse getFollowingCount (FollowingCountRequest request) {
        if(request.getTargetUser() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a AuthToken");
        }
        int count = getFollowsDAO().getFolloweeCount(request.getTargetUser());

        if(!getAuthDAO().authenticate(request.getAuthToken())) {
            LogoutRequest logoutRequest = new LogoutRequest(request.getAuthToken());
            getUserDAO().logout(logoutRequest);
            throw new RuntimeException("[Bad Request] Expired Authtoken");
            //ToDo:Logout
        }
        if (count <0 ) {
            FollowingCountResponse response = new FollowingCountResponse("Failed to retrieve count");
            return response;
        } else {
            FollowingCountResponse response = new FollowingCountResponse(count);
            return response;
        }
    }

    public FollowResponse follow(FollowRequest request) {

        if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a authToken");
        }

        if(!getAuthDAO().authenticate(request.getAuthToken())) {
            LogoutRequest logoutRequest = new LogoutRequest(request.getAuthToken());
            getUserDAO().logout(logoutRequest);
            throw new RuntimeException("[Bad Request] Expired Authtoken");
            //ToDo:Logout
        }


       FollowResponse response = getFollowsDAO().follow(request.getFollowee(), request.getAuthToken());

        List<Status> statuses = getStoryDao().getUnlimitedStories(request.getFollowee().getUser_alias());

        for (Status elem: statuses) {
            elem.setUser_alias(request.getAuthToken().getUser_alias());
            getFeedDao().postFeed(elem);
        }

        if(response.isSuccess()) {
            //TODO: Status posting
            return response;
        }
        else {
            return response;

        }

    }

    public UnfollowResponse unfollow (UnfollowRequest request) {

        if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a authToken");
        }

        if(!getAuthDAO().authenticate(request.getAuthToken())) {
            LogoutRequest logoutRequest = new LogoutRequest(request.getAuthToken());
            getUserDAO().logout(logoutRequest);
            throw new RuntimeException("[Bad Request] Expired Authtoken");
            //ToDo:Logout
        }

        return getFollowsDAO().unfollow(request.getFollowee(), request.getAuthToken());
    }

    public IsFollowerResponse isFollower (IsFollowerRequest request) {

        if(request.getFollower() == null ) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        } else if(request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a authToken");
        }

        if(!getAuthDAO().authenticate(request.getAuthToken())) {
            LogoutRequest logoutRequest = new LogoutRequest(request.getAuthToken());
            getUserDAO().logout(logoutRequest);
            throw new RuntimeException("[Bad Request] Expired Authtoken");
            //ToDo:Logout
        }

        return getFollowsDAO().isFollower(request.getFollower(), request.getFollowee());

    }

    public void postStatusSQS(PostStatusSQSRequest request) {

        List<Follow> followList = getFollowsDAO().getUnlimitedFollowers(request.getStatus().getUser().getUser_alias());
        System.out.println("StatusService page");

        List<Follow> batchToWrite = new ArrayList<>();


        int i = 0 ;

        System.out.println("total followlist: " + followList.size());
        for (Follow f: followList) {

            batchToWrite.add(f);

            if(batchToWrite.size() == 25) {
                writeChunkOfFollows(batchToWrite, request.getStatus());
                batchToWrite = new ArrayList<>();
                i += 1;
            }

        }
        System.out.println("total loop: " + i);

        if(batchToWrite.size() > 0) {

            writeChunkOfFollows(batchToWrite, request.getStatus());
        }
    }
    private void writeChunkOfFollows(List<Follow> followers, Status status) {

        UpdateFeedSQSRequest updateFeedSQSRequest = new UpdateFeedSQSRequest(followers,status);

        String JsonRequestMsg = JsonSerializer.serialize(updateFeedSQSRequest);

        String updateFeedsQueueURL = "https://sqs.us-west-2.amazonaws.com/366346557198/cs340UpdateFeeds";

        SendMessageRequest sendMessageRequest = new SendMessageRequest().withQueueUrl(updateFeedsQueueURL).withMessageBody(JsonRequestMsg);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        System.out.println("completed until sqs (post stat sqs stage)");

        sqs.sendMessage(sendMessageRequest);

    }


    FollowsDAO getFollowsDAO() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(FollowsDAO.class);
    }

    UserDAO getUserDAO() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(UserDAO.class);
    }

    public AuthDAO getAuthDAO() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(AuthDAO.class);
    }


    StoryDAO getStoryDao() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(StoryDAO.class);
    }

    FeedDAO getFeedDao () {

        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(FeedDAO.class);
    }
}
