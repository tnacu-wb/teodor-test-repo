package uk.co.whitbread.avail.business.events.infrastructure.client.ocd.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.ocd-adapter")
public class OcdAdapterProperties {
  private String host;
  private String offerEndpoint;
}