package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    private ServerFacade serverFacade;
    private static final String LOG_TAG = "GetStoryTask";
    public static final String URL_PATH = "/getstory";

    @Override
    protected PagedResponse getResponse() throws IOException, TweeterRemoteException {

        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
        Status lastStory = getLastItem() == null ? null : getLastItem();

        StoryRequest request = new StoryRequest(authToken, targetUserAlias, limit, lastStory);
        StoryResponse response = getServerFacade().getStories(request, URL_PATH);

        return response;
    }

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken,targetUser,limit,lastStatus,messageHandler);
    }

    @Override
    protected void showExceptionMessage(Exception ex) {
        Log.e(LOG_TAG, "Failed to get stories", ex);
        sendExceptionMessage(ex);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        return getFakeData().getPageOfStatus(getLastItem(), getLimit());
    }


    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return new ServerFacade();
    }
}