package uk.co.whitbread.payments.infrastructure.rest.client.token.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.token")
public class TokenClientProperties {

  private String host;
  private String paypalTokenEndPoint;

}
