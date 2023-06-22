package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusSQSRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.request.UpdateFeedSQSRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.Json.JsonSerializer;
import edu.byu.cs.tweeter.server.Module.DynamoModule;
import edu.byu.cs.tweeter.server.dao.AuthDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;

public class StatusService {


    public FeedResponse getFeeds(FeedRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a User alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        if(! getAuthDAO().authenticate(request.getAuthToken())) {
            LogoutRequest logoutRequest = new LogoutRequest(request.getAuthToken());
            getUserDAO().logout(logoutRequest);
            throw new RuntimeException("[Bad Request] Expired Authtoken");
            //ToDo:Logout
        }

        List<Status> resultStatuses = new ArrayList<>();

        List<Follow> followees = getFollowsDAO().getUnlimitedFollowees(request.getTargetUserAlias());
        List<Status> statuses = getFeedDao().getFeeds(request.getTargetUserAlias(),request.getLimit(),request.getLastFeed());

        boolean hasMorePages = false;
        if(!(statuses.size() < request.getLimit())) {
            hasMorePages = true;

        }

        System.out.println("follows: ");
        System.out.println(followees);

        for(Status status: statuses) {
            System.out.println("feed status: " + status.getUser().getUser_alias());

            for(Follow elem: followees) {
                System.out.println("followee: " + elem.getFollowee_handle());
                if(elem.getFollowee_handle().equals(status.getUser().getUser_alias())) {
                    resultStatuses.add(status);
                    continue;
                }
            }
        }

        return new FeedResponse(hasMorePages, resultStatuses);
    }

    public StoryResponse getStories(StoryRequest request) {

        if(request.getTargetUserAlias() == null) {

            throw new RuntimeException("[Bad Request] Request needs to have a User alias");
        }
        else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }


        if(! getAuthDAO().authenticate(request.getAuthToken())) {
            LogoutRequest logoutRequest = new LogoutRequest(request.getAuthToken());
            getUserDAO().logout(logoutRequest);
            throw new RuntimeException("[Bad Request] Expired Authtoken");
            //ToDo:Logout
        }

        return getStoryDao().getStories(request.getTargetUserAlias(), request.getLimit(),request.getLastFeed());

    }

    public PostStatusResponse postStatus (PostStatusRequest request) {

        if(request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a authToken");
        }

        if(! getAuthDAO().authenticate(request.getAuthToken())) {
            LogoutRequest logoutRequest = new LogoutRequest(request.getAuthToken());
            getUserDAO().logout(logoutRequest);
            throw new RuntimeException("[Bad Request] Expired Authtoken");
            //ToDo:Logout
        }

        getStoryDao().postStory(request.getStatus());

        PostStatusSQSRequest SQSRequest = new PostStatusSQSRequest(request.getStatus());

        String JsonRequestMsg = JsonSerializer.serialize(SQSRequest);

        String postStatusQueueURL = "https://sqs.us-west-2.amazonaws.com/366346557198/cs340PostStatus";

        SendMessageRequest sendMessageRequest = new SendMessageRequest().withQueueUrl(postStatusQueueURL).withMessageBody(JsonRequestMsg);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        sqs.sendMessage(sendMessageRequest);


        return new PostStatusResponse(true);

    }

    public void postFeedsSQS(UpdateFeedSQSRequest request) {

        List<Status> statuses = new ArrayList<>();
        for(Follow elem : request.getFollowList() ) {
            Status newStatus = new Status(elem.getFollower_handle(),request.getStatus().getPost(),request.getStatus().getUser(),request.getStatus().getDate(),request.getStatus().getUrls(),request.getStatus().getMentions());
            statuses.add(newStatus);
        }
        getFeedDao().postFeedsSQS(statuses);
    }


    UserDAO getUserDAO() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(UserDAO.class);
    }
    FeedDAO getFeedDao () {

        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(FeedDAO.class);
    }

    StoryDAO getStoryDao() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(StoryDAO.class);
    }

    public AuthDAO getAuthDAO() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(AuthDAO.class);
    }
    FollowsDAO getFollowsDAO() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(FollowsDAO.class);
    }
}
