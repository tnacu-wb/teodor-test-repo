package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.basket.out.BasketError;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class ErroredBookingDto implements SelfValidation<ErroredBookingDto> {

  @NotNull
  private Boolean isErroredBooking;

  private BasketError basketError;

  public ErroredBookingDto(Boolean isErroredBooking, BasketError basketError) {
    this.isErroredBooking = isErroredBooking;
    this.basketError = basketError;
    this.validateSelf();
  }
}