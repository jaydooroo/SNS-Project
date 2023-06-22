package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.model.net.request.PostStatusSQSRequest;
import edu.byu.cs.tweeter.model.net.request.UpdateFeedSQSRequest;
import edu.byu.cs.tweeter.server.Json.JsonSerializer;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.StatusService;

public class UpdateFeedSQSHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        StatusService service = new StatusService();

        System.out.println("updateFeedsSQS stage");
        for(SQSEvent.SQSMessage msg : input.getRecords()) {
            UpdateFeedSQSRequest request = JsonSerializer.deserialize(msg.getBody(), UpdateFeedSQSRequest.class);
            service.postFeedsSQS(request);
        }
        return null;
    }
}
