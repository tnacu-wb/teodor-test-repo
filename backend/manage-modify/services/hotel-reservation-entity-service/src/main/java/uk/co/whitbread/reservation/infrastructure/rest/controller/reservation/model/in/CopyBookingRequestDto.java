package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CopyBookingRequestDto {

  @NotEmpty
  private String originalBasketReference;

  private String token;

  @NotNull
  @Valid
  private BookingChannelDto bookingChannel;
}
