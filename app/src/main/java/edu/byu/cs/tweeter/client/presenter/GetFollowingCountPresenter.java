package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.view.view_interface.CountView;

public class GetFollowingCountPresenter extends GetCountPresenter {
    public GetFollowingCountPresenter(CountView view) {
        super(view);
    }

    @Override
    String getDescription() {
        return "following count";
    }

    @Override
    void count(int count) {
        getView().showFollowingCount(count);
    }
}
