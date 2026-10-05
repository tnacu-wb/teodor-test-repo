package uk.co.whitbread.ohip.domain.model.profile.in;

import java.util.List;
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
public class ProfileReservations {

  private List<ProfileReservationIdList> reservationIdList;
  private List<ProfileReservationGuests> reservationGuests;

}
