package uk.co.whitbread.payments.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import uk.co.whitbread.payments.model.dynamo.PaymentsSchema;
import uk.co.whitbread.payments.properties.DynamoDBProperties;

import java.net.URI;
import java.time.Duration;

/**
 * Handles configuration of the DynamoDB AWS Asynchronous client for local development and for
 * DynamoDB instances in environments which are accessed using service roles (irsa).
 */
@Configuration
@RequiredArgsConstructor
@Component
@Slf4j
public class DynamoDBConfig {

  private final DynamoDBProperties dynamoDBProperties;

  @Bean
  @ConditionalOnExpression("${amazon.dynamodb.irsa}")
  public DynamoDbAsyncClient dynamoDbAsyncClient() {
    log.debug("Configuring DynamoDB async client using irsa.");
    final AwsCredentialsProvider webIdentityTokenCredentialsProvider = WebIdentityTokenFileCredentialsProvider.create();
    return DynamoDbAsyncClient.builder()
        .overrideConfiguration(
            ClientOverrideConfiguration.builder()
                .retryPolicy(RetryMode.STANDARD)
                .apiCallAttemptTimeout(
                    Duration.ofSeconds(dynamoDBProperties.getApiCallAttemptTimeout())).build()
        ).httpClientBuilder(NettyNioAsyncHttpClient.builder()
            .maxConcurrency(dynamoDBProperties.getMaxConcurrency())
            .maxPendingConnectionAcquires(dynamoDBProperties.getMaxPendingConnectionAcquires())
            .connectionAcquisitionTimeout(
                Duration.ofSeconds(dynamoDBProperties.getConnectionAcquisitionTimeout()))
            .connectionTimeout(Duration.ofSeconds(dynamoDBProperties.getConnectionTimeout()))
            .readTimeout(Duration.ofSeconds(dynamoDBProperties.getReadTimeout())))
        .credentialsProvider(webIdentityTokenCredentialsProvider)
        .build();
  }

  @Bean
  @ConditionalOnExpression("!${amazon.dynamodb.irsa}")
  public DynamoDbAsyncClient dynamoDbAsyncLocalClient() {
    log.info("Configuring DynamoDB async client using credentials.");
    final AwsCredentials awsCredentials = AwsBasicCredentials.create(
        dynamoDBProperties.getAccessKey(), dynamoDBProperties.getSecretKey());
    final AwsCredentialsProvider staticCredentialsProvider = StaticCredentialsProvider.create(
        awsCredentials);
    return DynamoDbAsyncClient.builder()
        .overrideConfiguration(
            ClientOverrideConfiguration.builder()
                .retryPolicy(RetryMode.STANDARD)
                .apiCallAttemptTimeout(
                    Duration.ofSeconds(dynamoDBProperties.getApiCallAttemptTimeout())).build()
        ).httpClientBuilder(NettyNioAsyncHttpClient.builder()
            .maxConcurrency(dynamoDBProperties.getMaxConcurrency())
            .maxPendingConnectionAcquires(dynamoDBProperties.getMaxPendingConnectionAcquires())
            .connectionAcquisitionTimeout(
                Duration.ofSeconds(dynamoDBProperties.getConnectionAcquisitionTimeout()))
            .connectionTimeout(Duration.ofSeconds(dynamoDBProperties.getConnectionTimeout()))
            .readTimeout(Duration.ofSeconds(dynamoDBProperties.getReadTimeout())))
        .endpointOverride(URI.create(dynamoDBProperties.getEndpoint()))
        .credentialsProvider(staticCredentialsProvider)
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
  public DynamoDbAsyncTable<PaymentsSchema> paymentsTable(
      DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient) {
    return dynamoDbEnhancedAsyncClient.table(dynamoDBProperties.getTableName(),
        TableSchema.fromBean(PaymentsSchema.class));
  }
}