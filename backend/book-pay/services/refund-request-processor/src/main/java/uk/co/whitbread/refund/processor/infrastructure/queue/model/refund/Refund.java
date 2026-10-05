package uk.co.whitbread.refund.processor.infrastructure.queue.model.refund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Refund {

  private PaymentType type;
  private Card card;
  private Amount amount;
  private RefundReason reason;

}
