package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class StoryRequest {

    private AuthToken authToken;
    private String targetUserAlias;
    private int limit;
    private Status lastFeed;


    public StoryRequest() {
    }

    public StoryRequest(AuthToken authToken, String targetUserAlias, int limit, Status lastFeed) {
        this.authToken = authToken;
        this.targetUserAlias = targetUserAlias;
        this.limit = limit;
        this.lastFeed = lastFeed;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getTargetUserAlias() {
        return targetUserAlias;
    }

    public void setTargetUserAlias(String targetUserAlias) {
        this.targetUserAlias = targetUserAlias;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Status getLastFeed() {
        return lastFeed;
    }

    public void setLastFeed(Status lastFeed) {
        this.lastFeed = lastFeed;
    }
}
