package uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.ohip.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class RoomAllocationOhipProperties {

  private final String vacantRoomsEndpoint;
  private final String roomsAssignmentEndpoint;
  private final String getHousekeeping;

  public RoomAllocationOhipProperties(
      @Value("${config.service.ohip.vacantRoomsEndpoint}") String vacantRoomsEndpoint,
      @Value("${config.service.ohip.roomsAssignmentEndpoint}") String roomsAssignmentEndpoint,
      @Value("${config.service.ohip.getHousekeeping}") String getHousekeeping) {
    this.vacantRoomsEndpoint = vacantRoomsEndpoint;
    this.roomsAssignmentEndpoint = roomsAssignmentEndpoint;
    this.getHousekeeping = getHousekeeping;
  }

}
