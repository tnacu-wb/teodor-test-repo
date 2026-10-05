package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.out.Deposits;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

  @NotNull
  private String bookingReference;

  private String email;

  private String type;

  private List<Deposits> deposits;

  private boolean failedRefund;
}
