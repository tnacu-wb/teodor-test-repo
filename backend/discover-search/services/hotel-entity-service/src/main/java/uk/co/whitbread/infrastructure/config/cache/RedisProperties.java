package uk.co.whitbread.infrastructure.config.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.redis")
public class RedisProperties {

  private Integer availabilityCacheTTLms;
  private Integer onSaleFlagCacheTTLHours;

}
