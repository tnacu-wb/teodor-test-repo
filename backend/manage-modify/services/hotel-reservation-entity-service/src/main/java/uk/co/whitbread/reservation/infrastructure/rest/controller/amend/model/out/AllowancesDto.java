package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;

@Data
@AllArgsConstructor
public class AllowancesDto {

  private boolean display;
  private List<BookingAllowance> values;
}
