package edu.byu.cs.tweeter.server.Module;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import edu.byu.cs.tweeter.server.dao.AuthDAO;
import edu.byu.cs.tweeter.server.dao.AuthDAODynamo;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAODynamo;
//import edu.byu.cs.tweeter.server.dao.FollowingDAO;
//import edu.byu.cs.tweeter.server.dao.FollowingDAODynamo;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAODynamo;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAODynamo;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.UserDAODynamo;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;


public class DynamoModule extends AbstractModule {
    ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
    Region region = Region.US_WEST_2;


    @Override
    protected void configure() {
        bind(FeedDAO.class).to(FeedDAODynamo.class);
//        bind(FollowingDAO.class).to(FollowingDAODynamo.class);
        bind(FollowsDAO.class).to(FollowsDAODynamo.class);
        bind(StoryDAO.class).to(StoryDAODynamo.class);
        bind(UserDAO.class).to(UserDAODynamo.class);
        bind(AuthDAO.class).to(AuthDAODynamo.class);
    }


//    @Provides
//    @Named("DynamoDBRegion")
//    public Region dynamodbRegion() {
//        return this.region;
//    }
//
////    @Provides
////    @Named("DynamoDbCredential")
////    public ProfileCredentialsProvider dynamoCredential() {
////        return this.credentialsProvider;
////    }

}
