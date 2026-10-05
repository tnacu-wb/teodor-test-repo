package uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.basket.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.basket")
public class BasketProperties {

  private String host;
  private String confirmItemProcessingEndpoint;

}
