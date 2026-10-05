package uk.co.whitbread.content.infrastructure.rest.client.aem.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.aem.apps.homepage")
public class AemHomepageProperties {

  private String countryParam;
  private String languageParam;
  private String pathParam;
  private String subchannelParam;
}