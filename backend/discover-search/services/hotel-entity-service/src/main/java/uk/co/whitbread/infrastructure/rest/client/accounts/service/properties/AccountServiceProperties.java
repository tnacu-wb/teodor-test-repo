package uk.co.whitbread.infrastructure.rest.client.accounts.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.accounts")
public class AccountServiceProperties {

  private String host;
  private String companyEndpoint;
}
