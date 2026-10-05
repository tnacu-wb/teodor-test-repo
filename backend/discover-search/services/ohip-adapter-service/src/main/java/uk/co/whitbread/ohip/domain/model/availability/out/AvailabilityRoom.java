package uk.co.whitbread.ohip.domain.model.availability.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityRoom {

  private String pmsRoomType;
  private Boolean silentSubstitution;
  private Boolean isSubstitution;
  private String substitution;
  private String roomClass;
  private Boolean cotAvailable;
  private List<String> specialRequests;
  private AvailabilityRoomPriceBreakdown roomPriceBreakdown;
  private String ratePlanSet;
  private MealsIncluded mealsIncluded;
  private Integer numberOfRoomsAvailable;
}
