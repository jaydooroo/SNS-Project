package edu.byu.cs.tweeter.server.service;

import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.Module.DynamoModule;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;

public class StatusService {


    public FeedResponse getFeeds(FeedRequest request) {
        if(request.getTargetUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a User alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFeedDao().getFeeds(request);

    }

    public StoryResponse getStories(StoryRequest request) {

        if(request.getTargetUserAlias() == null) {

            throw new RuntimeException("[Bad Request] Request needs to have a User alias");
        }
        else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getStoryDao().getStories(request);
    }

    public PostStatusResponse postStatus (PostStatusRequest request) {

        if(request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        } else if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a authToken");
        }

        return getStoryDao().postStatus(request);
    }



    FeedDAO getFeedDao () {

        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(FeedDAO.class);
    }

    StoryDAO getStoryDao() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(StoryDAO.class);
    }
}
