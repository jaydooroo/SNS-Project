package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.view.view_interface.FollowAndUnfollowView;
import edu.byu.cs.tweeter.client.view.view_interface.View;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class FollowAndUnfollowPresenter extends Presenter {

    GetCountPresenter.GetCountObserver getFollowersCountObserver;
    GetCountPresenter.GetCountObserver getFollowingCountObserver;


    public FollowAndUnfollowPresenter(FollowAndUnfollowView view, GetCountPresenter.GetCountObserver getFollowersCountObserver,
                                      GetCountPresenter.GetCountObserver getFollowingCountObserver) {
        super(view);
        this.getFollowersCountObserver = getFollowersCountObserver;
        this.getFollowingCountObserver = getFollowingCountObserver;

    }

    protected FollowAndUnfollowView getView() {
        return  (FollowAndUnfollowView) this.view;
    }

    abstract String getDescription();

    abstract void succeeded();

    public void updateSelectedUserFollowingAndFollowers(AuthToken currUserAuthToken, User selectedUser) {

        new FollowService().updateSelectedUserFollowingAndFollowers(currUserAuthToken,selectedUser,
                getFollowersCountObserver,getFollowingCountObserver);
    }

    class FollowAndUnfollowObserver implements FollowService.FollowAndUnFollowObserver {

        @Override
        public void handleSuccess(AuthToken currUserAuthToken, User selectedUser) {
          updateSelectedUserFollowingAndFollowers(currUserAuthToken,
                    selectedUser);
           succeeded();
        }

        @Override
        public void handleFailure(String message) {
            getView().displayFollowAndUnFollowMessage("Failed to "+ getDescription() +": " + message);
        }

        @Override
        public void handleException(Exception exception) {
            getView().displayFollowAndUnFollowMessage("Failed to" + getDescription() + "because of exception: " + exception.getMessage());
        }
    }

}
