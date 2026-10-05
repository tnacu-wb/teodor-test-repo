package uk.co.whitbread.booking.infrastructure.rest.client.basket.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.basket")
public class BasketProperties {

  private String host;
  private String operaBasketOptionInformationEndpoint;
  private String operaBasketInformationByBookingEndpoint;
  private String operaResendEmailEndpoint;
  private String getBasketsForBookingRefs;
}
