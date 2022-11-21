package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class StoryDAODynamo implements StoryDAO {

    public StoryResponse getStories (StoryRequest request) {
        assert  request.getLimit() > 0;
        assert  request.getTargetUserAlias() != null;

        List<Status> allFeeds = getDummyStories();
        List<Status> responseFeeds = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if (request.getLimit() > 0) {
            if(allFeeds != null) {
                int feedsIndex = getStoriesStartingIndex(request.getLastFeed(), allFeeds);

                for(int limitCounter = 0;  feedsIndex < allFeeds.size() && limitCounter < request.getLimit(); feedsIndex ++, limitCounter ++) {
                    responseFeeds.add(allFeeds.get(feedsIndex));
                }

                hasMorePages = feedsIndex < allFeeds.size();
            }

        }

        return new StoryResponse(hasMorePages, responseFeeds);

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

    public PostStatusResponse postStatus (PostStatusRequest request) {

        return new PostStatusResponse(true);
    }

    List<Status> getDummyStories() {
        return getFakeData().getFakeStatuses();
    }

    FakeData getFakeData() {
        return FakeData.getInstance();
    }





}
