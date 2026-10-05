package uk.co.whitbread.avail.business.events.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "mockservice")
public class OhipGraphQlMockProperties {

  private boolean enabled;
  private String url;

}
