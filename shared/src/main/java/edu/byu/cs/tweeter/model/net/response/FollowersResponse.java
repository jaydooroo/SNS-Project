package edu.byu.cs.tweeter.model.net.response;

import java.util.List;
import java.util.Objects;

import edu.byu.cs.tweeter.model.domain.User;

public class FollowersResponse extends PagedResponse<User> {


    public FollowersResponse(boolean hasMorePages, List<User> items) {
        super(true, hasMorePages, items);
    }

    public FollowersResponse( String message) {
        super(false, message, false);
    }

    @Override
    public boolean equals(Object param) {
        if (this == param) {
            return true;
        }

        if (param == null || getClass() != param.getClass()) {
            return false;
        }

        FollowersResponse that = (FollowersResponse) param;

        return (Objects.equals(this.items, that.items) &&
                Objects.equals(this.getMessage(), that.getMessage()) &&
                this.isSuccess() == that.isSuccess());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.items);
    }

}
