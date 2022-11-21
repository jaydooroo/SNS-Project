package edu.byu.cs.tweeter.model.domain;


import java.io.Serializable;
import java.util.Objects;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

/**
 * Represents a follow relationship.
 */
@DynamoDbBean
public class Follow implements Serializable {
    /**
     * The user doing the following.
     */
    public String follower_handle;
    /**
     * The user being followed.
     */
    public String followee_handle;

    public Follow(String follower_handle, String followee_handle) {
        this.follower_handle = follower_handle;
        this.followee_handle = followee_handle;
    }

    public Follow() {
    }

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = "follows_index")
    public String getFollower_handle() {
        return follower_handle;
    }

    public void setFollower_handle(String follower_handle) {
        this.follower_handle = follower_handle;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = "follows_index")
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String followee_handle) {
        this.followee_handle = followee_handle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Follow follow = (Follow) o;
        return Objects.equals(follower_handle, follow.follower_handle) && Objects.equals(followee_handle, follow.followee_handle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower_handle, followee_handle);
    }

    @Override
    public String toString() {
        return "Follow{" +
                "follower_handle='" + follower_handle + '\'' +
                ", followee_handle='" + followee_handle + '\'' +
                '}';
    }


//
//    public Follow() {
//    }
//
//    public Follow(User follower, User followee) {
//        this.follower = follower;
//        this.followee = followee;
//    }
//
//    public User getFollower() {
//        return follower;
//    }
//
//    public User getFollowee() {
//        return followee;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Follow that = (Follow) o;
//        return follower.equals(that.follower) &&
//                followee.equals(that.followee);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(follower, followee);
//    }
//
//    @Override
//    public String toString() {
//        return "Follow{" +
//                "follower=" + follower.getAlias() +
//                ", followee=" + followee.getAlias() +
//                '}';
//    }
}
