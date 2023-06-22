package edu.byu.cs.tweeter.model.net.request;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.Status;

public class UpdateFeedSQSRequest {
    List<Follow> followList;
    Status status;

    public UpdateFeedSQSRequest() {
    }

    public UpdateFeedSQSRequest(List<Follow> followList, Status status) {
        this.followList = followList;
        this.status = status;
    }


    public List<Follow> getFollowList() {
        return followList;
    }

    public void setFollowList(List<Follow> followList) {
        this.followList = followList;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
