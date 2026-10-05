package uk.co.whitbread.basket.infrastructure.repository.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "amazon.dynamodb")
@Configuration
@Data
public class DynamoDbProperties {
  private String endpoint;
  private String tableName;
  private String tableNamePrepaidDeposit;
  private String accessKey;
  private String secretKey;
  private String region;
}
