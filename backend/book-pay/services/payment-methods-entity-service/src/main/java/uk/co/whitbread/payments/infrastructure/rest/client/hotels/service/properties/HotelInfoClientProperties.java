package uk.co.whitbread.payments.infrastructure.rest.client.hotels.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.content")
public class HotelInfoClientProperties {

  private String host;
  private String paymentInformationEndpoint;
}