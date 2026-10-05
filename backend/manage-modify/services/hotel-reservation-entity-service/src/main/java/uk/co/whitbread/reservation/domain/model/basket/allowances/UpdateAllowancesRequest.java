package uk.co.whitbread.reservation.domain.model.basket.allowances;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowance;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAllowancesRequest implements SelfValidation<UpdateAllowancesRequest> {

  @NotNull
  private List<BookingAllowance> bookingAllowances;
}
