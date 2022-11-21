package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.view.view_interface.FollowingView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;


public class FollowingPresenter extends  PagedPresenter<User> {

    private FollowService service;

    public FollowingPresenter(FollowingView view) {
        super(view,Cache.getInstance().getCurrUserAuthToken());
        service = new FollowService();
    }

    @Override
    void getItems(AuthToken authToken, User targetUser, int PAGE_SIZE, User lastItem) {
        service.loadMoreFollowingItems(Cache.getInstance().getCurrUserAuthToken(), targetUser, PAGE_SIZE, lastItem, new PagedObserver());
    }

    @Override
    String getDescription() {
        return "following";
    }

    @Override
    protected FollowingView getView() {
        return (FollowingView) this.view;
    }

}
