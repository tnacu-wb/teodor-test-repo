package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomTypeInfoDto {

  private String roomClass;
  private Boolean accessible;
  private String roomType;
  private Integer numberOfRooms;

}