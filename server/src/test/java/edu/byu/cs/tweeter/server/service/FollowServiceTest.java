package edu.byu.cs.tweeter.server.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import static edu.byu.cs.tweeter.server.service.UserServiceTest.toByteArray;

import org.checkerframework.dataflow.qual.TerminatesExecution;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;

import javax.imageio.ImageIO;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAODynamo;
import edu.byu.cs.tweeter.server.dao.UserDAODynamo;

public class FollowServiceTest {

    private FollowingRequest request;
    private FollowingResponse expectedResponse;
    private FollowsDAODynamo mockFollowingDAODynamo;
    private FollowService followServiceSpy;


    private RegisterRequest registerRequest;
    private UserDAODynamo mockUserDAODynamo;
    private UserService userServiceSpy;


    @BeforeEach
    public void setup() throws IOException {
        AuthToken authToken = new AuthToken();

        User currentUser = new User("FirstName", "LastName", null);

        User resultUser1 = new User("FirstName1", "LastName1",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        User resultUser2 = new User("FirstName2", "LastName2",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
        User resultUser3 = new User("FirstName3", "LastName3",
                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");

        // Setup a request object to use in the tests
        request = new FollowingRequest(authToken, currentUser.getUser_alias(), 3, null);

        // Setup a mock FollowDAO that will return known responses
        expectedResponse = new FollowingResponse(Arrays.asList(resultUser1, resultUser2, resultUser3), false);
        mockFollowingDAODynamo = Mockito.mock(FollowsDAODynamo.class);
        Mockito.when(mockFollowingDAODynamo.getFollowees(request.getFollowerAlias(), request.getLimit(), request.getLastFolloweeAlias())).thenReturn(expectedResponse);

        followServiceSpy = Mockito.spy(FollowService.class);
        Mockito.when(followServiceSpy.getFollowsDAO()).thenReturn(mockFollowingDAODynamo);




        BufferedImage img =  ImageIO.read(new URL("https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png"));

        byte[] bytes = toByteArray(img, "jpg");

        String imageBytesBase64 = Base64.getEncoder().encodeToString(bytes);
        // Setup a request object to use in the tests
        registerRequest = new RegisterRequest("FirstName", "LastName1", "ASd","@aaa", "Asadsad");

        // Setup a mock FollowDAO that will return known responses

//        expectedResponse = resultUser1;
        mockUserDAODynamo = Mockito.mock(UserDAODynamo.class);
//        Mockito.when(mockUserDAODynamo.register(request.getFirstName(),request.getLastName(),request.getImage(),request.getUsername())).thenReturn(expectedResponse);
        Mockito.when(mockUserDAODynamo.uploadFile( anyString(), anyString(), Mockito.any(), anyLong(), anyString())).thenReturn("sadsadsa");
//
        userServiceSpy = Mockito.spy(UserService.class);
        Mockito.when(userServiceSpy.getUserDAO()).thenReturn(mockUserDAODynamo);
    }

    public static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;

    }

    /**
     * Verify that the {@link FollowService#getFollowees(FollowingRequest)}
     * method returns the same result as the {@link FollowsDAODynamo} class.
     */
    @Test
    public void testGetFollowees_validRequest_correctResponse() {
        FollowingResponse response = followServiceSpy.getFollowees(request);
        Assertions.assertEquals(expectedResponse, response);
    }

    @Test
    public void testFollow_correctResponse() {

        UserService service = new UserService();
        service.register(registerRequest);
        RegisterResponse response = service.register(registerRequest);

        User user = new User("followtestfollowee", "fff","@followtestfollowee","adssa","adsa");
        FollowRequest followRequest = new FollowRequest(user, response.getAuthToken());

        FollowResponse followResponse =  followServiceSpy.follow(followRequest);

        Assertions.assertTrue(followResponse.isSuccess());
    }

}
