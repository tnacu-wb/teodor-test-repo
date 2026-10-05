package uk.co.whitbread.reservation.domain.model.payment.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Allowances {

  private boolean display;
  private List<BookingAllowance> values;
}
