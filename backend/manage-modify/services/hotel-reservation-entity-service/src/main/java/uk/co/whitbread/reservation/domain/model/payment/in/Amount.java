package uk.co.whitbread.reservation.domain.model.payment.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Amount {

  @NotEmpty
  private String currency;
  @NotNull
  private BigDecimal minorUnits;

}
