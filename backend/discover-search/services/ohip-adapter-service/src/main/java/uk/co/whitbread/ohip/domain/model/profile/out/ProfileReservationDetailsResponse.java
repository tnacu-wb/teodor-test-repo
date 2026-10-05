package uk.co.whitbread.ohip.domain.model.profile.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileReservationDetailsResponse {

  private ProfileReservationsOut reservations;

}
