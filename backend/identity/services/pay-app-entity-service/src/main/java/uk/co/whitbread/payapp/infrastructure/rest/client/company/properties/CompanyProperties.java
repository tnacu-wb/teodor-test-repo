package uk.co.whitbread.payapp.infrastructure.rest.client.company.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.company")
public class CompanyProperties {

  private String host;
  private String getCompanyDetailsEndpoint;
}
