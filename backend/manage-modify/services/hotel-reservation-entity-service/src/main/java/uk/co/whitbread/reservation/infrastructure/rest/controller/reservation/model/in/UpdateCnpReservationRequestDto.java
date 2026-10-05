package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCnpReservationRequestDto {

  private String language;
  private String countryCode;
  @Schema
  @NotNull
  private BusinessAccountCnpDto businessAccount;
  @Schema
  @Valid
  private BookerDetailsCnpDto booker;
}
