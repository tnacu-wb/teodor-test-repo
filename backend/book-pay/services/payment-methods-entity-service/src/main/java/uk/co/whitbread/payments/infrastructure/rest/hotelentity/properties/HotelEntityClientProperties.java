package uk.co.whitbread.payments.infrastructure.rest.hotelentity.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.hotel-entity")
public class HotelEntityClientProperties {

  private String host;
  private String hotelInfoEndpoint;

}
