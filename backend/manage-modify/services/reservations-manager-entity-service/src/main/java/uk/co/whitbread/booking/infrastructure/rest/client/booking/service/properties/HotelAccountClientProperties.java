package uk.co.whitbread.booking.infrastructure.rest.client.booking.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.hotel-account")
public class HotelAccountClientProperties {

  private String host;
  private String bookingsEndpoint;
}
