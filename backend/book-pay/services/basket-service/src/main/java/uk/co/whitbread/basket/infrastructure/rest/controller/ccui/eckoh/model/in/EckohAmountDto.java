package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohAmountDto {
  private String currency;
  private int minorUnits;
}
