package edu.byu.cs.tweeter.client.presenter;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.view.view_interface.PostStatusView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PostStatusPresenter extends Presenter {

    private StatusService statusService;

    public PostStatusPresenter(PostStatusView view) {
        super(view);
        this.statusService = getStatusService();
    }

    protected StatusService getStatusService() {
        if(statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }

    protected PostStatusView getView() {
        return (PostStatusView) this.view;
    }

    public void initiatePostStatus(String post, User currUser, AuthToken currUserAuthToken, String LOG_TAG) {
        getView().displayPostToast("Posting Status...");

        try {
            Status newStatus = new Status(post, currUser, getFormattedDateTime(), parseURLs(post), parseMentions(post));
             getStatusService().initiatePostStatus(currUserAuthToken,newStatus,new PostStatusObserver());
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            getView().displayMessage("Failed to post the status because of exception: " + ex.getMessage());

        }
    }
    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");
        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }


    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public class PostStatusObserver implements StatusService.PostStatusObserver {
        @Override
        public void handleSuccess(String message) {
            getView().displayMessage(message);
            getView().postStatusSucceeded();
        }

        @Override
        public void handleFailure(String message) {
            getView().displayMessage("Failed to post status: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            getView().displayMessage("Failed to post status because of exception: " + exception.getMessage());
        }
    }

}
