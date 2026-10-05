package uk.co.whitbread.wallet.infrastructure.rest.client.reservations.service.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class HotelReservationsProperties {

  private final String basketReferenceEndpoint;
  private final String reservationEndpoint;
  private final String reservationHost;

  public HotelReservationsProperties(@Value("${config.service.reservation.basketReferenceEndpoint}")
                                     String basketReferenceEndpoint,
                                       @Value("${config.service.reservation.reservationEndpoint}")
                                       String reservationEndpoint,
                                       @Value("${config.service.reservation.host}") String reservationHost) {
    this.basketReferenceEndpoint = basketReferenceEndpoint;
    this.reservationEndpoint = reservationEndpoint;
    this.reservationHost = reservationHost;
  }
}
