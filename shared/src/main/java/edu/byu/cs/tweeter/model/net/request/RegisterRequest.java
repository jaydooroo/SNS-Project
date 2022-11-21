package edu.byu.cs.tweeter.model.net.request;

public class RegisterRequest {

    /**
     * The user's first name.
     */
    private String firstName;
    /**
     * The user's last name.
     */
    private String lastName;
    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private String image;

    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    private String username;

    /**
     * The user's password.
     */
    private String password;


    public RegisterRequest(String firstName, String lastName, String image, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.username = username;
        this.password = password;
    }

    public RegisterRequest() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
