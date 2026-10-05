package uk.co.whitbread.booking.infrastructure.rest.client.reservation.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "reservations-manager")
public class ReservationsManagerProperties {

  private Integer oldBookingThreshold;
}
