package uk.co.whitbread.refund.processor.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refund {

  private PaymentType type;
  private Card card;
  private Amount amount;
  private RefundReason reason;

}
