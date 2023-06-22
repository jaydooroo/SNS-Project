package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.view_interface.AuthenticationView;
import edu.byu.cs.tweeter.client.view.view_interface.PostStatusView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class PostStatusPresenterUnitTest {
    private PostStatusView mockPostStatusView;
    private AuthenticationView mockAuthView;
    private Cache mockCache;
    private StatusService mockStatusService;
    private UserService mockUserService;
    private LoginPresenter loginPresenterSpy;
    private PostStatusPresenter postStatusPresenterSpy;
    private String dummyPost;
    private User dummyUser;
    private AuthToken authToken;
    private String dummyLOG_TAG;


    @BeforeEach
    public void setup() throws IOException {
        //Create mocks
        mockPostStatusView = Mockito.mock(PostStatusView.class);

        mockAuthView = Mockito.mock(AuthenticationView.class);

        mockUserService = Mockito.mock(UserService.class);

        mockStatusService = Mockito.mock(StatusService.class);

        dummyPost = "dummy";
        dummyUser = new User();
        authToken = new AuthToken();
        dummyLOG_TAG = "dummy_log_tag";

        postStatusPresenterSpy =  Mockito.spy(new PostStatusPresenter(mockPostStatusView));
//        Mockito.doReturn(mockStatusService).when(postStatusPresenterSpy).getStatusService();
        Mockito.when(postStatusPresenterSpy.getStatusService()).thenReturn(mockStatusService);



        loginPresenterSpy = Mockito.spy(new LoginPresenter(mockAuthView));

        Mockito.when(loginPresenterSpy.getView()).thenReturn(mockAuthView);

        loginPresenterSpy.initiateLogin("@jay", "123");

        Mockito.verify(mockAuthView).displayMessage("Hello jay");


//        Cache.getInstance().getCurrUserAuthToken();


    }

    @Test
    public void testPostStatus_postStatusSuccessful() {

        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {

                Assertions.assertEquals(authToken, invocation.getArgument(0));
                StatusService.PostStatusObserver observer = invocation.getArgument(2,StatusService.PostStatusObserver.class);
                observer.handleSuccess("Successfully Posted!");

                return null;
            }
        };

        testPostingStatus(answer, "Successfully Posted!");
        Mockito.verify(mockPostStatusView).postStatusSucceeded();
    }

    @Test
    public void testPostStatus_postStatusFailed() {

        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2,StatusService.PostStatusObserver.class);
                observer.handleFailure("dummyString");
                return null;
            }
        };

        testPostingStatus(answer, "Failed to post status: " + "dummyString");
    }

    @Test
    public void testPostStatus_postStatusFailedWithException() {

        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.PostStatusObserver observer = invocation.getArgument(2,StatusService.PostStatusObserver.class);
                observer.handleException(new Exception("My Exception"));
                return null;
            }
        };

        testPostingStatus(answer, "Failed to post status because of exception: " + "My Exception");

    }

    private void testPostingStatus(Answer<Void> answer, String dummyString) {
        // need to ask if this one works or not.  and what it means to creat a status object and call a service to send it to the server.
        Mockito.doAnswer(answer).when(mockStatusService).initiatePostStatus(Mockito.any(), Mockito.any(), Mockito.any());
        postStatusPresenterSpy.initiatePostStatus(dummyPost, dummyUser, authToken, dummyLOG_TAG);

        Mockito.verify(mockPostStatusView).displayPostToast("Posting Status...");
        Mockito.verify(mockPostStatusView).displayMessage(dummyString);
    }



}
