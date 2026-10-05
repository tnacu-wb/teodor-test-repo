package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReservationProfiles {

  private String bookerProfileId;
  private String companyProfileId;
}
