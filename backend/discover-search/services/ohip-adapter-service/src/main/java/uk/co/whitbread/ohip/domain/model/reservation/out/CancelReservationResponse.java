package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CancelReservationResponse {

  private List<String> cancellationIds;
  private Map<String, DepositsResponse> refundedDeposits;

}
