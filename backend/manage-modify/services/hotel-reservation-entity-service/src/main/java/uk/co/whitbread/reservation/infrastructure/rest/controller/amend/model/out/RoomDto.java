package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {

  private String pmsRoomType;
  private Boolean silentSubstitution;
  private String roomClass;
  private Boolean cotAvailable;
  private List<String> specialRequests;
  private RoomPriceBreakdownDto roomPriceBreakdown;
  private MealsIncludedDto mealsIncluded;
  private Integer numberOfRoomsAvailable;

}
