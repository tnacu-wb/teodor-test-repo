package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomRates {

  private String roomType;
  private String ratePlanCode;
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
