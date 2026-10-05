package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookingAllowancesResponse {

  private List<BookingAllowance> bookingAllowances;
  private String businessNotes;
}
