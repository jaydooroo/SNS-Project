package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.view_interface.AuthenticationView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticationPresenter extends Presenter {


    AuthenticationPresenter(AuthenticationView view) {
        super(view);
    }

    public AuthenticationView getView() {
        return (AuthenticationView)this.view;
    }

    abstract String getDescription();


    public class AuthenticationObserver implements UserService.AuthenticationObserver {

        @Override
        public void handleSuccess(User user, AuthToken authToken) {
            try {
                view.displayMessage("Hello " + user.getFirstName());
                getView().clearErrorMessage();
                getView().navigateToUser(user);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        @Override
        public void handleFailure(String message) {
            getView().clearMessage();
            getView().displayErrorMessage("Failed to " +  getDescription() + ": " + message);
        }

        @Override
        public void handleException(Exception exception) {
            getView().clearMessage();
            getView().displayErrorMessage( "Failed to "+ getDescription() +" because of exception: " + exception.getMessage());
        }

    }

}
