package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    private ServerFacade serverFacade;

    private static final String LOG_TAG = "LoginTask";
    public static final String URL_PATH = "/login";



    public LoginTask(String username, String password, Handler messageHandler) {
        super(username,password,messageHandler);
    }

    @Override
    protected void showExceptionMessage(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
        sendExceptionMessage(ex);
    }

    @Override
    protected LoginResponse runAuthenticationTask() throws IOException, TweeterRemoteException {

        LoginRequest request = new LoginRequest(getUsername(), getPassword());
        LoginResponse response = getServerFacade().login(request, URL_PATH);

        return response;
    }

//    @Override
//    protected Pair<User, AuthToken> runAuthenticationTask() {
//        User loggedInUser = getFakeData().getFirstUser();
//        AuthToken authToken = getFakeData().getAuthToken();
//        return new Pair<>(loggedInUser, authToken);
//    }

    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return new ServerFacade();
    }
}
