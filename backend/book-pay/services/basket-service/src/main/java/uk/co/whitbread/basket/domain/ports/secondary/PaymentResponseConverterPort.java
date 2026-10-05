package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;

/**
 * Port for converting payment confirmation to payment response model.
 */
public interface PaymentResponseConverterPort {

  /**
   * Convert PaymentsConfirmation to PaymentResponse domain model.
   *
   * @param paymentsConfirmation the payment confirmation from external provider
   * @return the converted payment response
   */
  PaymentResponse toPaymentsResponseModel(PaymentsConfirmation paymentsConfirmation);
}
