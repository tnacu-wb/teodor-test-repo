package uk.co.whitbread.infrastructure.rest.controller.availability.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRateV2Dto {

  private String ratePlanCode;
  private String displaySet;
  private String currencyCode;
  private RoomRateInfoV2Dto roomRateInfo;
  private String globalCompanyId;
}
