package uk.co.whitbread.basket.domain.model.basket.out;

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
