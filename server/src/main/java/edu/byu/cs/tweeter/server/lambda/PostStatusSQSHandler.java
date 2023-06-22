package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.model.net.request.PostStatusSQSRequest;
import edu.byu.cs.tweeter.server.Json.JsonSerializer;
import edu.byu.cs.tweeter.server.service.FollowService;

public class PostStatusSQSHandler implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        FollowService followService = new FollowService();
        System.out.println("Handler stage: " +  input.getRecords());
        for(SQSEvent.SQSMessage msg : input.getRecords()) {
             PostStatusSQSRequest request = JsonSerializer.deserialize(msg.getBody(), PostStatusSQSRequest.class);
             followService.postStatusSQS(request);
        }

        return null;
    }
}