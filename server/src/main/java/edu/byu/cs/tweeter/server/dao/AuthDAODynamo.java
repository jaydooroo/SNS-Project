package edu.byu.cs.tweeter.server.dao;

import java.security.Timestamp;
import java.util.Calendar;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class AuthDAODynamo extends DynamoDAO implements AuthDAO{
    long timeoutPeriod = 1000 * 60 * 60 * 2;

  //Todo: just wrote this for testing. need to be reomved once the test is done.
//    long timeoutPeriod = 1000 * 30;
    DynamoDbTable<AuthToken> authTable;

    public boolean authenticate(AuthToken token) {

        connectDB();

        Key key = Key.builder()
                .partitionValue(token.getToken())
                .build();
        AuthToken result = authTable.getItem(key);

        if(result == null || result.token == null) {

            return false;

        }

        Calendar calendar = Calendar.getInstance();
        long expiredTime = Long.parseLong(result.getDatetime()) + timeoutPeriod;
        long timestamp = calendar.getTimeInMillis();



        if( expiredTime > timestamp) {

            AuthToken refreshedToken = new AuthToken(token.getToken(), String.valueOf(timestamp), token.getUser_alias());

            authTable.putItem(refreshedToken);

            disconnectDB();

            return true;

        }
        else {

            disconnectDB();
            return false;

        }
    }

    public void registerAuthToken(AuthToken token) {

        connectDB();

        authTable.putItem(token);

        disconnectDB();


    }

    public void deleteAuthToken(AuthToken token) {

        connectDB();


        Key key = Key.builder()
                .partitionValue(token.getToken())
                .build();
        authTable.deleteItem(key);

        disconnectDB();

    }


    @Override
    protected void connectTable() {
        if(this.authTable == null) {
            this.authTable = enhancedClient.table("auth", TableSchema.fromBean(AuthToken.class));
        }
    }

    @Override
    protected void disconnectTable() {
        this.authTable = null;
    }
}
