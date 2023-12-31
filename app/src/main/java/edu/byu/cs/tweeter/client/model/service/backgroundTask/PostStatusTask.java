package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthenticatedTask {
    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */

    private ServerFacade serverFacade;
    private static final String LOG_TAG = "PostStatusTask";
    public static final String URL_PATH = "/poststatus";

    private Status status;


    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(authToken,messageHandler);
        this.status = status;
    }

    @Override
    protected void runTask() throws IOException {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually access the database from here when we aren't using dummy data.

        try {
            PostStatusRequest request = new PostStatusRequest(this.status,this.authToken);

            PostStatusResponse response =  getServerFacade().postStatus(request, URL_PATH);

            if(response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
            // Call sendSuccessMessage if successful

            // or call sendFailedMessage if not successful
            // sendFailedMessage()

        } catch (Exception ex) {
            Log.e(LOG_TAG, "Failed to post status", ex);
            sendExceptionMessage(ex);
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {

    }

    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return new ServerFacade();
    }
}
