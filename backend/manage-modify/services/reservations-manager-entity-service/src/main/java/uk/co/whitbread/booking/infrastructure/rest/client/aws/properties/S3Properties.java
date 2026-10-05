package uk.co.whitbread.booking.infrastructure.rest.client.aws.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aws.s3")
public class S3Properties {

  private int maxRetry;
  private Bucket bucket;
  private String endpoint;
  private int validForMinutes;

  @Data
  public static class Bucket {

    private String invoiceReportName;
    private String region;
    private String accessKey;
    private String secretAccessKey;
  }
}
