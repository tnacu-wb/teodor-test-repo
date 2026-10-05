package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomRatesDto {

  private TotalDto total;
  private CheckInRatesDto rates;
  private List<StayProfilesDto> stayProfiles;
  private GuestCountsDto guestCounts;
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
