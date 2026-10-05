package uk.co.whitbread.ohip.infrastructure.rest.controller.hotel.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypesInfoDto {

  private List<RoomTypeInfoDto> roomType;
  private String hotelId;
}
