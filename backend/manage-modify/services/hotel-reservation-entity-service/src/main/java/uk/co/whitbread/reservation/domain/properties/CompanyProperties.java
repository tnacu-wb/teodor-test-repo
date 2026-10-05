package uk.co.whitbread.reservation.domain.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.whitbread-privilege")
public class CompanyProperties {
  private String companyId;
}