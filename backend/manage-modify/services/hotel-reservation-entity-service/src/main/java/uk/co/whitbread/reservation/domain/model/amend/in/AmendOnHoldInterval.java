package uk.co.whitbread.reservation.domain.model.amend.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmendOnHoldInterval {

  private String amendArrivalDate;
  private String amendDepartureDate;
}
