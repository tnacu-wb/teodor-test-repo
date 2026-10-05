package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class StayingGuest {

  private String reservationId;
  @NotNull
  private Boolean sameAsBooker;
  private String language;
  @Valid
  private StayingGuestDetails stayingGuestDetails;
  private AccompanyingGuestDetails accompanyingGuestDetails;
  private Boolean isAccompanyingGuest;
}
