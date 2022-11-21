package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticateTask {
    private static final String LOG_TAG = "RegisterTask";
    public static final String URL_PATH = "/register";
    public static final String SUCCESS_KEY = "success";
    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    /**
     * The user's first name.
     */
    private String firstName;
    /**
     * The user's last name.
     */
    private String lastName;
    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private String image;
//
    private ServerFacade serverFacade;

    public RegisterTask(String firstName, String lastName, String username, String password,
                        String image, Handler messageHandler) {
        super(username,password,messageHandler);
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
    }

    @Override
    protected void showExceptionMessage(Exception ex) {
        Log.e(LOG_TAG, ex.getMessage(), ex);
        sendExceptionMessage(ex);
    }

    @Override
    protected RegisterResponse runAuthenticationTask() throws IOException, TweeterRemoteException {

        RegisterRequest request = new RegisterRequest(firstName,lastName, image, getUsername(),getPassword());
        RegisterResponse response = getServerFacade().register(request, URL_PATH);

        return response;
    }

    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return new ServerFacade();
    }

//    @Override
//    protected Pair<User, AuthToken> runAuthenticationTask() {
//        User registeredUser = getFakeData().getFirstUser();
//        AuthToken authToken = getFakeData().getAuthToken();
//        return new Pair<>(registeredUser,authToken);
//    }
//    @Override
//    public void run() {
//        try {
//            Pair<User, AuthToken> registerResult = doRegister();
//
//            User registeredUser = registerResult.getFirst();
//            AuthToken authToken = registerResult.getSecond();
//
//            sendSuccessMessage(registeredUser, authToken);
//
//        } catch (Exception ex) {
//            Log.e(LOG_TAG, ex.getMessage(), ex);
//            sendExceptionMessage(ex);
//        }
//    }

//    private FakeData getFakeData() {
//        return FakeData.getInstance();
//    }

//    private Pair<User, AuthToken> doRegister() {
//        User registeredUser = getFakeData().getFirstUser();
//        AuthToken authToken = getFakeData().getAuthToken();
//        return new Pair<>(registeredUser, authToken);
//    }

//    private void sendSuccessMessage(User registeredUser, AuthToken authToken) {
//        Bundle msgBundle = new Bundle();
//        msgBundle.putBoolean(SUCCESS_KEY, true);
//        msgBundle.putSerializable(USER_KEY, registeredUser);
//        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
//
//        Message msg = Message.obtain();
//        msg.setData(msgBundle);
//
//        messageHandler.sendMessage(msg);
//    }
//
//    private void sendFailedMessage(String message) {
//        Bundle msgBundle = new Bundle();
//        msgBundle.putBoolean(SUCCESS_KEY, false);
//        msgBundle.putString(MESSAGE_KEY, message);
//
//        Message msg = Message.obtain();
//        msg.setData(msgBundle);
//
//        messageHandler.sendMessage(msg);
//    }
//
//    private void sendExceptionMessage(Exception exception) {
//        Bundle msgBundle = new Bundle();
//        msgBundle.putBoolean(SUCCESS_KEY, false);
//        msgBundle.putSerializable(EXCEPTION_KEY, exception);
//
//        Message msg = Message.obtain();
//        msg.setData(msgBundle);
//
//        messageHandler.sendMessage(msg);
//    }
}
