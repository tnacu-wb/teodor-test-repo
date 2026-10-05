package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "db")
@Data
public class JDBCBatchProperties {
  private int batchSize = 10;
  private int  maxRetryLimit = 2;
}
