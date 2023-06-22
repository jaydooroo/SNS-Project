package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Represents a status (or tweet) posted by a user.
 */

@DynamoDbBean
public class Status implements Serializable {

    public String user_alias;
    /**
     * Text for the status.
     */
    public String post;
    /**
     * User who sent the status.
     */
    public User user;
    /**
     * String representation of the date/time at which the status was sent.
     */
    public String timestamp;
    /**
     * URLs contained in the post text.
     */
    public List<String> urls;
    /**
     * User mentions contained in the post text.
     */
    public List<String> mentions;

    public Status() {
    }

    public Status(String post, User user, String timestamp, List<String> urls, List<String> mentions) {
        this.user_alias = "No Alias";
        this.post = post;
        this.user = user;
        this.timestamp = timestamp;
        this.urls = urls;
        this.mentions = mentions;
    }

    public Status(String user_alias, String post, User user, String timestamp, List<String> urls, List<String> mentions) {
        this.user_alias = user_alias;
        this.post = post;
        this.user = user;
        this.timestamp = timestamp;
        this.urls = urls;
        this.mentions = mentions;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getDate() {
        return timestamp;
    }

    public String getPost() {
        return post;
    }

    public List<String> getUrls() {
        return urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return Objects.equals(post, status.post) &&
                Objects.equals(user, status.user) &&
//                Objects.equals(datetime, status.datetime) &&
                Objects.equals(mentions, status.mentions) &&
                Objects.equals(urls, status.urls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, user, timestamp, mentions, urls);
    }



    public void setPost(String post) {
        this.post = post;
    }

    @DynamoDbSortKey
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    @DynamoDbPartitionKey
    public String getUser_alias() {
        return user_alias;
    }

    public void setUser_alias(String user_alias) {
        this.user_alias = user_alias;
    }

    @Override
    public String toString() {
        return "Status{" +
                "user_alias='" + user_alias + '\'' +
                ", post='" + post + '\'' +
                ", user=" + user +
                ", timestamp='" + timestamp + '\'' +
                ", urls=" + urls +
                ", mentions=" + mentions +
                '}';
    }
}
