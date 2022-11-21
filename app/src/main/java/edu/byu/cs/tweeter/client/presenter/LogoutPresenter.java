package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.view_interface.LogoutView;
import edu.byu.cs.tweeter.client.view.view_interface.View;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class LogoutPresenter extends Presenter {


   public LogoutPresenter(LogoutView view) {
        super(view);
    }

    protected LogoutView getView() {
        return (LogoutView) this.view;
    }

    public void initiateLogout (AuthToken currUserAuthToken) {

        getView().displayLogoutMessage("Logging Out...");
        new UserService().initiateLogout(currUserAuthToken,new LogoutObserver());

    }



    public class LogoutObserver implements UserService.LogoutObserver {
        @Override
        public void handleSuccess() {
            getView().clearLogoutMessage();
            getView().logoutUser();
        }

        @Override
        public void handleFailure(String message) {
            getView().displayLogoutMessage("Failed to logout: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            getView().displayLogoutMessage("Failed to logout because of exception: " + exception.getMessage());
        }
    }
}
