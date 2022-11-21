package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    private ServerFacade serverFacade;
    private static final String LOG_TAG = "GetFollowingCountTask";
    public static final String URL_PATH = "/getfollowingcount";


    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected void showExceptionMessage(Exception ex) {
        Log.e(LOG_TAG, "Failed to get following count", ex);
        sendExceptionMessage(ex);
    }

    @Override
    protected int runCountTask() throws IOException, TweeterRemoteException {
        FollowingCountRequest request = new FollowingCountRequest(getTargetUser(),this.authToken);
        FollowingCountResponse response = getServerFacade().getFollowingCount(request, URL_PATH);
        return response.getCount();
    }

    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return new ServerFacade();
    }
}