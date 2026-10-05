package uk.co.whitbread.avail.business.events.infrastructure.client.content.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.content")
public class ContentProperties {
  private String host;
  private String globalConfigEndpoint;
}
