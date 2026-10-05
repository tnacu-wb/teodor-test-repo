package uk.co.whitbread.ohip.infrastructure.rest.client.token.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.token-service")
public class TokenServiceProperties {

  private String host;
  private String tokenEndpoint;
}

