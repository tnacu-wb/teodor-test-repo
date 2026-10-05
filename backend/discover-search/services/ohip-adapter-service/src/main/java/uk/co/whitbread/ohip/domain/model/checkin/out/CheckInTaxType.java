package uk.co.whitbread.ohip.domain.model.checkin.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInTaxType {

  private String code;
  private String description;
  private boolean collectingAgentTax;
  private boolean printAutoAdjust;

}
