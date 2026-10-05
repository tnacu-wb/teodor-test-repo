package uk.co.whitbread.basket.domain.model.ccuieckoh.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EckohPayment {

  private String type;
  private String subType;
  private String environment;
  private EckohBilling billing;
  private EckohAmount amount;
}
