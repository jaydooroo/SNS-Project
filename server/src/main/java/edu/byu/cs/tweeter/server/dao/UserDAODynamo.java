package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.FakeData;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;

public class UserDAODynamo extends DynamoDAO implements UserDAO{
    public static final String BUCKET_NAME = "tweeterimages340";
    public static final String MIME_TYPE = "image/jpg";

    DynamoDbTable<User> userTable;

//    @Inject
//    public UserDAODynamo( ProfileCredentialsProvider credentialsProvider,@Named("DynamoDBRegion") Region region) {
//        super(credentialsProvider, region);
//    }


    @Override
    public LoginResponse login(String username, String password) {

        connectDB();


        Key key = Key.builder().partitionValue(username).build();

        User result = userTable.getItem((GetItemEnhancedRequest.Builder requestBuilder) -> requestBuilder.key(key));

        if(!result.comparePassword(password)) {
            disconnectDB();
            return new LoginResponse("Wrong Password");
        }

        AuthToken authToken = new AuthToken(username);

        disconnectDB();

        return new LoginResponse(result, authToken);
    }

    @Override
    public User register(String firstName, String lastName, String image, String username, String password) {

        connectDB();

        byte[] inputByte = Base64.getDecoder().decode(image.getBytes());
        InputStream inputImage = new ByteArrayInputStream(inputByte);

        String url =  uploadFile(BUCKET_NAME, username,inputImage,inputByte.length, MIME_TYPE);

        if(url == null) {
            disconnectDB();
            throw new RuntimeException("[Server Error] Failed to upload Image");

        }

        User user = new User(firstName,lastName,username,url,password);

//        user.setUser_alias(username);
//        user.setFirstName(firstName);
//        user.setImageUrl(image);
//        user.setLastName(lastName);

        this.userTable.putItem(user);


        disconnectDB();

        return user;
    }



    public boolean logout(LogoutRequest request) {

        return true;
    }

    public GetUserResponse getUser(String alias) {

//        User user = getFakeData().findUserByAlias(alias);

        try {
            connectDB();

            Key key = Key.builder()
                    .partitionValue(alias)
                    .build();

            User user = this.userTable.getItem(key);


            if(user != null && user.getUser_alias().equals(alias)) {
                return new GetUserResponse(user);
            }

            disconnectDB();
            return new GetUserResponse("User: " + alias + " does not exist");
        } catch (Exception e) {
            e.printStackTrace();
            disconnectDB();
            return new GetUserResponse(e.getMessage());
        }
    }


    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {

        return getFakeData().getFirstUser();

    }

    public String uploadFile(String bucketName,
                              String keyName,//filename,
                              InputStream content, // image decode into byte array convert it into input stream.
                              long contentLength, //size
                              String mimeType  //"image/jpg"
    ) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            metadata.setContentType(mimeType);

            s3.putObject(new PutObjectRequest(bucketName, keyName, content, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            return s3.getUrl(bucketName,keyName).toString();

            //rather return string
        } catch (AmazonServiceException e) {
            System.out.println(e.getErrorMessage());
            e.printStackTrace();
            return null;
            //rather return null        }
        }
    }


    protected void connectTable(){
        if(this.userTable == null) {
            this.userTable = enhancedClient.table("user", TableSchema.fromBean(User.class));
        }
    }

    protected void disconnectTable(){
        this.userTable = null;
    }
}