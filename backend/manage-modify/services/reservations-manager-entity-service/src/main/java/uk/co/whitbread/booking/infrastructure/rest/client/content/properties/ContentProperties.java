package uk.co.whitbread.booking.infrastructure.rest.client.content.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.content")
public class ContentProperties {

  private String host;
  private String hotelRateInformationEndpoint;
  private String operaReservationRateInformationEndpoint;
  private String aemMealsInfoEndpoint;
  private String hotelInfoEndpoint;

}
