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
public class RoomDto {

  private String pmsRoomType;
  private Boolean silentSubstitution;
  private Boolean isSubstitution;
  private String substitution;
  private String roomClass;
  private Boolean cotAvailable;
  private List<String> specialRequests;
  private RoomPriceBreakdownDto roomPriceBreakdown;
  private MealsIncludedDto mealsIncluded;
  private Integer numberOfRoomsAvailable;

}
