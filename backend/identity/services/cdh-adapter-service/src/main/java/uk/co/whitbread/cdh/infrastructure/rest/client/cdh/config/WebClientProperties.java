package uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "webclient.codec")
public class WebClientProperties {

  private int size;

  /**
   * Timeout in seconds for blocking operations (e.g., blockLast() calls during pagination).
   * This is particularly important for operations that paginate through large datasets.
   * Default: 120 seconds (supports ~60 API calls at 2 seconds each).
   */
  private long blockingTimeout = 120;

}
