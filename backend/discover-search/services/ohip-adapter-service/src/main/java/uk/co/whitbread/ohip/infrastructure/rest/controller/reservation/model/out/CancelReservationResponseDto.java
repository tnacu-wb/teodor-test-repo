package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CancelReservationResponseDto {

  private List<String> cancellationIds;
  private Map<String, DepositsResponseDto> refundedDeposits;

}
