package edu.byu.cs.tweeter.server.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.amazonaws.services.dynamodbv2.xspec.S;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.Calendar;

import javax.imageio.ImageIO;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.UserDAODynamo;

public class UserServiceTest {

    private RegisterRequest request;
    private User expectedResponse;
    private UserDAODynamo mockUserDAODynamo;
    private UserService userServiceSpy;



    @BeforeEach
    public void setup() throws IOException {


        BufferedImage img =  ImageIO.read(new URL("https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png"));

        byte[] bytes = toByteArray(img, "jpg");

        String imageBytesBase64 = Base64.getEncoder().encodeToString(bytes);
        // Setup a request object to use in the tests
        request = new RegisterRequest("FirstName1", "LastName1", "ASd","@aaa", "Asadsad");

        // Setup a mock FollowDAO that will return known responses

//        expectedResponse = resultUser1;
        mockUserDAODynamo = Mockito.mock(UserDAODynamo.class);
//        Mockito.when(mockUserDAODynamo.register(request.getFirstName(),request.getLastName(),request.getImage(),request.getUsername())).thenReturn(expectedResponse);
        Mockito.when(mockUserDAODynamo.uploadFile( anyString(), anyString(), Mockito.any(), anyLong(), anyString())).thenReturn("sadsadsa");
//
        userServiceSpy = Mockito.spy(UserService.class);
        Mockito.when(userServiceSpy.getUserDAO()).thenReturn(mockUserDAODynamo);
//    }
    }

