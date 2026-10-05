package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AvailableCostDto {

  private LocalDate date;

  private BigDecimal amount;

  private String currency;

  private int qtyAvailable;

}
