package uk.co.whitbread.kiosk.infrastructure.rest.client.reservation.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.reservation")
public class ReservationProperties {

  private String host;
  private String confirmReservation;

}
