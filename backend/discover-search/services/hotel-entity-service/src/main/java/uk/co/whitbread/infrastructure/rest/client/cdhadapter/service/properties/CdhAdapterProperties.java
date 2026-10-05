package uk.co.whitbread.infrastructure.rest.client.cdhadapter.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.cdh")
public class CdhAdapterProperties {

  private String host;
  private String getCompanySuppressRatesEndpoint;
  private String companiesSearchEndpoint;
}