package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.InputStream;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class UserDAODynamo implements UserDAO {

    public LoginResponse login(LoginRequest request) {

        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();

        return new LoginResponse(user, authToken);
    }

    public RegisterResponse register(RegisterRequest request) {
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();

        return new RegisterResponse(user, authToken);
    }

    public LogoutResponse logout(LogoutRequest request) {

        return new LogoutResponse(true);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        User user = getFakeData().findUserByAlias(request.getAlias());
        return new GetUserResponse(user);
    }


    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {

        return getFakeData().getFirstUser();

    }

    public boolean uploadFile(String bucketName,
                              String keyName,//filename,
                              InputStream content, // image decode into byte array convert it into input stream.
                              long contentLength, //size
                              String mimeType  //"image/jpg"
    ) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            metadata.setContentType(mimeType);

            s3.putObject(new PutObjectRequest(bucketName, keyName, content, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));


            return true;
            //rather return string
        } catch (AmazonServiceException e) {
            System.out.println(e.getErrorMessage());
            e.printStackTrace();
            return false;
            //rather return null        }
        }
    }
}