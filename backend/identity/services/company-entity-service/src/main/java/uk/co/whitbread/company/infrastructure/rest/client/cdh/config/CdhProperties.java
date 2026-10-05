package uk.co.whitbread.company.infrastructure.rest.client.cdh.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.cdh")
public class CdhProperties {
  private String host;
  private String companiesEndpoint;
}
