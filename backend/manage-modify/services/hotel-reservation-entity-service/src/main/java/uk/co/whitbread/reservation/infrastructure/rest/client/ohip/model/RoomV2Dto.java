package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomV2Dto {

  private String tag;
  private List<String> roomTypes;
  private Integer adults;
  private Integer children;
  private Integer numberOfRooms;
}
