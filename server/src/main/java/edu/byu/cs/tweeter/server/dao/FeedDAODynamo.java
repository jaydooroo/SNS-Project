package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class FeedDAODynamo implements FeedDAO {


    public FeedResponse getFeeds (FeedRequest request) {
        assert  request.getLimit() > 0;
        assert  request.getTargetUserAlias() != null;

        List<Status> allFeeds = getDummyFeeds();
        List<Status> responseFeeds = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if (request.getLimit() > 0) {
            if(allFeeds != null) {
                int feedsIndex = getFeedsStartingIndex(request.getLastFeed(), allFeeds);

                for(int limitCounter = 0;
                    feedsIndex < allFeeds.size() && limitCounter < request.getLimit();
                    feedsIndex ++,
                            limitCounter ++) {
                    responseFeeds.add(allFeeds.get(feedsIndex));
                }

                hasMorePages = feedsIndex < allFeeds.size();
            }

        }

        return new FeedResponse(hasMorePages,responseFeeds);

    }


    private int getFeedsStartingIndex(Status lastFeed, List<Status> allFeeds) {
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

    List<Status> getDummyFeeds() {
        return getFakeData().getFakeStatuses();
    }

    FakeData getFakeData() {
        return FakeData.getInstance();
    }

}
