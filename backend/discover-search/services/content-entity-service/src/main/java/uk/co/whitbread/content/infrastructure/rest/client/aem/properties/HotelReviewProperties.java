package uk.co.whitbread.content.infrastructure.rest.client.aem.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.hotelreviewservice")
public class HotelReviewProperties {

  private String host;
  private String reviewsForSingleHotelEndpoint;
}
