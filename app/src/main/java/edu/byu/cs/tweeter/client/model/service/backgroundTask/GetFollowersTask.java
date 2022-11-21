package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    private ServerFacade serverFacade;
    private static final String LOG_TAG = "GetFollowersTask";
    public static final String URL_PATH = "/getfollowers";


    @Override
    protected PagedResponse getResponse() throws IOException, TweeterRemoteException {

        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
        String lastFollowerAlias = getLastItem() == null ? null : getLastItem().getAlias();

        FollowersRequest request = new FollowersRequest(authToken, targetUserAlias, limit, lastFollowerAlias);
        FollowersResponse response = getServerFacade().getFollowers(request,URL_PATH);

        return response;
    }

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken,targetUser,limit,lastFollower,messageHandler);
    }

    @Override
    protected void showExceptionMessage(Exception ex) {
        Log.e(LOG_TAG, "Failed to get followers", ex);
        sendExceptionMessage(ex);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
      return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
    }

    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return new ServerFacade();
    }
}
