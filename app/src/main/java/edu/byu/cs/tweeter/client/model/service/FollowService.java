package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.observer.ServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public FollowService() {}

    public interface GetCountObserver extends ServiceObserver {
        void handleSuccess(int count);

}

    public interface IsFollowerObserver extends ServiceObserver {

        void handleSuccess(boolean isFollower);
    }

    public interface FollowAndUnFollowObserver extends ServiceObserver {
        void handleSuccess(AuthToken currUserAuthToken, User selectedUser);

    }


    public void loadMoreFollowingItems(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> getFollowingObserver) {

        BaseTaskService getFollowingTaskService = new BaseTaskService(new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new PagedHandler<User>(getFollowingObserver)));
        getFollowingTaskService.runExecutor();
    }

    public void loadMoreFollowersItems(AuthToken currUserAuthToken, User user, int pageSize, User lastFollower, PagedObserver<User> getFollowersObserver) {

      new BaseTaskService(new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollower, new PagedHandler<User>(getFollowersObserver))).runExecutor();
    }


    public void updateSelectedUserFollowingAndFollowers(AuthToken currUserAuthToken, User selectedUser, GetCountObserver followersCountObserver, GetCountObserver followingCountObserver ) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Get count of most recently selected user's followers.
      new BaseTaskService(new GetFollowersCountTask(currUserAuthToken,
                selectedUser, new GetFollowCountHandler(followersCountObserver))).runExecutor(executor);

        // Get count of most recently selected user's followees (who they are following)
      new BaseTaskService(new GetFollowingCountTask(currUserAuthToken,
                selectedUser, new GetFollowCountHandler(followingCountObserver))).runExecutor(executor);
    }

    public void initiateIsFollower(AuthToken currUserAuthToken, User currUser, User selectedUser, IsFollowerObserver observer) {

       new BaseTaskService(new IsFollowerTask(currUserAuthToken,
                currUser, selectedUser, new IsFollowerHandler(observer))).runExecutor();
    }

    public void initiateFollow (AuthToken currUserAuthToken, User selectedUser, FollowAndUnFollowObserver observer) {

       new BaseTaskService( new FollowTask(currUserAuthToken,
                selectedUser, new FollowAndUnFollowHandler(observer, currUserAuthToken, selectedUser))).runExecutor();


    }

    public void initiateUnfollow (AuthToken currUserAuthToken, User selectedUser, FollowAndUnFollowObserver observer) {

        new BaseTaskService(new UnfollowTask(currUserAuthToken,
                selectedUser, new FollowAndUnFollowHandler(observer,currUserAuthToken,selectedUser))).runExecutor();

    }


    // GetFollowingCountHandler
    private class GetFollowCountHandler extends BackgroundTaskHandler<GetCountObserver> {

        public GetFollowCountHandler(GetCountObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(GetCountObserver observer, Bundle data) {
            int count = data.getInt(GetCountTask.COUNT_KEY);
            observer.handleSuccess(count);
        }
    }

    // IsFollowerHandler

    private class IsFollowerHandler extends BackgroundTaskHandler<IsFollowerObserver> {

        public IsFollowerHandler(IsFollowerObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(IsFollowerObserver observer, Bundle data) {
            boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
                observer.handleSuccess(isFollower);
        }
    }



    private class FollowAndUnFollowHandler extends BackgroundTaskHandler<FollowAndUnFollowObserver> {

        private  AuthToken currUserAuthToken;
        private User selectedUser;

        public FollowAndUnFollowHandler(FollowAndUnFollowObserver observer, AuthToken currUserAuthToken, User selectedUser ) {
            super(observer);
            this.currUserAuthToken = currUserAuthToken;
            this. selectedUser = selectedUser;
        }

        @Override
        protected void handleSuccessMessage(FollowAndUnFollowObserver observer, Bundle data) {
            observer.handleSuccess(currUserAuthToken,selectedUser);
        }
    }
}
