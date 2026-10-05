package uk.co.whitbread.ohip.infrastructure.rest.client.hotel.ohip.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class HotelConfigOhipProperties {

  private final String hotelConfig;

  public HotelConfigOhipProperties(@Value("${config.service.ohip.hotelConfigEndpoint}") String hotelConfig) {
    this.hotelConfig = hotelConfig;
  }
}


