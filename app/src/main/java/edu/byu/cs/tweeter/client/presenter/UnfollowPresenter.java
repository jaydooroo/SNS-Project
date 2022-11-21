package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.view.view_interface.FollowAndUnfollowView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowPresenter extends FollowAndUnfollowPresenter{


    public UnfollowPresenter(FollowAndUnfollowView view, GetCountPresenter.GetCountObserver getFollowersCountObserver, GetCountPresenter.GetCountObserver getFollowingCountObserver) {
        super(view, getFollowersCountObserver, getFollowingCountObserver);
    }

    @Override
    String getDescription() {
        return "unfollow";
    }

    @Override
    void succeeded() {
        getView().unfollowSucceeded();
    }

    public void initiateUnfollow (AuthToken currUserAuthToken, User selectedUser) {

        new FollowService().initiateUnfollow(currUserAuthToken,selectedUser,new FollowAndUnfollowObserver());
        getView().displayFollowAndUnFollowMessage("Removing " + selectedUser.getName() + "...");
    }
}
