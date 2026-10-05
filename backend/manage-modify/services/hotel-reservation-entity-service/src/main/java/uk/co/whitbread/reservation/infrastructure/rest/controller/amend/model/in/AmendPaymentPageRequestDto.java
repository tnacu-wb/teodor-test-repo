package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BookingChannelDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendPaymentPageRequestDto {

  @NotNull
  private String originalBookingRef;
  @NotNull
  private String tempBookingRef;
  @NotNull
  private String token;
  @NotNull
  private BookingChannelDto bookingChannel;
  @NotNull
  private String country;
}
