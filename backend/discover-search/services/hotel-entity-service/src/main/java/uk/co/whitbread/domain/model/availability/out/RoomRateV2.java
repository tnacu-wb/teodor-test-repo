package uk.co.whitbread.domain.model.availability.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRateV2 {

  private String ratePlanCode;
  private String displaySet;
  private String currencyCode;
  private RoomRateInfoV2 roomRateInfo;
  private String globalCompanyId;
}
