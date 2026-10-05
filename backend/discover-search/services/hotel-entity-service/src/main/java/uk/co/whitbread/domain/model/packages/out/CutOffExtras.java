package uk.co.whitbread.domain.model.packages.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CutOffExtras {

  private Boolean isEciCutOffCiol;
  private Boolean isEciCutOffBooking;
  private Boolean isLcoCutOffCiol;
  private Boolean isLcoCutOffBooking;
  private Long ciolCurrentDateAvailableRooms;
}
