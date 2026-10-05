package uk.co.whitbread.ohip.domain.model.checkin.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomRates {

  private Total total;
  private CheckInRates rates;
  private List<StayProfiles> stayProfiles;
  private GuestCounts guestCounts;
  private String roomType;
  private String ratePlanCode;
  private String start;
  private String end;
  private boolean suppressRate;
  private String marketCode;
  private String marketCodeDescription;
  private String sourceCode;
  private String sourceCodeDescription;
  private int numberOfUnits;
  private String roomId;
  private boolean pseudoRoom;
  private String roomTypeCharged;
  private boolean houseUseOnly;
  private boolean complimentary;
  private boolean fixedRate;
  private boolean discountAllowed;
  private boolean bogoDiscount;

}
