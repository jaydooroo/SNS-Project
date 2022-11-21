package edu.byu.cs.tweeter.client.view.view_interface;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public interface PagedView <T> extends View {
    void setLoading(boolean isLoading);
    void addItems(List<T> items);
    void navigateToUser(User user);

}
