package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.view_interface.AuthenticationView;

public class LoginPresenter extends AuthenticationPresenter  {


    public LoginPresenter(AuthenticationView view) {
            super(view);
    }

    @Override
    String getDescription() {
        return "login";
    }

    public void initiateLogin(String username, String password ) {

           String message = validateLogin(username, password); // presenter
            if(message == null ) {

                getView().clearErrorMessage();
                getView().displayMessage("Logging in ...");
                getUserService().login(username, password, getObserver());

            }
            else {

                getView().clearMessage();
                getView().displayErrorMessage(message);

            }

        }

    public String validateLogin(String username, String password) {

        if (username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }

        return null;
    }

    public UserService getUserService() {
        return new UserService();
    }

    public UserService.AuthenticationObserver getObserver () {
        return new AuthenticationObserver();
    }

}
