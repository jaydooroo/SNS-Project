package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class FacadeTest {

    private User currentUser;
    private AuthToken currentAuthToken;

    private ServerFacade serverFacade;

    private CountDownLatch countDownLatch;

    @BeforeEach
    public void setup() {
        currentUser = FakeData.getInstance().getFirstUser();
        currentAuthToken = new AuthToken();

        serverFacade = new ServerFacade();

    }


    @Test
    public void testRegister_validRequest_correctResposne () {



        RegisterResponse expectedResponse = new RegisterResponse( FakeData.getInstance().getFirstUser(),FakeData.getInstance().getAuthToken());
        RegisterRequest request = new RegisterRequest(currentUser.getFirstName(),currentUser.getLastName(),currentUser.getImageUrl(),"aaa","bbb");
        try {
            RegisterResponse response = serverFacade.register(request, "/register");
            Assertions.assertEquals(expectedResponse,response);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testgetfollowers_validRequest_correctResposne () {



        FollowersResponse expectedResponse = new FollowersResponse(true,FakeData.getInstance().getFakeUsers().subList(0,10));
        FollowersRequest request = new FollowersRequest(this.currentAuthToken,currentUser.getUser_alias(),10,null);
        try {
            FollowersResponse response = serverFacade.getFollowers(request, "/getfollowers");
            Assertions.assertEquals(expectedResponse,response);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testgetfollowingcount_validRequest_correctResposne () {



        FollowingCountResponse expectedResponse = new FollowingCountResponse(20);
        FollowingCountRequest request = new FollowingCountRequest(currentUser,currentAuthToken);
        try {
            FollowingCountResponse response = serverFacade.getFollowingCount(request, "/getfollowingcount");
            Assertions.assertEquals(expectedResponse,response);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (TweeterRemoteException e) {
            e.printStackTrace();
        }
    }

}
