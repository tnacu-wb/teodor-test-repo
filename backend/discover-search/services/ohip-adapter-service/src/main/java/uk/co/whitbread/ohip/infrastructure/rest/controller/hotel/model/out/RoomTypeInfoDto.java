package uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeInfoDto {

  private String roomClass;
  private Boolean accessible;
  private String roomType;
  private String numberOfRooms;
}
