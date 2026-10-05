package uk.co.whitbread.booking.domain.model.history.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookingResponse {

  private List<Booking> bookings;
  private Integer pageIndex;
  private int pageSize;
  private int totalSize;
  private TypesTotals totals;
  private String continuationToken;
}
