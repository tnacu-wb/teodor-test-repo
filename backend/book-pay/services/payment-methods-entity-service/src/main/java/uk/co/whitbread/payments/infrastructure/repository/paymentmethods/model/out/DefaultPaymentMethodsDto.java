package uk.co.whitbread.payments.infrastructure.repository.paymentmethods.model.out;

import java.util.List;
import lombok.Data;

@Data
public class DefaultPaymentMethodsDto {
  private List<PaymentMethodDto> defaultPaymentMethods;
  private List<PaymentOptionDto> savedCardPaymentOptions;
  private List<PaymentMethodDto> failSafePaymentMethods;
}
