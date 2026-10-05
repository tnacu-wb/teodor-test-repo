package uk.co.whitbread.dashboard.infrastructure.rest.client.hotelaccount.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.hotel-account")
public class HotelAccountProperties {

  private String host;
  private String customerStaysEndpoint;

}
