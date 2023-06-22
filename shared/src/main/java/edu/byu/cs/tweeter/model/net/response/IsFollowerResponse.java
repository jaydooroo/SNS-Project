package edu.byu.cs.tweeter.model.net.response;

import java.util.Objects;

public class IsFollowerResponse extends Response{

    public boolean isGetIsFollower() {
        return getIsFollower;
    }

    private boolean getIsFollower;


    public IsFollowerResponse(boolean isFollower) {
        super(true);
        this.getIsFollower = isFollower;
    }

    public IsFollowerResponse(String message) {
        super(false, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IsFollowerResponse that = (IsFollowerResponse) o;
        return getIsFollower == that.getIsFollower;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIsFollower);
    }


    public void setGetIsFollower(boolean getIsFollower) {
        this.getIsFollower = getIsFollower;
    }
}