    public static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;

    }


    @Test
    public void testRegister_success() {

        UserService service = new UserService();
        service.register(request);
        RegisterResponse response = userServiceSpy.register(request);


    }

    @Test
    public void testLogin_success() {

        UserService service = new UserService();
        service.register(request);
        RegisterResponse response = userServiceSpy.register(request);


        LoginRequest loginRequest = new LoginRequest(request.getUsername(),request.getPassword());
        LoginResponse loginResponse = service.login(loginRequest);

        Assertions.assertTrue(loginResponse.isSuccess());

    }

    @Test
    public void testFollow_success() {

        UserService service = new UserService();
        service.register(request);
        RegisterResponse response = userServiceSpy.register(request);

        User user = new User("followtestfollowee", "fff","@followtestfollowee","adssa","adsa");
        FollowRequest followRequest = new FollowRequest(user, response.getAuthToken());

        FollowResponse followResponse =  new FollowService().follow(followRequest);

        Assertions.assertTrue(followResponse.isSuccess());

    }
    @Test
    public void testUnFollow_success() {

//        UserService service = new UserService();
//        service.register(request);
        RegisterResponse response = userServiceSpy.register(request);

        User user = new User("followtestfollowee", "fff","@followtestfollowee","adssa","adsa");
        FollowRequest followRequest = new FollowRequest(user, response.getAuthToken());

        FollowResponse followResponse =  new FollowService().follow(followRequest);

        Assertions.assertTrue(followResponse.isSuccess());

        UnfollowRequest unfollowRequest = new UnfollowRequest(user,response.getAuthToken());
        UnfollowResponse unfollowResponse = new FollowService().unfollow(unfollowRequest);

        Assertions.assertTrue(unfollowResponse.isSuccess());

    }

    @Test
    void testIsFollower_success() {


        User follower = new User("FirstName1", "LastName1","@isfollowerFollower","ASd", "Asadsad");

        User followee = new User("followtestfollowee", "fff","@isfollowereFolloweeFail","adssa","adsa");

        RegisterRequest registerRequest = new RegisterRequest(follower.getFirstName(), follower.getLastName(),follower.getImageUrl(),follower.getUser_alias(), follower.getPassword());

        UserService service = new UserService();
        service.register(registerRequest);
        RegisterResponse response = userServiceSpy.register(registerRequest);

//        FollowRequest followRequest = new FollowRequest(followee, response.getAuthToken());

//        FollowResponse followResponse =  new FollowService().follow(followRequest);

//        Assertions.assertTrue(followResponse.isSuccess());

        IsFollowerRequest isFollowerRequest = new IsFollowerRequest(response.getAuthToken(),follower, followee);
        IsFollowerResponse isFollowerResponse = new FollowService().isFollower(isFollowerRequest);

        Assertions.assertTrue(isFollowerResponse.isGetIsFollower());

    }

    @Test
    void testGetFollowees_success () {
        User follower = new User("FirstName1", "LastName1","@abb","ASd", "Asadsad");

        User followee1 = new User("followtestfollowee", "fff","@followee1","adssa","adsa");
        User followee2 = new User("Adasdsads", "Dasdsad", "@followee2", "SAdsad", "Asdasdsa");

//        UserService service = new UserService();
//        RegisterResponse response = service.register(request);
//        RegisterResponse response = userServiceSpy.register(request);

        RegisterRequest registerFollower = new RegisterRequest(follower.getFirstName(), follower.getLastName(), follower.getImageUrl(), follower.getUser_alias(), follower.getPassword());

        RegisterRequest registerFollowee1 = new RegisterRequest(followee1.getFirstName(),followee1.getLastName(),followee1.getImageUrl(),followee1.getUser_alias(),followee1.getPassword());
        RegisterRequest registerFollowee2 = new RegisterRequest(followee2.getFirstName(),followee2.getLastName(),followee2.getImageUrl(),followee2.getUser_alias(), followee2.getPassword());

        RegisterResponse registerResponse = userServiceSpy.register(registerFollower);
        userServiceSpy.register(registerFollowee1);
        userServiceSpy.register(registerFollowee2);

        FollowRequest followRequest1 = new FollowRequest(followee1, registerResponse.getAuthToken());
        FollowRequest followRequest2 = new FollowRequest(followee2, registerResponse.getAuthToken());

        FollowResponse followResponse1 =  new FollowService().follow(followRequest1);
        FollowResponse followResponse2 =  new FollowService().follow(followRequest2);

        Assertions.assertTrue(followResponse1.isSuccess());
        FollowingRequest followingRequest = new FollowingRequest(registerResponse.getAuthToken(), follower.getUser_alias(), 3, null );

        FollowingResponse followingResponse = new FollowService().getFollowees(followingRequest);

        Assertions.assertTrue(followingResponse.isSuccess());
        Assertions.assertEquals(followingResponse.getHasMorePages(), false);

    }


    @Test
    void testGetFollowers_success () {
        User followee = new User("FirstName1", "LastName1","@abb","ASd", "Asadsad");

        User follower1 = new User("followtestfollowee", "fff","@followee1","adssa","adsa");
        User follower2 = new User("Adasdsads", "Dasdsad", "@followee2", "SAdsad", "Asdasdsa");

        RegisterRequest registerFollowee = new RegisterRequest(followee.getFirstName(), followee.getLastName(), followee.getImageUrl(), followee.getUser_alias(), followee.getPassword());

        RegisterRequest registerFollower1 = new RegisterRequest(follower1.getFirstName(),follower1.getLastName(),follower1.getImageUrl(),follower1.getUser_alias(),follower1.getPassword());
        RegisterRequest registerFollower2 = new RegisterRequest(follower2.getFirstName(),follower2.getLastName(),follower2.getImageUrl(),follower2.getUser_alias(), follower2.getPassword());

        RegisterResponse registerResponse = userServiceSpy.register(registerFollowee);
        RegisterResponse follower1Response = userServiceSpy.register(registerFollower1);
        RegisterResponse follower2Response = userServiceSpy.register(registerFollower2);

        FollowRequest followRequest1 = new FollowRequest(followee, follower1Response.getAuthToken());
        FollowRequest followRequest2 = new FollowRequest(followee, follower2Response.getAuthToken());

        FollowResponse followResponse1 =  new FollowService().follow(followRequest1);
        FollowResponse followResponse2 =  new FollowService().follow(followRequest2);

        Assertions.assertTrue(followResponse1.isSuccess());
        Assertions.assertTrue(followResponse2.isSuccess());

        FollowersRequest followersRequest = new FollowersRequest(registerResponse.getAuthToken(), followee.getUser_alias(), 4, null );
        FollowersResponse followersResponse = new FollowService().getFollowers(followersRequest);

        Assertions.assertTrue(followersResponse.isSuccess());
        Assertions.assertEquals(followersResponse.getHasMorePages(), false);

    }

    @Test
    public void testGetFollowingCount_success() {

        User follower = new User("FirstName1", "LastName1","@getfollowingcounttest","ASd", "Asadsad");

        User followee1 = new User("followtestfollowee", "fff","@followee1","adssa","adsa");
        User followee2 = new User("Adasdsads", "Dasdsad", "@followee2", "SAdsad", "Asdasdsa");
        User followee3 = new User("Adasdsads", "Dasdsad", "@followee3", "SAdsad", "Asdasdsa");

//        UserService service = new UserService();
//        RegisterResponse response = service.register(request);
//        RegisterResponse response = userServiceSpy.register(request);

        RegisterRequest registerFollower = new RegisterRequest(follower.getFirstName(), follower.getLastName(), follower.getImageUrl(), follower.getUser_alias(), follower.getPassword());

        RegisterRequest registerFollowee1 = new RegisterRequest(followee1.getFirstName(),followee1.getLastName(),followee1.getImageUrl(),followee1.getUser_alias(),followee1.getPassword());
        RegisterRequest registerFollowee2 = new RegisterRequest(followee2.getFirstName(),followee2.getLastName(),followee2.getImageUrl(),followee2.getUser_alias(), followee2.getPassword());
        RegisterRequest registerFollowee3 = new RegisterRequest(followee3.getFirstName(),followee3.getLastName(),followee3.getImageUrl(),followee3.getUser_alias(), followee3.getPassword());

        RegisterResponse registerResponse = userServiceSpy.register(registerFollower);
        userServiceSpy.register(registerFollowee1);
        userServiceSpy.register(registerFollowee2);
        userServiceSpy.register(registerFollowee3);

        FollowRequest followRequest1 = new FollowRequest(followee1, registerResponse.getAuthToken());
        FollowRequest followRequest2 = new FollowRequest(followee2, registerResponse.getAuthToken());
        FollowRequest followRequest3 = new FollowRequest(followee3, registerResponse.getAuthToken());

        FollowResponse followResponse1 =  new FollowService().follow(followRequest1);
        FollowResponse followResponse2 =  new FollowService().follow(followRequest2);
        FollowResponse followResponse3 =  new FollowService().follow(followRequest3);

        FollowingCountRequest request = new FollowingCountRequest(follower, registerResponse.getAuthToken());
        FollowingCountResponse response = new FollowService().getFollowingCount(request);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(response.getCount(), 3);


    }

    @Test
    public void testGetFollowersCount_success() {
        User followee = new User("FirstName1", "LastName1","@getfollowerscount","ASd", "Asadsad");

        User follower1 = new User("followtestfollowee", "fff","@followee1","adssa","adsa");
        User follower2 = new User("Adasdsads", "Dasdsad", "@followee2", "SAdsad", "Asdasdsa");

//        UserService service = new UserService();
//        RegisterResponse response = service.register(request);
//        RegisterResponse response = userServiceSpy.register(request);

        RegisterRequest registerFollowee = new RegisterRequest(followee.getFirstName(), followee.getLastName(), followee.getImageUrl(), followee.getUser_alias(), followee.getPassword());

        RegisterRequest registerFollower1 = new RegisterRequest(follower1.getFirstName(),follower1.getLastName(),follower1.getImageUrl(),follower1.getUser_alias(),follower1.getPassword());
        RegisterRequest registerFollower2 = new RegisterRequest(follower2.getFirstName(),follower2.getLastName(),follower2.getImageUrl(),follower2.getUser_alias(), follower2.getPassword());

        RegisterResponse registerResponse = userServiceSpy.register(registerFollowee);
        RegisterResponse follower1Response = userServiceSpy.register(registerFollower1);
        RegisterResponse follower2Response = userServiceSpy.register(registerFollower2);

        FollowRequest followRequest1 = new FollowRequest(followee, follower1Response.getAuthToken());
        FollowRequest followRequest2 = new FollowRequest(followee, follower2Response.getAuthToken());

        FollowResponse followResponse1 =  new FollowService().follow(followRequest1);
        FollowResponse followResponse2 =  new FollowService().follow(followRequest2);

        Assertions.assertTrue(followResponse1.isSuccess());
        Assertions.assertTrue(followResponse2.isSuccess());


        FollowerCountRequest request = new FollowerCountRequest(followee, registerResponse.getAuthToken());
        FollowerCountResponse response = new FollowService().getFollowerCount(request);

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(response.getCount(), 2);

    }

    @Test
    public void testPostStatus_success() {
        User followee = new User("FirstName1", "LastName1","@getfollowerscount","ASd", "Asadsad");

        User follower1 = new User("followtestfollowee", "fff","@followee1","adssa","adsa");
        User follower2 = new User("Adasdsads", "Dasdsad", "@followee2", "SAdsad", "Asdasdsa");

        RegisterRequest registerFollowee = new RegisterRequest(followee.getFirstName(), followee.getLastName(), followee.getImageUrl(), followee.getUser_alias(), followee.getPassword());
        RegisterRequest registerFollower1 = new RegisterRequest(follower1.getFirstName(),follower1.getLastName(),follower1.getImageUrl(),follower1.getUser_alias(),follower1.getPassword());
        RegisterRequest registerFollower2 = new RegisterRequest(follower2.getFirstName(),follower2.getLastName(),follower2.getImageUrl(),follower2.getUser_alias(), follower2.getPassword());

        RegisterResponse registerResponse = userServiceSpy.register(registerFollowee);
        RegisterResponse follower1Response = userServiceSpy.register(registerFollower1);
        RegisterResponse follower2Response = userServiceSpy.register(registerFollower2);

        FollowRequest followRequest1 = new FollowRequest(followee, follower1Response.getAuthToken());
        FollowRequest followRequest2 = new FollowRequest(followee, follower2Response.getAuthToken());

        FollowResponse followResponse1 =  new FollowService().follow(followRequest1);
        FollowResponse followResponse2 =  new FollowService().follow(followRequest2);

        Assertions.assertTrue(followResponse1.isSuccess());
        Assertions.assertTrue(followResponse2.isSuccess());


        StatusService statusService = new StatusService();


        Calendar calendar = Calendar.getInstance();
        long timestamp = calendar.getTimeInMillis();
        Status status = new Status(followee.getUser_alias(),"succeeded", followee, String.valueOf(timestamp) , null,null);
        PostStatusRequest statusRequest = new PostStatusRequest(status, registerResponse.getAuthToken());

        statusService.postStatus(statusRequest);

    }

}
