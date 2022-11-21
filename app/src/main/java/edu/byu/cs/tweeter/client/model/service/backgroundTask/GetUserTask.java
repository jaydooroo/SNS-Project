package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthenticatedTask {
    private static final String LOG_TAG = "GetUserTask";

    public static final String SUCCESS_KEY = "success";
    public static final String USER_KEY = "user";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";


    private ServerFacade serverFacade;
    public static final String URL_PATH = "/getuser";

    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private String alias;

    private User user;


    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(authToken,messageHandler);
        this.alias = alias;
    }

    @Override
    protected void runTask() throws IOException {

        try {
            GetUserRequest request = new GetUserRequest(this.alias,this.authToken);

            GetUserResponse response =  getServerFacade().getUser(request, URL_PATH);

            if(response.isSuccess()) {
                this.user = response.getUser();
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
            // Call sendSuccessMessage if successful

            // or call sendFailedMessage if not successful
            // sendFailedMessage()

        } catch (Exception ex) {
            Log.e(LOG_TAG, "Failed to get user", ex);
            sendExceptionMessage(ex);
        }


//        user = getUser();
//
//        // Call sendSuccessMessage if successful
//        sendSuccessMessage();
        // or call sendFailedMessage if not successful
        // sendFailedMessage()
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, user);
    }

        private User getUser() {
        User user = getFakeData().findUserByAlias(alias);
        return user;
    }

    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return new ServerFacade();
    }
}
