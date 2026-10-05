package uk.co.whitbread.promo.infrastructure.rest.client.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "promo-config")
public class PromoBatchProperties {
  private int prefixLength;
  private int maxBatchSize;
  private int minCodeSuffixLength;
  private int maxCodeSuffixLength;
}