package uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceInfoDto {

  private BigDecimal amountBeforeTax;

  private BigDecimal amountAfterTax;

  private String currencyCode;

  private TaxDetailsDto taxes;

}
