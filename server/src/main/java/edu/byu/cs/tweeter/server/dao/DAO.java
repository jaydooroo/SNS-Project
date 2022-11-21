package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.Follow;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public abstract class DAO{


    protected ProfileCredentialsProvider credentialsProvider;
    protected Region region;
    protected DynamoDbClient ddb;
    protected DynamoDbEnhancedClient enhancedClient;


    public DAO(ProfileCredentialsProvider credentialsProvider, Region region) {
        this.credentialsProvider = credentialsProvider;
        this.region = region;
    }

    protected void connectDB () {

        if(this.ddb == null) {
            this.ddb = DynamoDbClient.builder().credentialsProvider(credentialsProvider).region(region).build();
        }
        if(this.enhancedClient == null) {
            this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();
        }
       connectTable();
    }
    protected abstract void connectTable();


    // need to ask if I have to disconnect
    protected void disconnectDB() {
        ddb.close();

        this.ddb = null;
        this.enhancedClient = null;
        disconnectTable();
    }

    protected abstract void disconnectTable();
}
