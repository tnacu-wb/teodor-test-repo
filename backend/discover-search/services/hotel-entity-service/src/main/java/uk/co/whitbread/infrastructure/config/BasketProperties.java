package uk.co.whitbread.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.basket-service")
public class BasketProperties {

  private String host;
  private String basketEndpoint;
}
