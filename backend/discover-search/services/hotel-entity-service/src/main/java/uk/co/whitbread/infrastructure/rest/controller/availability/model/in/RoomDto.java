package uk.co.whitbread.infrastructure.rest.controller.availability.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {

  private String tag;
  private Integer adults;
  private Integer children;
  private Integer numberOfRooms;
  private String pmsRoomType;
}
