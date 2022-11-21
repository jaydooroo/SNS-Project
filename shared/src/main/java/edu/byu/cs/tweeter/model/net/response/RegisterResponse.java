package edu.byu.cs.tweeter.model.net.response;

import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterResponse extends AuthenticateResponse{
    public RegisterResponse(String message) {
        super(false, message);
    }

    public RegisterResponse(User user, AuthToken authToken) {

        super(true, null, user, authToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticateResponse that = (AuthenticateResponse) o;
        return Objects.equals(user, that.user) && Objects.equals(authToken, that.authToken);
    }
}
