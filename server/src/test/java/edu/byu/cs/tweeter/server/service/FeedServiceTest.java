package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.dao.FeedDAODynamo;

public class FeedServiceTest {
    private FeedRequest request;
    private FeedResponse expectedResponse;
    private FeedDAODynamo mockFeedDAODynamo;
    private StatusService statusServiceSpy;

    @BeforeEach
    public void setup() {
        AuthToken authToken = new AuthToken();

        User currentUser = new User("FirstName", "LastName", null);

//        User resultUser1 = new User("FirstName1", "LastName1",
//                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
//        User resultUser2 = new User("FirstName2", "LastName2",
//                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
//        User resultUser3 = new User("FirstName3", "LastName3",
//                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");


        List<String> urls = new ArrayList<>();
        urls.add("Aaa");
        urls.add("ASdsasda");

        List<String> mentions = new ArrayList<>();
        mentions.add("Aa");
        mentions.add("assdfad");

        Status resultStatus1 = new Status("yes", currentUser, "2222", urls, mentions);
        List<Status> items = new ArrayList<Status>();

        items.add(resultStatus1);
        items.add(resultStatus1);

        // Setup a request object to use in the tests

        request = new FeedRequest(authToken,currentUser.getUser_alias(),3,resultStatus1);
        expectedResponse = new FeedResponse(true,items );
//        request = new FollowingRequest(authToken, currentUser.getAlias(), 3, null);

        // Setup a mock FollowDAO that will return known responses
        mockFeedDAODynamo = Mockito.mock(FeedDAODynamo.class);
//        Mockito.when(mockFeedDAODynamo.getFeeds(request.getTargetUserAlias(),request.getLimit(), request.getLastFeed())).thenReturn(expectedResponse);

        statusServiceSpy = Mockito.spy(StatusService.class);
        Mockito.when(statusServiceSpy.getFeedDao()).thenReturn(mockFeedDAODynamo);
    }

    /**
     * Verify that the {@link FollowService#getFollowees(FollowingRequest)}
     * method returns the same result as the {@link edu.byu.cs.tweeter.server.dao.FollowsDAODynamo} class.
     */
    @Test
    public void testGetFollowees_validRequest_correctResponse() {
        FeedResponse response = statusServiceSpy.getFeeds(request);
        Assertions.assertEquals(expectedResponse, response);
    }
}
