package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.view.view_interface.IsFollowerView;
import edu.byu.cs.tweeter.client.view.view_interface.View;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowPresenter extends Presenter {


    public IsFollowPresenter(IsFollowerView view) {
        super(view);
    }

    protected IsFollowerView getView() {
        return (IsFollowerView) this.view;
    }

    public void initiateIsFollower (AuthToken currUserAuthToken, User currUser, User selectedUser) {
        new FollowService().initiateIsFollower(currUserAuthToken,currUser,selectedUser,new IsFollowerObserver());
    }

    private class IsFollowerObserver implements FollowService.IsFollowerObserver {

        @Override
        public void handleSuccess(boolean isFollower) {
            getView().isFollower(isFollower);
        }
        @Override
        public void handleFailure(String message) {
            getView().displayMessage("Failed to determine following relationship: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            getView().displayMessage("Failed to determine following relationship because of exception: " + exception.getMessage());
        }


    }

}
