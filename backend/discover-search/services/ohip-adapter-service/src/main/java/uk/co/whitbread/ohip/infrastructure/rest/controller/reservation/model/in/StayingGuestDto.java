package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import uk.co.whitbread.ohip.domain.model.reservation.in.AccompanyingGuestDetails;

@Data
public class StayingGuestDto {

  @NotEmpty
  @Schema(required = true)
  private String reservationId;
  @Schema(required = true)
  private Boolean sameAsBooker;
  private String language;
  @Valid
  @Schema(required = true)
  private StayingGuestDetailsDto stayingGuestDetails;
  private AccompanyingGuestDetails accompanyingGuestDetails;
  private Boolean isAccompanyingGuest;
}
