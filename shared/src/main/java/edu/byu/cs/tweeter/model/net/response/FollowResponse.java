package edu.byu.cs.tweeter.model.net.response;

public class FollowResponse extends Response {


    public FollowResponse(boolean success) {
        super(success);
    }

    FollowResponse( String message) {
        super(false, message);
    }
}
