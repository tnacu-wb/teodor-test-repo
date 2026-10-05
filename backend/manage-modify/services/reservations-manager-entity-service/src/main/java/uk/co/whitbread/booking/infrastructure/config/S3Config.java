package uk.co.whitbread.booking.infrastructure.config;

import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import uk.co.whitbread.booking.infrastructure.rest.client.aws.properties.S3Properties;

@Configuration
@Slf4j
@AllArgsConstructor
public class S3Config {

  @Bean
  @ConditionalOnExpression("'${irsa.enabled}'.equals('true')")
  public S3Client s3Client(S3Properties s3Properties) {
    log.info("Configuring S3 client using IRSA is true.");

    return S3Client.builder()
        .region(Region.of(s3Properties.getBucket().getRegion()))
        .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
        .build();
  }

  @Bean
  @ConditionalOnExpression("'${irsa.enabled}'.equals('true')")
  public S3Presigner buildS3Presigner(S3Properties s3Properties) {
    return S3Presigner.builder()
        .region(Region.of(s3Properties.getBucket().getRegion()))
        .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
        .build();
  }

  @Bean
  @ConditionalOnExpression("'${irsa.enabled}'.equals('false')")
  public S3Client s3ClientLocal(S3Properties s3Properties) {
    log.info("Configuring S3 client using IRSA false.");

    return S3Client.builder()
        .region(Region.of(s3Properties.getBucket().getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(s3Properties.getBucket().getAccessKey(),
                s3Properties.getBucket().getSecretAccessKey())))
        .endpointOverride(URI.create(s3Properties.getEndpoint()))
        .build();
  }

  @Bean
  @ConditionalOnExpression("'${irsa.enabled}'.equals('false')")
  public S3Presigner buildS3PresignerLocal(S3Properties s3Properties) {
    return S3Presigner.builder()
        .region(Region.of(s3Properties.getBucket().getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(s3Properties.getBucket().getAccessKey(),
                s3Properties.getBucket().getSecretAccessKey())))
        .endpointOverride(URI.create(s3Properties.getEndpoint()))
        .build();
  }

}

