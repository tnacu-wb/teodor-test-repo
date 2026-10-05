package uk.co.whitbread.company.infrastructure.rest.client.aws.properties;

import java.util.List;
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

    private String miReportName;
    private String emergencyReportName;
    private String region;
    private String accessKey;
    private String secretAccessKey;
  }
}

