package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class CopyBookingRequest implements SelfValidation<CopyBookingRequest> {

  @NotEmpty
  private String originalBasketReference;

  @NotNull
  private String token;

  @NotNull
  @Valid
  private BookingChannel bookingChannel;
}
