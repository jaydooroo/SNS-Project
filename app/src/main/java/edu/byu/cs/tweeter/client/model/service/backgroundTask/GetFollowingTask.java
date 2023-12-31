package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.PagedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask {

    private ServerFacade serverFacade;
    private static final String LOG_TAG = "GetFollowingTask";
    public static final String URL_PATH = "/getfollowing";

    @Override
    protected PagedResponse getResponse() throws IOException, TweeterRemoteException {


            String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getUser_alias();
            String lastFolloweeAlias = getLastItem() == null ? null : getLastItem().getUser_alias();

            FollowingRequest request = new FollowingRequest(authToken, targetUserAlias, limit, lastFolloweeAlias);
            FollowingResponse response = getServerFacade().getFollowees(request, URL_PATH);

            return response;

    }

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(authToken,targetUser,limit,lastFollowee,messageHandler);
    }

    @Override
    protected void showExceptionMessage(Exception ex) {
        Log.e(LOG_TAG, "Failed to get followees", ex);
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
