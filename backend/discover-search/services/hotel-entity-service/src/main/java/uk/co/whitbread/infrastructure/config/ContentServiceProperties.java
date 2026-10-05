package uk.co.whitbread.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Data
@EnableAsync
@Configuration
@ConfigurationProperties(prefix = "config.service.content-service")
public class ContentServiceProperties {

  private String host;
  private String hotelFacilitiesFilterUpdateEndpoint;
  private String hotelsOpeningSoonCacheUpdateEndpoint;
  private String hotelInformationEndpoint;
  private String extrasLabels;
  private String upsellItemsEndpoint;
  private String searchRulesEndpoint;
  private String categoryLabelsEndpoint;
  private String globalConfigEndpoint;
}
