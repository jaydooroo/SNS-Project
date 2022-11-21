package edu.byu.cs.tweeter.client.view.view_interface;

import edu.byu.cs.tweeter.model.domain.User;

public interface AuthenticationView  extends View{
    void clearMessage();

    void displayErrorMessage (String message);
    void clearErrorMessage();

    void navigateToUser(User user);
}
