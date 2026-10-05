package uk.co.whitbread.company.infrastructure.rest.client.ohip.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.ohip")
public class OhipProperties {
  private String host;
  private String companiesProfile;
  private String companyProfile;
  private String companyProfileByCompanyId;
  private String negotiatedRatesByCompanyProfileId;
}
