package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeDto {

  private String roomType;
  private Integer adults;
  private Integer children;
  private Boolean cotRequested;
  private List<RoomDto> rooms;

}
