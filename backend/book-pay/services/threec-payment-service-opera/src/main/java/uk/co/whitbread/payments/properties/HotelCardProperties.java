package uk.co.whitbread.payments.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "hotel-card")
@Configuration
@Data
public class HotelCardProperties {
  private boolean keepAlive;
  private int maxConnections;
  private int maxIdleTime;
  private int maxLifetime;
  private int acquiredTimeout;
  private int evictTimeout;
  private long response;
  private long connection;
  private String saveCardEndpoint;
}