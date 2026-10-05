package uk.co.whitbread.domain.model.availability.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

  private String roomType;
  private String pmsRoomType;
  private Boolean silentSubstitution;
  private Boolean isSubstitution;
  private String substitution;
  private String roomClass;
  private Boolean cotAvailable;
  private List<String> specialRequests;
  private RoomPriceBreakdown roomPriceBreakdown;
  private MealsIncluded mealsIncluded;
  private Integer numberOfRoomsAvailable;
  private SoftBundles softBundles;

}
