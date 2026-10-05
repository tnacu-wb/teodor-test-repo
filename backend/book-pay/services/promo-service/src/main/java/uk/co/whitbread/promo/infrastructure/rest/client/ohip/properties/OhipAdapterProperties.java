package uk.co.whitbread.promo.infrastructure.rest.client.ohip.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "config.service.ohip")
public class OhipAdapterProperties {

  private String promotionsEndpoint;
  private String host;

}
