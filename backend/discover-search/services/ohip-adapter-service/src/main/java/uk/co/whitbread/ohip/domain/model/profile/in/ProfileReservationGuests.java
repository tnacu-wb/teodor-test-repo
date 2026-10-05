package uk.co.whitbread.ohip.domain.model.profile.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ProfileReservationGuests {

  private KioskProfileInfo profileInfo;
  private boolean primary;

}
