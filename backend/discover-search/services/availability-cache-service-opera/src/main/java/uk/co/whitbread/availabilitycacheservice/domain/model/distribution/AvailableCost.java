package uk.co.whitbread.availabilitycacheservice.domain.model.distribution;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AvailableCost {

  private LocalDate date;
  private BigDecimal amount;
  private String currency;
  private int qtyAvailable;
}
