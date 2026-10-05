package uk.co.whitbread.dashboard.infrastructure.rest.client.hotelinfo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.hotel-info")
public class HotelInfoProperties {

  private String host;
  private String hotelInfoEndpoint;
}
