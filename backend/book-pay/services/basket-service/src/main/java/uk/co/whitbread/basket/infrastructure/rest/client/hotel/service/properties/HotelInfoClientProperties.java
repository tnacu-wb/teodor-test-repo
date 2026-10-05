package uk.co.whitbread.basket.infrastructure.rest.client.hotel.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.hotel-entity")
public class HotelInfoClientProperties {

  private String host;
  private String hotelInfoEndpoint;
}
