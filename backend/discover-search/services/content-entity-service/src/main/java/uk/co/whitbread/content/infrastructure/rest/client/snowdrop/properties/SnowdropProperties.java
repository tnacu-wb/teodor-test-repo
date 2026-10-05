package uk.co.whitbread.content.infrastructure.rest.client.snowdrop.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.snowdrop")
public class SnowdropProperties {

  private String host;
  private String hotelSearchEndpoint;
}
