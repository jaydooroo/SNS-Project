package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public interface FeedDAO {

    public abstract List<Status> getFeeds (String feedAlias, int limit, Status lastFeed);
    public abstract void postFeed(Status status);
    public abstract void postFeedsSQS(List<Status> statuses);

}
