package uk.co.whitbread.kiosk.domain.model.checkin.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaxType {

  private String code;
  private String description;
  private boolean collectingAgentTax;
  private boolean printAutoAdjust;

}
