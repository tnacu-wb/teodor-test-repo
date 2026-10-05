package uk.co.whitbread.reservation.infrastructure.rest.client.content.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.content")
public class ContentProperties {

  private String host;
  private String contentIndexHeaderEndpoint;
  private String businessNotesEndpoint;
  private String hotelPaymentInformation;
  private String hotelInformationEndpoint;
  private String hotelRateInformationEndpoint;
  private String searchRulesEndpoint;
}
