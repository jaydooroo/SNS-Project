package edu.byu.cs.tweeter.server.service;

import com.google.inject.Guice;
import com.google.inject.Injector;

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
import edu.byu.cs.tweeter.server.Module.DynamoModule;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {




    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
        return  getUserDAO().login(request);
    }

    public RegisterResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        } else if(request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        } else if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        } else if(request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing a Image");
        }

        //1. check if the database has the user
        //2. need to upload to s3 bucket
        //3. it will return url and svae it
        // TODO: Generates dummy data. Replace with a real implementation.
        return getUserDAO().register(request);
    }

    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Missing a authToken");
        }

        return getUserDAO().logout(request);
    }

    public GetUserResponse getUser (GetUserRequest request) {
        if(request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing a alias");
        }
        else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Missing a authToken");
        }

        return getUserDAO().getUser(request);
    }


    private UserDAO getUserDAO() {
        Injector injector = Guice.createInjector(new DynamoModule());
        return injector.getInstance(UserDAO.class);
        }
    }

//    /**
//     * Returns the dummy user to be returned by the login operation.
//     * This is written as a separate method to allow mocking of the dummy user.
//     *
//     * @return a dummy user.
//     */
//    User getDummyUser() {
//        return getFakeData().getFirstUser();
//    }

//    /**
//     * Returns the dummy auth token to be returned by the login operation.
//     * This is written as a separate method to allow mocking of the dummy auth token.
//     *
//     * @return a dummy auth token.
//     */
//    AuthToken getDummyAuthToken() {
//        return getFakeData().getAuthToken();
//    }

//    /**
//     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
//     * This is written as a separate method to allow mocking of the {@link FakeData}.
//     *
//     * @return a {@link FakeData} instance.
//     */
//    FakeData getFakeData() {
//        return FakeData.getInstance();
//    }


