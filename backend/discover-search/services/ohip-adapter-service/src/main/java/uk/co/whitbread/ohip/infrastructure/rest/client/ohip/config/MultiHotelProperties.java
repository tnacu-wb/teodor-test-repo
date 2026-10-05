package uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "hotels")
public class MultiHotelProperties {
  private int noOfAllowedHotels;
}