package uk.co.whitbread.ohip.domain.model.reservation.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class StayingGuest implements SelfValidation<StayingGuest> {

  @NotEmpty
  @Schema(example = "132484")
  private String reservationId;
  @Schema(required = true)
  private Boolean sameAsBooker;
  private String language;
  private StayingGuestDetails stayingGuestDetails;
  private AccompanyingGuestDetails accompanyingGuestDetails;
  private Boolean isAccompanyingGuest;

  public StayingGuest(String reservationId, Boolean sameAsBooker, String language,
      StayingGuestDetails stayingGuestDetails, AccompanyingGuestDetails accompanyingGuestDetails,
      Boolean isAccompanyingGuest) {
    this.reservationId = reservationId;
    this.sameAsBooker = sameAsBooker;
    this.language = language;
    this.stayingGuestDetails = stayingGuestDetails;
    this.accompanyingGuestDetails = accompanyingGuestDetails;
    this.isAccompanyingGuest = isAccompanyingGuest;
    this.validateSelf();
  }
}
