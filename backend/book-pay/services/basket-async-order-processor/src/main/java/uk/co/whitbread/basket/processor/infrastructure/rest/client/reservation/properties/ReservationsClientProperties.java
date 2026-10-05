package uk.co.whitbread.basket.processor.infrastructure.rest.client.reservation.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.reservation")
public class ReservationsClientProperties {

  private String host;
  private String confirmReservationEndpoint;
  private String cancelReservationEndpoint;
  private String amendReservationEndpoint;
}

