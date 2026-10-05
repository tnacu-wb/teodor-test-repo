package uk.co.whitbread.ohip.infrastructure.rest.client.checkin.ohip.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class CheckInOhipProperties {

  private final String checkInEndpoint;
  private final String reservationEndpoint;

  public CheckInOhipProperties(
      @Value("${config.service.ohip.checkInEndpoint}") String checkInEndpoint,
      @Value("${config.service.ohip.reservationEndpoint}") String reservationEndpoint
  ) {
    this.checkInEndpoint = checkInEndpoint;
    this.reservationEndpoint = reservationEndpoint;
  }

}
