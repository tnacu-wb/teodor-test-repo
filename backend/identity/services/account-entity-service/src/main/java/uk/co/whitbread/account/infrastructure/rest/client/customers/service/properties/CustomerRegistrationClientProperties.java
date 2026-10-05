package uk.co.whitbread.account.infrastructure.rest.client.customers.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.customers")
public class CustomerRegistrationClientProperties {
  private String host;
  private String registrationEndpoint;
}
