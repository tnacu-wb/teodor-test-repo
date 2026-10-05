package uk.co.whitbread.reservation.infrastructure.rest.client.hotel.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomDto {

  private String tag;
  private Integer adults;
  private Integer children;
  private Integer numberOfRooms;
  private String pmsRoomType;
}
