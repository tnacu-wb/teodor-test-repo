package uk.co.whitbread.reservation.domain.model.amend.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendPaymentPageRequest {

  @NotNull
  private String originalBookingRef;
  @NotNull
  private String tempBookingRef;
  @NotNull
  private String token;
  @NotNull
  private BookingChannel bookingChannel;
  @NotNull
  private String country;
}
