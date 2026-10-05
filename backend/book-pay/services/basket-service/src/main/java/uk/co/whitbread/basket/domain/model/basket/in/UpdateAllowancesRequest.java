package uk.co.whitbread.basket.domain.model.basket.in;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.basket.out.BookingAllowance;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class UpdateAllowancesRequest implements SelfValidation<UpdateAllowancesRequest> {

  private List<BookingAllowance> bookingAllowances;

  public UpdateAllowancesRequest(List<BookingAllowance> bookingAllowances) {
    this.bookingAllowances = bookingAllowances;
    this.validateSelf();
  }
}
