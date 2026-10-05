package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ccui;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;


@Data
@Builder
@NoArgsConstructor
public class PaymentCcuiRequestDto implements SelfValidation<PaymentCcuiRequestDto> {

  private String requestId;
  private PaymentCcuiDto payment;

  public PaymentCcuiRequestDto(String requestId, PaymentCcuiDto payment) {
    this.requestId = requestId;
    this.payment = payment;
    this.validateSelf();
  }
}
