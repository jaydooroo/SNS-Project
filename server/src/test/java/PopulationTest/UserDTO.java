//package PopulationTest;
//
//import edu.byu.cs.tweeter.model.domain.User;
//import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
//import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
//
//@DynamoDbBean
//public class UserDTO {
//
//    private String firstName;
//    private String lastName;
//    private String user_alias;
//    private String imageUrl;
//    private String password;
//
//    public UserDTO() {
//    }
//
//    public UserDTO(User u) {
//        this.setFirstName(u.getFirstName());
//        this.setLastName(u.getLastName());
//        this.setUser_alias(u.getUser_alias());
//        this.setImageUrl(u.getImageUrl());
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    @DynamoDbPartitionKey
//    public String getUser_alias() {
//        return user_alias;
//    }
//
//    public void setUser_alias(String user_alias) {
//        this.user_alias = user_alias;
//    }
//
//    public String getImageUrl() {
//        return imageUrl;
//    }
//
//    public void setImageUrl(String imageUrl) {
//        this.imageUrl = imageUrl;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//}
