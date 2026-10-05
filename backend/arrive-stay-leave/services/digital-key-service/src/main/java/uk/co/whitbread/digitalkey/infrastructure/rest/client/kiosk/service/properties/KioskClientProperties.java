package uk.co.whitbread.digitalkey.infrastructure.rest.client.kiosk.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.kiosk")
public class KioskClientProperties {

  private String host;
  private String kioskAllocate;
  private String kioskCheckIn;

}
