package uk.co.whitbread.ocd.domain.model.tax.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxDetails {

  private List<Tax> tax;

  private BigDecimal amount;

  private String currencyCode;
}
