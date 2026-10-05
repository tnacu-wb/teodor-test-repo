package uk.co.whitbread.ohip.domain.model.availability.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRate {

  private String ratePlanCode;
  private String displaySet;
  private String currencyCode;
  private String globalCompanyId;
  private RoomRateInfoV2 roomRateInfo;
}
