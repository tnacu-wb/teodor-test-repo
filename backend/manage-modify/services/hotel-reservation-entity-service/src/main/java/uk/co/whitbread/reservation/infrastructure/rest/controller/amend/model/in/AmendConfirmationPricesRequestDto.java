package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendConfirmationPricesRequestDto {

  @NotNull
  private String originalBookingRef;

  @NotNull
  private String tempBookingRef;

  @NotNull
  private String token;

}
