package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.view.view_interface.CountView;

public class GetFollowersCountPresenter extends GetCountPresenter {
    public GetFollowersCountPresenter(CountView view) {
        super(view);
    }

    @Override
    String getDescription() {
        return "followers count";
    }

    @Override
    void count(int count) {
        getView().showFollowersCount(count);
    }
}
