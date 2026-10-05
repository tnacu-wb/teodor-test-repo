package uk.co.whitbread.ohip.infrastructure.rest.client.hotel.ohip.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class HotelDetailsOhipProperties {

  private final String hotelDetails;

  public HotelDetailsOhipProperties(@Value("${config.service.ohip.hotelDetailsEndpoint}") String hotelDetails) {
    this.hotelDetails = hotelDetails;
  }

}
