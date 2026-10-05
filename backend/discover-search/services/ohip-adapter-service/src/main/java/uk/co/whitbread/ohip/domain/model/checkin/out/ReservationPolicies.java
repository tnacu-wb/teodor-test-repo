package uk.co.whitbread.ohip.domain.model.checkin.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositPolicies;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPolicies {

  private List<DepositPolicies> depositPolicies;

}
