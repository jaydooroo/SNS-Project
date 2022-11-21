package edu.byu.cs.tweeter.client.model.service;
import android.os.Bundle;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {


    public StatusService() {}

    public interface PostStatusObserver extends ServiceObserver {
        void handleSuccess(String message);
    }

    /**
     * Causes the Adapter to display a loading footer and make a request to get more story
     * data.
     */
    public void loadMoreStoryItems(AuthToken currUserAuthToken, User user,
                       int pageSize, Status lastStatus, PagedObserver<Status> getStoryObserver) {
     new BaseTaskService(new GetStoryTask(currUserAuthToken,
                user, pageSize, lastStatus, new PagedHandler<Status>(getStoryObserver))).runExecutor();

    }

    /**
     * Causes the Adapter to display a loading footer and make a request to get more feed
     * data.
     */
    public void loadMoreFeedItems(AuthToken currUserAuthToken, User user,
                              int pageSize, Status lastStatus, PagedObserver<Status> getFeedObserver){
        new BaseTaskService(new GetFeedTask(currUserAuthToken,
                user, pageSize, lastStatus, new PagedHandler<Status>(getFeedObserver))).runExecutor();

    }

    public void initiatePostStatus(AuthToken currUserAuthToken, Status newStatus, PostStatusObserver observer) {
      new BaseTaskService(new PostStatusTask(currUserAuthToken,
                newStatus, new PostStatusHandler(observer))).runExecutor();
    }

    // PostStatusHandler

    private class PostStatusHandler extends BackgroundTaskHandler<PostStatusObserver> {
        public PostStatusHandler(PostStatusObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(PostStatusObserver observer, Bundle data) {
            observer.handleSuccess("Successfully Posted!");
        }
    }
}
