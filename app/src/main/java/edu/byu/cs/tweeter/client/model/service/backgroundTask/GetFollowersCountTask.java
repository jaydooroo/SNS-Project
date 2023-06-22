package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {

    private ServerFacade serverFacade;
    private static final String LOG_TAG = "GetFollowerCountTask";
    public static final String URL_PATH = "/getfollowercount";


    public GetFollowersCountTask(AuthToken authToken, User targetUser
            , Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected void showExceptionMessage(Exception ex) {
        Log.e(LOG_TAG, "Failed to get follower count", ex);
        sendExceptionMessage(ex);
    }

    @Override
    protected int runCountTask() throws IOException, TweeterRemoteException {
        FollowerCountRequest request = new FollowerCountRequest(getTargetUser(),this.authToken);
       FollowerCountResponse response = getServerFacade().getFollowerCount(request,URL_PATH);
       System.out.println(response.getCount());
       return response.getCount();
    }


    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return new ServerFacade();
    }
}
