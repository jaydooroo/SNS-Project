package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowersRequest {
    private AuthToken authToken;
    private String followeeAlias;
    private int limit;
    private String lastFollowersAlias;

    public FollowersRequest() {
    }

    public FollowersRequest(AuthToken authToken, String followeeAlias, int limit, String lastFollowersAlias) {
        this.authToken = authToken;
        this.followeeAlias = followeeAlias;
        this.limit = limit;
        this.lastFollowersAlias = lastFollowersAlias;
    }


    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLastFollowersAlias() {
        return lastFollowersAlias;
    }

    public void setLastFollowersAlias(String lastFollowersAlias) {
        this.lastFollowersAlias = lastFollowersAlias;
    }
}
