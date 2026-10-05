package uk.co.whitbread.booking.infrastructure.rest.client.reservation.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.hotel-reservation")
public class ReservationProperties {

  private String host;
  private String operaBasketInformationAuthEndpoint;
  private String operaBasketInformationEndpoint;
  private String operaCancelReservationInformationEndpoint;
  private String operaCancelReservationEndpoint;
  private String operaDinnerAllowanceEndpoint;
  private String operaFindBookingEndpoint;
}
