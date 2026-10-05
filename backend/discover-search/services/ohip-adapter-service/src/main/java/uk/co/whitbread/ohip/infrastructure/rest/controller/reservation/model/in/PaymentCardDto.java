package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentCardDto {

  @NotNull
  @Schema(example = "Va", required = true)
  private String cardType;
  @NotNull
  @Schema(example = "4111111111111111", required = true)
  private String token;
  @NotNull
  @Schema(example = "2025-03-31", required = true)
  private String expirationDate;
  //@NotNull --> fix for amend add new room
  @Schema(example = "Tester", required = false)
  private String cardHolderName;
  @NotNull
  @Schema(example = "1234", required = true)
  private String cardNumberLast4Digits;
  @Schema(example = "acd1234")
  private String citId;
}
