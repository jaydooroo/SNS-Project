package PopulationTest;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Follow;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class FollowDAO {
    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .credentialsProvider(ProfileCredentialsProvider.create())
            .region(Region.US_WEST_2)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private static final String TableName = "follows";


    public void addFollowersBatch(List<Follow> followers , String follow_target) {

        List<Follow> batchToWrite = new ArrayList<>();

        for (Follow f: followers) {
            batchToWrite.add(f);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfFollows(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfFollows(batchToWrite);
        }
    }


    private void writeChunkOfFollows(List<Follow> Followers) {

        if(Followers.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<Follow> table = enhancedClient.table(TableName, TableSchema.fromBean(Follow.class));
        WriteBatch.Builder<Follow> writeBuilder = WriteBatch.builder(Follow.class).mappedTableResource(table);
        for (Follow item : Followers) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfFollows(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {

            System.err.println(e.getMessage());
            System.exit(1);

        }
    }
}
