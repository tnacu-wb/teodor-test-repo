package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Reservations {

  private List<ReservationInfo> reservationInfo;
  private int totalPages;
  private int offset;
  private int limit;
  private boolean hasMore;
  private int totalResults;
}
