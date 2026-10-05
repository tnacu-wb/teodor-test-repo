package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundDto implements SelfValidation<RefundDto> {

  private PaymentType type;
  private CardDto card;
  private AmountDto amount;
  private RefundReason reason;

}
