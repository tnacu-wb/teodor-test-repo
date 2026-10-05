package uk.co.whitbread.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "promotion")
public class PromotionProperties {

  private String promotionalRate;
  private String excludedRates;
}