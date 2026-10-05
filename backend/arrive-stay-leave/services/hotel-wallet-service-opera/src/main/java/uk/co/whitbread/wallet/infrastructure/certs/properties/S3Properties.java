package uk.co.whitbread.wallet.infrastructure.certs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {

  private int maxRetry;
  private Bucket bucket;

  @Data
  public static class Bucket {

    private String name;
  }
}
