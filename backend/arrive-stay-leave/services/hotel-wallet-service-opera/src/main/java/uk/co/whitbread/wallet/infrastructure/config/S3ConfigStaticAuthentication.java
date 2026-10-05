package uk.co.whitbread.wallet.infrastructure.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.wallet.infrastructure.certs.properties.S3Properties;


@Configuration
@Slf4j
@ConditionalOnExpression("'${irsa.enabled}'.equals('false')")
@RequiredArgsConstructor
public class S3ConfigStaticAuthentication {

  @Bean
  public ClientConfiguration getClientConfiguration(S3Properties s3Properties) {

    ClientConfiguration clientConfiguration = new ClientConfiguration();
    clientConfiguration.withMaxErrorRetry(s3Properties.getMaxRetry());

    return clientConfiguration;
  }

  @Bean
  public AmazonS3 getAmazonS3(ClientConfiguration clientConfiguration,
      @Value("${aws.endpoint:}") String endpoint) {

    AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
        .withClientConfiguration(clientConfiguration);
    if (!Strings.isNullOrEmpty(endpoint)) {
      builder.withPathStyleAccessEnabled(true)
          .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, null));
    }
    return builder.build();

  }
}