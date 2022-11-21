package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Random;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {

    public static final String IS_FOLLOWER_KEY = "is-follower";


    private ServerFacade serverFacade;
    public static final String URL_PATH = "/isfollower";
    /**
     * The alleged follower.
     */
    private User follower;
    /**
     * The alleged followee.
     */
    private User followee;

    private boolean isFollower;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken,messageHandler);
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected void runTask() throws IOException {



        try {
            IsFollowerRequest request = new IsFollowerRequest(this.authToken, this.follower,this.followee);

            IsFollowerResponse response =  getServerFacade().isFollower(request, URL_PATH);

            if(response.isSuccess()) {
                this.isFollower = response.isFollower();
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
            // Call sendSuccessMessage if successful

            // or call sendFailedMessage if not successful
            // sendFailedMessage()

        } catch (Exception ex) {
            Log.e(IS_FOLLOWER_KEY, "Failed to discern if it is a follower", ex);
            sendExceptionMessage(ex);
        }


//        isFollower = new Random().nextInt() > 0;
//        sendSuccessMessage();
    }
    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {

        msgBundle.putBoolean(IS_FOLLOWER_KEY,isFollower);
    }

    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return new ServerFacade();
    }
}
