package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

/**
 * Represents an auth token in the system.
 */
@DynamoDbBean
public class AuthToken implements Serializable {
    /**
     * Value of the auth token.
     */
    public String token;
    /**
     * String representation of date/time at which the auth token was created.
     */
    public String datetime;

    public String user_alias;

    public AuthToken(String user_alias) {
        this.user_alias = user_alias;
        UUID uuid = UUID.randomUUID();
        this.token = uuid.toString();

        Calendar calendar = Calendar.getInstance();
        long timestamp = calendar.getTimeInMillis();

        this.datetime = String.valueOf(timestamp);

    }


    public AuthToken(String token, String datetime, String user_alias) {
        this.token = token;
        this.datetime = datetime;
        this.user_alias = user_alias;
    }

    public AuthToken() {
        UUID uuid = UUID.randomUUID();
        this.token = uuid.toString();

        Calendar calendar = Calendar.getInstance();
        long timestamp = calendar.getTimeInMillis();

        this.datetime = String.valueOf(timestamp);

    }

    @DynamoDbPartitionKey
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDatetime() {
        return datetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken = (AuthToken) o;
        return Objects.equals(token, authToken.token) ;
//                Objects.equals(datetime, authToken.datetime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, datetime);
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getUser_alias() {
        return user_alias;
    }

    public void setUser_alias(String user_alias) {
        this.user_alias = user_alias;
    }
}