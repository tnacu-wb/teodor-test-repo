package uk.co.whitbread.infrastructure.rest.client.companyentity.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.company-service")
public class CompanyEntityServiceProperties {

  private String host;
  private String companyEndpoint;
}

