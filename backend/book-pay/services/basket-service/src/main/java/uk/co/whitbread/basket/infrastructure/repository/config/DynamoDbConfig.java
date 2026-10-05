package uk.co.whitbread.basket.infrastructure.repository.config;


import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.PrepaidDepositEntity;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DynamoDbConfig {

  private final DynamoDbProperties dynamoDbProperties;

  @Bean
  @ConditionalOnExpression("${amazon.dynamodb.irsa}")
  public DynamoDbAsyncClient dynamoDbAsyncClient() {
    log.info("Configuring DynamoDB async client using irsa.");
    final AwsCredentialsProvider webIdentityTokenCredentialsProvider =
        WebIdentityTokenFileCredentialsProvider.create();
    return DynamoDbAsyncClient.builder()
        .region(Region.of(dynamoDbProperties.getRegion()))
        .credentialsProvider(webIdentityTokenCredentialsProvider)
        .build();
  }

  @Bean
  @ConditionalOnExpression("!${amazon.dynamodb.irsa}")
  public DynamoDbAsyncClient dynamoDbAsyncLocalClient() throws URISyntaxException {
    log.info("Configuring DynamoDB async client using credentials.");
    final AwsCredentials awsCredentials = AwsBasicCredentials
        .create(dynamoDbProperties.getAccessKey(), dynamoDbProperties.getSecretKey());
    final AwsCredentialsProvider staticCredentialsProvider =
        StaticCredentialsProvider.create(awsCredentials);
    return DynamoDbAsyncClient.builder()
        .region(Region.EU_WEST_1)
        .credentialsProvider(staticCredentialsProvider)
        .endpointOverride(new URI(dynamoDbProperties.getEndpoint()))
        .build();
  }

  @Bean
  public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient(
      DynamoDbAsyncClient dynamoDbAsyncClient) {
    log.info("Configuring enhanced DynamoDB async client.");
    return DynamoDbEnhancedAsyncClient.builder()
        .dynamoDbClient(dynamoDbAsyncClient)
        .build();
  }

  @Bean
  public TableSchema<PrepaidDepositEntity> prepaidDepositTableSchema() {
    return TableSchema.fromBean(PrepaidDepositEntity.class);
  }

  @Bean
  public TableSchema<BasketEntity> basketEntityTableSchema() {
    return TableSchema.fromBean(BasketEntity.class);
  }
}
