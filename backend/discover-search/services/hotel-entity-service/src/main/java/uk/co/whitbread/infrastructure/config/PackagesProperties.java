package uk.co.whitbread.infrastructure.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "packages")
public class PackagesProperties {

  private Integer cutOffTime;
  private List<String> extrasPackageCodes;
}
