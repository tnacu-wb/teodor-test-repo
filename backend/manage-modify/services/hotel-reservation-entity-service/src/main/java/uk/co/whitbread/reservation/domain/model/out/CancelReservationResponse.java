package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelReservationResponse {

  private String basketReference;
  private Map<String, DepositsResponse> refundedDeposits;
  private List<String> cancellationIds;
}
