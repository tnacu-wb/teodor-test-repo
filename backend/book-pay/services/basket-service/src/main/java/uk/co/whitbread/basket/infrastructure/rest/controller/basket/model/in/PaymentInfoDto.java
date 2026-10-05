package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentOption;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;


@Data
@Builder
@NoArgsConstructor
public class PaymentInfoDto implements SelfValidation<PaymentInfoDto> {

  private PaymentOption paymentOption;
  private String paymentId;

  public PaymentInfoDto(PaymentOption paymentOption, String paymentId) {
    this.paymentOption = paymentOption;
    this.paymentId = paymentId;
    this.validateSelf();
  }
}

