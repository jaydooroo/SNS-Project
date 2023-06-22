package edu.byu.cs.tweeter.model.net.response;

import java.util.Objects;

public class FollowerCountResponse extends Response {

    protected int count;

    public FollowerCountResponse(String message) {
        super(false, message);
    }

    public FollowerCountResponse( int count) {
        super(true);
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowerCountResponse that = (FollowerCountResponse) o;
        return count == that.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
