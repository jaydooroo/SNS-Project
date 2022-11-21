package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.view.view_interface.FollowersView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> {

    private FollowService service;

    public FollowersPresenter (FollowersView view) {
        super(view,Cache.getInstance().getCurrUserAuthToken());
        service = new FollowService();
    }

    @Override
    void getItems(AuthToken authToken, User targetUser, int PAGE_SIZE, User lastItem) {
        service.loadMoreFollowersItems(Cache.getInstance().getCurrUserAuthToken(), targetUser, PAGE_SIZE,lastItem, new PagedObserver());
    }

    @Override
    String getDescription() {
        return "followers";
    }
    @Override
    protected FollowersView getView() {
        return (FollowersView) this.view;
    }

}
