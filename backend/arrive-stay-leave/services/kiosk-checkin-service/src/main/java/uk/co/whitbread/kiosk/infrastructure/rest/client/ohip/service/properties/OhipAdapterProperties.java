package uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.ohip")
public class OhipAdapterProperties {

  private String host;
  private String getVacantRoomsEndpoint;
  private String allocateRoomEndpoint;
  private String createProfileEndpoint;
  private String checkInEndpoint;
  private String housekeepingStatusEndpoint;
  private String reservationPreferences;
  private String updateCommentsEndpoint;
  private String updateProfileEndpoint;
  private String reservationAmountsEndpoint;

}
