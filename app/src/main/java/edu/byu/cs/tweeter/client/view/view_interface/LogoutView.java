package edu.byu.cs.tweeter.client.view.view_interface;

public interface LogoutView extends View{
    void logoutUser();
    void displayLogoutMessage(String message);
    void clearLogoutMessage();
}
