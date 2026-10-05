package uk.co.whitbread.booking.infrastructure.rest.client.ohip.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.ohip")
public class OhipAdapterProperties {

  private String host;
  private Integer connectionTimeoutMillis;
  private Integer responseTimeoutMillis;
  private String hotelInfoEndpoint;
  private String lightweightReservationsByIdsEndpoint;
  private String reservationsPaymentTypeByReservationIds;
  private String packageGroupsEndPoint;
}