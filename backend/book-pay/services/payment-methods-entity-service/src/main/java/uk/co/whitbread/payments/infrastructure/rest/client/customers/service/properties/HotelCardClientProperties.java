package uk.co.whitbread.payments.infrastructure.rest.client.customers.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.hotel-card")
public class HotelCardClientProperties {

  private String host;
  private String companyCardsEndpoint;
}
