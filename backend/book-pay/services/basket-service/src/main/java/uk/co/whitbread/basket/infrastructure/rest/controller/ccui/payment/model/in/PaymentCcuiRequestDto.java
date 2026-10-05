package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.BookingDto;

@Data
@Builder
@NoArgsConstructor
public class PaymentCcuiRequestDto implements SelfValidation<PaymentCcuiRequestDto> {

  private String requestId;
  private PaymentCcuiDto payment;
  private BookingDto booking;

  public PaymentCcuiRequestDto(String requestId, PaymentCcuiDto payment, BookingDto booking) {
    this.requestId = requestId;
    this.payment = payment;
    this.booking = booking;
    this.validateSelf();
  }
}
