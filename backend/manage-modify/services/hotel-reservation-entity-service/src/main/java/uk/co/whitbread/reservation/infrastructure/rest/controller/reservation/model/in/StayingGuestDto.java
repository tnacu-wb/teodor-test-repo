package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class StayingGuestDto {

  @Schema(example = "132484")
  private String reservationId;
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private Boolean sameAsBooker;
  private String language;
  @Valid
  @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
  private StayingGuestDetailsDto stayingGuestDetails;
  private AccompanyingGuestDetailsDto accompanyingGuestDetails;
  private Boolean isAccompanyingGuest;
}
