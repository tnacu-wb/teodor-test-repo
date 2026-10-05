package uk.co.whitbread.booking.domain.model.history.out;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TypesTotals {

  private int cancelled;
  private int upcoming;
  private int past;
  private int checkedIn;
}
