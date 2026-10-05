package uk.co.whitbread.reservation.infrastructure.rest.client.hotel.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.hotel-entity")
public class HotelAvailabilityClientProperties {
  private String host;
  private String hotelsAvailabilityV2Endpoint;
}
