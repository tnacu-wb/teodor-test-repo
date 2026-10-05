package uk.co.whitbread.refund.processor.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

  private String type;
  private String subType;
  private String environment;
  private Card card;
  private Amount amount;
  private Billing billing;
}
