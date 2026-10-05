package uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.service.properties;

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
  private String externalReservationEndpoint;
  private String allocateRoomEndpoint;
  private String checkInEndpoint;
  private String reservationsByBasketReservationIds;
  private String reservationPreferences;
  private String updateUdfc20Endpoint;

}
