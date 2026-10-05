package uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.reservation")
public class HotelReservationProperties {

  private String host;
  private String findBookingEndpoint;
  private String cancelInformationEndpoint;
  private int responseTimeout;
  private int connectionTimeout;

}
