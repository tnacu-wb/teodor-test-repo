package uk.co.whitbread.basket.domain.model.ccuieckoh.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohAmount {

  private String currency = "GBP";
  private int minorUnits = 0;
}
