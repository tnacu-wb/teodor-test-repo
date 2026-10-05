package uk.co.whitbread.payments.infrastructure.rest.client.basket.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.basket")
public class BasketClientProperties {

  private String host;
  private String basketEndpoint;

}
