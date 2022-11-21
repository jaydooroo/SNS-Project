package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.view.view_interface.FollowAndUnfollowView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowPresenter extends FollowAndUnfollowPresenter{


    public FollowPresenter(FollowAndUnfollowView view,
                           GetCountPresenter.GetCountObserver getFollowersCountObserver,
                           GetCountPresenter.GetCountObserver getFollowingCountObserver) {
        super(view, getFollowersCountObserver, getFollowingCountObserver);
    }

    @Override
    String getDescription() {
        return "follow";
    }

    @Override
    void succeeded() {
        getView().followSucceeded();
    }


    public void initiateFollow (AuthToken currUserAuthToken, User selectedUser) {
        new FollowService().initiateFollow(currUserAuthToken,selectedUser,new FollowAndUnfollowObserver());
        getView().displayFollowAndUnFollowMessage("Adding " + selectedUser.getName() + "...");
    }
}
