package uk.co.whitbread.company.infrastructure.rest.client.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "config.reports")
public class CompanyReportProperties {

  private Integer allowedRecordCount;
  private String emergencyStatusItems;
  private String managementInformationStatusItems;


}
