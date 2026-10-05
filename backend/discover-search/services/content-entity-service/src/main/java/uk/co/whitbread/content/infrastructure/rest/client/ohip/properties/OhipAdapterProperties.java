package uk.co.whitbread.content.infrastructure.rest.client.ohip.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.ohip")
public class OhipAdapterProperties {

  private String host;
  private String hotelInfoEndpoint;
  private String ratePlansEndpoint;

}
