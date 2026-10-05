package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class RefundRequestDto implements SelfValidation<RefundRequestDto> {

  @NotNull
  private String hotelCode;

  @NotNull
  private RefundDto refund;

  @NotNull
  private BookingDto booking;

  @NotNull
  private RefundType refundType;

  public RefundRequestDto(String hotelCode, RefundDto refund,
                          BookingDto booking, RefundType refundType) {
    this.hotelCode = hotelCode;
    this.refund = refund;
    this.booking = booking;
    this.refundType = refundType;
    this.validateSelf();
  }
}
