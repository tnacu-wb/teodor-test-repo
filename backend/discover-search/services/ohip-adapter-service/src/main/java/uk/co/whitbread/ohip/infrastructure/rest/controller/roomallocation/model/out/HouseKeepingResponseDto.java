package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseKeepingResponseDto {

  private String hotelId;
  private String roomId;
  private String status;


}
