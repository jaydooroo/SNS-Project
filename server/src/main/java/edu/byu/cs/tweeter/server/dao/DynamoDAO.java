package edu.byu.cs.tweeter.server.dao;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public abstract class   DynamoDAO {

    Region region = Region.US_WEST_2;
    DynamoDbClient ddb;
    DynamoDbEnhancedClient enhancedClient;

//    public DynamoDAO(ProfileCredentialsProvider credentialsProvider, Region region) {
//        this.credentialsProvider = credentialsProvider;
//        this.region = region;
//    }

    protected void connectDB () {

        if(this.ddb == null) {
            this.ddb = DynamoDbClient.builder().region(region).build();
        }
        if(this.enhancedClient == null) {
            this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();
        }
       connectTable();
    }

    // need to ask if I have to disconnect
    protected void disconnectDB() {
        ddb.close();
        this.ddb = null;
        this.enhancedClient = null;
        disconnectTable();
    }

    protected abstract void connectTable();

    protected abstract void disconnectTable();
}
