package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;

public abstract class AuthenticateTask extends BackgroundTask {

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    protected User authenticatedUser;

    protected AuthToken authToken;

    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    private String username;

    /**
     * The user's password.
     */
    private String password;

    protected AuthenticateTask(String username,String password,Handler messageHandler) {
        super(messageHandler);
        this.username = username;
        this.password = password;
    }

    @Override
    protected final void runTask() throws IOException {
        try {
            AuthenticateResponse response = runAuthenticationTask();

            if(response.isSuccess()) {
                authenticatedUser = response.getUser();
                authToken = response.getAuthToken();

                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception e) {
            showExceptionMessage(e);
        }

    }

    protected abstract void showExceptionMessage(Exception ex);
    protected abstract AuthenticateResponse runAuthenticationTask() throws IOException, TweeterRemoteException;

    //    protected abstract Pair<User, AuthToken> runAuthenticationTask();
    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY,this.authenticatedUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY,this.authToken);
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
