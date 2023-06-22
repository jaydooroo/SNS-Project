package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * Represents a user in the system.
 */

@DynamoDbBean
public class User implements Comparable<User>, Serializable {

    private String firstName;
    private String lastName;
    private String user_alias;
    private String imageUrl;
    private String password;

    /**
     * Allows construction of the object from Json. Private so it won't be called by other code.
     */
    public User() {}

    public User(String firstName, String lastName, String imageURL) {
        this(firstName, lastName, String.format("@%s%s", firstName, lastName), imageURL);
    }

    public User(String firstName, String lastName, String alias, String imageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.user_alias = alias;
        this.imageUrl = imageURL;
    }

    public User(String firstName, String lastName, String alias, String imageURL, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.user_alias = alias;
        this.imageUrl = imageURL;
        this.password = hashPassword(password);
    }

    public boolean comparePassword(String password) {
        if(this.password.equals(hashPassword(password))) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return String.format("%s %s", firstName, lastName);
    }

    @DynamoDbPartitionKey
    public String getUser_alias() {
        return user_alias;
    }

    public void setUser_alias(String alias) {
        this.user_alias = alias;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return user_alias.equals(user.user_alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_alias);
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", alias='" + user_alias + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public int compareTo(User user) {
        return this.getUser_alias().compareTo(user.getUser_alias());
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    private static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }


}
