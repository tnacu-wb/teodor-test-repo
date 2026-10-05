package uk.co.whitbread.basket.domain.model.refund.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Refund {

  private PaymentType type;
  private Card card;
  private Amount amount;
  private RefundReason reason;

}
