package edu.byu.cs.tweeter.client.model.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.PostStatusPresenter;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.client.view.view_interface.AuthenticationView;
import edu.byu.cs.tweeter.client.view.view_interface.PostStatusView;
import edu.byu.cs.tweeter.client.view.view_interface.StoryView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PostStatusPresenterTest {
    private AuthenticationView mockAuthView;
    private LoginPresenter loginPresenterSpy;
    private AuthenticationObserver authObserver;

    private PostStatusView mockPostStatusView;
    private PostStatusPresenter postStatusPresenterSpy;

    private StoryView mockStoryView;
    private StoryPresenter storyPresenterSpy;
    private TestPagedObserver storyObserver;

    private Cache mockCache;
    private StatusService mockStatusService;
//    private UserService userServiceSpy;

    private String dummyPost;
    private User dummyUser;
    private AuthToken authToken;
    private String dummyLOG_TAG;


    @BeforeEach
    public void setup() throws IOException {
        //Create mocks
//        mockPostStatusView = Mockito.mock(PostStatusView.class);
        authObserver = new AuthenticationObserver();

        mockAuthView = Mockito.mock(AuthenticationView.class);

//        mockUserService = Mockito.mock(UserService.class);
//
//        mockStatusService = Mockito.mock(StatusService.class);
//
//        dummyPost = "dummy";
//        dummyUser = new User();
//        authToken = new AuthToken();
//        dummyLOG_TAG = "dummy_log_tag";
//
//        Mockito.doReturn(mockStatusService).when(postStatusPresenterSpy).getStatusService();
//        Mockito.when(postStatusPresenterSpy.getStatusService()).thenReturn(mockStatusService);


//        userServiceSpy = Mockito.spy(UserService.class);

        loginPresenterSpy = Mockito.spy(new LoginPresenter(mockAuthView));

        Mockito.when(loginPresenterSpy.getView()).thenReturn(mockAuthView);

//        Mockito.when(loginPresenterSpy.getUserService()).thenReturn(userServiceSpy);

        Mockito.when(loginPresenterSpy.getObserver()).thenReturn(authObserver);

        loginPresenterSpy.initiateLogin("@jay", "123");

        Mockito.verify(mockAuthView).displayMessage("Logging in ...");

        Mockito.verify(mockAuthView, Mockito.timeout(1000 * 60 * 10)).displayMessage("Login Succeed");


//        Cache.getInstance().getCurrUserAuthToken();


    }

    @Test
    public void testPostStatus_postStatusSuccessful() {
        storyObserver = new TestPagedObserver();



        mockPostStatusView = Mockito.mock(PostStatusView.class);
        postStatusPresenterSpy = Mockito.spy(new PostStatusPresenter(mockPostStatusView));

        postStatusPresenterSpy.initiatePostStatus("test", authObserver.getUser(), authObserver.getAuthToken(), "dummy");

        Mockito.verify(mockPostStatusView, Mockito.timeout(1000 * 60 * 10)).displayMessage("Successfully Posted!");

        mockStoryView = Mockito.mock(StoryView.class);
        storyPresenterSpy = Mockito.spy(new StoryPresenter(mockStoryView));
        Mockito.when(storyPresenterSpy.getObserver()).thenReturn(storyObserver);

        storyPresenterSpy.loadMoreItems(authObserver.getUser());

        Mockito.verify(mockStoryView, Mockito.timeout(1000 * 60 * 10)).displayMessage("test succeed");

        Assertions.assertEquals("test", storyObserver.getMainItem().getPost());
        Assertions.assertEquals(authObserver.getUser(), storyObserver.getMainItem().getUser());
        Assertions.assertEquals(authObserver.getUser().getUser_alias(), storyObserver.getMainItem().getUser_alias());
        Assertions.assertEquals(0, storyObserver.getMainItem().getMentions().size());
        Assertions.assertEquals(0, storyObserver.getMainItem().getUrls().size());
    }


    private class AuthenticationObserver implements UserService.AuthenticationObserver {

        private AuthToken authToken;
        private User user;
        private String message;
        private Exception exception;

        @Override
        public void handleSuccess(User user, AuthToken authToken) {

            this.authToken = authToken;
            this.user = user;
            mockAuthView.displayMessage("Login Succeed");

        }

        @Override
        public void handleFailure(String message) {

        }

        @Override
        public void handleException(Exception exception) {

        }

        public AuthToken getAuthToken() {
            return authToken;
        }

        public void setAuthToken(AuthToken authToken) {
            this.authToken = authToken;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }
    }

    public class TestPagedObserver implements edu.byu.cs.tweeter.client.model.service.observer.PagedObserver<Status> {

        Status mainItem;

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {

            mockStoryView.displayMessage("test succeed");

            mainItem = items.get(0);
        }

        @Override
        public void handleFailure(String message) {
            mockStoryView.displayMessage("test failed");
        }

        @Override
        public void handleException(Exception exception) {
            mockStoryView.displayMessage("test exception " + exception.getMessage());
        }

        public Status getMainItem() {
            return mainItem;
        }

        public void setMainItem(Status mainItem) {
            this.mainItem = mainItem;
        }
    }
}