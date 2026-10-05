package uk.co.whitbread.infrastructure.rest.controller.availability.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomStayV2Dto {

  private String roomClass;
  private List<RoomTypeV2Dto> roomTypes;
}
