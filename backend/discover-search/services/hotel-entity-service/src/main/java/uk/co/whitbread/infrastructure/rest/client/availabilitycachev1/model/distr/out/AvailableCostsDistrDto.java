package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailableCostsDistrDto {

  private String date;

  private BigDecimal amount;

  private String currency;

  private Integer qtyAvailable;

}
