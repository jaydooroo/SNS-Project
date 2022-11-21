package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.view.view_interface.FeedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;



public class FeedPresenter extends PagedPresenter<Status> {

    //    private FeedPresenter.View view;
    private StatusService service;

    public FeedPresenter(FeedView view) {
        super(view, Cache.getInstance().getCurrUserAuthToken());
        this.service = new StatusService();
    }

    @Override
    void getItems(AuthToken authToken, User targetUser, int PAGE_SIZE, Status lastItem){
        service.loadMoreFeedItems(authToken, targetUser, PAGE_SIZE, lastItem, new PagedObserver());
    }

    @Override
    String getDescription() {
        return "feed";
    }

    @Override
    protected FeedView getView () {
        return (FeedView) this.view;
    }

}
