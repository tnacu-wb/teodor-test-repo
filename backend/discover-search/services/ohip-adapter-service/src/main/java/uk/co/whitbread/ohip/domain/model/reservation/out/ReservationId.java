package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReservationId {

  private List<ReservationDetails> reservation;
  private int totalPages;
  private int offset;
  private int limit;
  private boolean hasMore;
  private int totalResults;
}