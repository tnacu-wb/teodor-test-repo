package uk.co.whitbread.company.infrastructure.rest.client.cdh.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.cdh")
public class CdhAdapterProperties {

  private String host;

  private String companyMiReportEndpoint;

  private String emergencyReportEndpoint;

  private String accessContext;

  private String accessedBy;

}
