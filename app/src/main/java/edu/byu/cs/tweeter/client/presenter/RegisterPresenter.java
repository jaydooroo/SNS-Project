package edu.byu.cs.tweeter.client.presenter;

import android.widget.ImageView;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.view_interface.AuthenticationView;

public class RegisterPresenter extends AuthenticationPresenter  {

    public RegisterPresenter(AuthenticationView view) {
        super(view);
    }

    @Override
    String getDescription() {
        return "register";
    }

    public void initiateRegister(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        try {
            validateRegistration(firstName,lastName,alias,password,imageToUpload);

           getView().clearErrorMessage();
           getView().displayMessage( "Registering...");
           new UserService().register(firstName,lastName,alias,password,imageToUpload,new AuthenticationObserver());
        } catch (Exception e) {
            getView().clearMessage();
            getView().displayErrorMessage(e.getMessage());
        }

    }
    public void validateRegistration(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageToUpload.getDrawable() == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }

}
