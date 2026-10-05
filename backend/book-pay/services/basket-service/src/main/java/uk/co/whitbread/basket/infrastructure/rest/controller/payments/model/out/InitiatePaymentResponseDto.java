package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentRequiredDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentResponseDto {
  private PaymentStatus status;
  private PaymentRequiredDetails paymentRequiredDetails;
}
