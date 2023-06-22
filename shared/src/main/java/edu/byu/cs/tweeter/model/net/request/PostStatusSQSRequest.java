package edu.byu.cs.tweeter.model.net.request;

import java.sql.Statement;

import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusSQSRequest {

    private Status status;

    public PostStatusSQSRequest() {
    }

    public PostStatusSQSRequest(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
