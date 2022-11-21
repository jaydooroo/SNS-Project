package edu.byu.cs.tweeter.client.view.view_interface;

public interface FollowAndUnfollowView extends  View{
    void followSucceeded();
    void unfollowSucceeded();
    void displayFollowAndUnFollowMessage(String message);



}
