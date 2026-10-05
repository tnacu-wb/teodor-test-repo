package uk.co.whitbread.wallet.infrastructure.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.WebIdentityTokenCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.wallet.infrastructure.certs.properties.S3Properties;

@Configuration
@Slf4j
@ConditionalOnExpression("'${irsa.enabled}'.equals('true')")
public class S3Config {

  @Bean
  public ClientConfiguration getClientConfiguration(S3Properties s3Properties) {

    ClientConfiguration clientConfiguration = new ClientConfiguration();
    clientConfiguration.withMaxErrorRetry(s3Properties.getMaxRetry());

    return clientConfiguration;
  }

  @Bean
  public AmazonS3 getAmazonS3(ClientConfiguration clientConfiguration) {

    return AmazonS3ClientBuilder.standard().withClientConfiguration(clientConfiguration)
        .withPathStyleAccessEnabled(true)
        .withCredentials(WebIdentityTokenCredentialsProvider.create()).build();

  }
}