package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.view.view_interface.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter  {

    protected static final int PAGE_SIZE = 10;
    protected User targetUser;
    protected AuthToken authToken;
    protected T lastItem;
    protected boolean hasMorePages;
    protected boolean isLoading = false;

    PagedPresenter(PagedView<T> view, AuthToken authToken) {
        super(view);
        this.authToken = authToken;
    }

    public void loadMoreItems(User user) {
        isLoading = true;
        this.targetUser =user;
        getView().setLoading(true);
        getItems(authToken,targetUser,PAGE_SIZE,lastItem);
    }

    public void getUser(String alias) {
        getView().displayMessage("Getting user's profile...");
        new UserService().getUser(authToken,alias,new UserObserver());
    }

    abstract void getItems(AuthToken authToken, User targetUser, int PAGE_SIZE, T lastItem);

    abstract String getDescription();

    public boolean isLoading() {
        return isLoading;
    }
    public boolean hasMorePages() {
        return hasMorePages;
    }
    protected PagedView<T> getView() {
        return (PagedView<T>) this.view;
    }



    public class UserObserver implements UserService.GetUserObserver {


        @Override
        public void handleSuccess(User user) {
            getView().navigateToUser(user);
        }

        @Override
        public void handleFailure(String message) {
            getView().displayMessage("Failed to get user's profile: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            getView().displayMessage("Failed to get user's profile because of exception: " + exception.getMessage());
        }


    }

    public class PagedObserver implements edu.byu.cs.tweeter.client.model.service.observer.PagedObserver<T> {

        @Override
        public void handleSuccess(List<T> items, boolean localHasMorePages) {
            getView().setLoading(false);
            isLoading = false;
            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            getView().addItems(items);
            hasMorePages = localHasMorePages;
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get " + getDescription() + ": " + message);
            getView().setLoading(false);
            isLoading = false;
        }

        @Override
        public void handleException(Exception exception) {

            view.displayMessage("Failed to get "+ getDescription() + " because of exception: " + exception.getMessage());
            getView().setLoading(false);
            isLoading = false;
        }

    }

}
