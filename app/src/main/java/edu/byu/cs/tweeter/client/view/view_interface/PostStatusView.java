package edu.byu.cs.tweeter.client.view.view_interface;

public interface PostStatusView extends View {

    void postStatusSucceeded();
    void displayPostToast(String message);
}
