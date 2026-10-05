package uk.co.whitbread.promo.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import uk.co.whitbread.promo.infrastructure.rest.client.properties.S3Properties;

@Configuration
@Slf4j
public class S3Config {

  private final S3Properties s3Properties;

  public S3Config(S3Properties s3Properties) {
    this.s3Properties = s3Properties;
  }

  @Bean
  @ConditionalOnExpression("'${irsa.enabled}'.equals('true')")
  public S3Presigner s3Presigner() {
    log.info("Configuring S3 presigner using IRSA is true.");
    return S3Presigner.builder()
        .region(Region.of(s3Properties.getRegion()))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }

  @Bean
  @ConditionalOnExpression("'${irsa.enabled}'.equals('true')")
  public S3Client s3Client() {
    log.info("Configuring S3 client using IRSA is true.");
    return S3Client.builder()
        .region(Region.of(s3Properties.getRegion()))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }
}