package edu.byu.cs.tweeter.model.net.response;

import java.util.Objects;

public class IsFollowerResponse extends Response{

    private boolean isFollower;


    public IsFollowerResponse(boolean isFollower) {
        super(true);
        this.isFollower = isFollower;
    }

    IsFollowerResponse(String message) {
        super(false, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IsFollowerResponse that = (IsFollowerResponse) o;
        return isFollower == that.isFollower;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isFollower);
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }
}
