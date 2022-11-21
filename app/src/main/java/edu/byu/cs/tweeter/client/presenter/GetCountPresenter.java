package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.view.view_interface.CountView;
import edu.byu.cs.tweeter.client.view.view_interface.View;

public abstract class GetCountPresenter extends Presenter {


    GetCountPresenter(CountView view) {
        super(view);
    }

    protected CountView getView() {
        return (CountView) this.view;
    }
    abstract String getDescription();
    abstract void count(int count);
    public GetCountObserver getObserver () {
        return new GetCountObserver();
    }


    public class GetCountObserver implements FollowService.GetCountObserver {

        @Override
        public void handleSuccess(int count) {
            count(count);
        }

        @Override
        public void handleFailure(String message) {
            getView().displayMessage("Failed to get " + getDescription() +": " + message);
        }

        @Override
        public void handleException(Exception exception) {
           getView().displayMessage("Failed to get " + getDescription() + "because of exception: " + exception.getMessage());
        }
    }
}
