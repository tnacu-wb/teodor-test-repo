package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.ohip.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class EckohOhiPproperties {

  private String externalReservationEndpoint;
  private String reservationEndpoint;
  private String hubId;

  public EckohOhiPproperties(
      @Value("${config.service.ohip.externalReservationEndpoint}") String externalReservationEndpoint,
      @Value("${config.service.ohip.reservationEndpoint}") String reservationEndpoint,
      @Value("${config.service.ohip.hubId}") String hubId
  ) {
    this.externalReservationEndpoint = externalReservationEndpoint;
    this.reservationEndpoint = reservationEndpoint;
    this.hubId = hubId;
  }

}
