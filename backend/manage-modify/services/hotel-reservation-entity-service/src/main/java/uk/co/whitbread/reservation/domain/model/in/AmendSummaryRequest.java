package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AmendSummaryRequest {

  @NotEmpty
  private String originalBasketRef;

  @NotEmpty
  private String copyBasketRef;

  private String token;

  @NotNull
  @Valid
  private BookingChannel bookingChannel;

  @NotEmpty
  private String country;

}
