package uk.co.whitbread.ocd.domain.model.tax.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceInfo {

  private BigDecimal amountBeforeTax;

  private BigDecimal amountAfterTax;

  private String currencyCode;

  private TaxDetails taxes;
}
