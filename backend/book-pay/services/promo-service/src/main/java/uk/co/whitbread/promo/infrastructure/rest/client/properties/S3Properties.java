package uk.co.whitbread.promo.infrastructure.rest.client.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {

  private int maxRetry;
  private int validForMinutes;
  private String region;
  private String promoBucketName;

}