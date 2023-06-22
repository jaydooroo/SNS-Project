package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {

    public interface AuthenticationObserver extends ServiceObserver {

        void handleSuccess(User user,AuthToken authToken);

    }


    public interface GetUserObserver extends ServiceObserver{

        void handleSuccess(User user);

    }

    public interface LogoutObserver extends ServiceObserver {

        void handleSuccess();

    }



    // Run the LoginTask in the background to lof the user in
    public void login (String username, String password, AuthenticationObserver observer) {

        // Send the login request. // service
        new BaseTaskService(new LoginTask(username, password, new AuthenticationHandler(observer))).runExecutor();

    }

    public void register(String firstName, String lastName, String alias, String password, ImageView imageToUpload, AuthenticationObserver observer) {

        // Convert image to byte array.
        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

        new BaseTaskService( new RegisterTask(firstName, lastName,
                alias, password, imageBytesBase64, new AuthenticationHandler(observer))).runExecutor();
    }

    public void getUser(AuthToken currUserAuthToken, String alias, GetUserObserver observer) {

        //code doesnt belong here. need to move later
       new BaseTaskService(new GetUserTask(currUserAuthToken,
                alias, new GetUserHandler(observer))).runExecutor();
    }

    public void initiateLogout (AuthToken currUserAuthToken, LogoutObserver observer) {

        new BaseTaskService( new LogoutTask(currUserAuthToken, new LogoutHandler(observer))).runExecutor();
    }

    private class AuthenticationHandler extends BackgroundTaskHandler<AuthenticationObserver> {

        public AuthenticationHandler(AuthenticationObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(AuthenticationObserver observer, Bundle data) {

            User authenticatedUser = (User) data.getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(LoginTask.AUTH_TOKEN_KEY);

            // Cache user session information
            Cache.getInstance().setCurrUser(authenticatedUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);
            observer.handleSuccess(authenticatedUser, authToken);
        }
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private class GetUserHandler extends BackgroundTaskHandler<GetUserObserver> {
        public GetUserHandler(GetUserObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(GetUserObserver observer, Bundle data) {
            User user = (User) data.getSerializable(GetUserTask.USER_KEY);
            observer.handleSuccess(user);
        }
    }


    // LogoutHandler
    private class LogoutHandler extends BackgroundTaskHandler<LogoutObserver> {
        public LogoutHandler(LogoutObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(LogoutObserver observer, Bundle data) {
            observer.handleSuccess();
        }
    }
}
