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
public class Tax {

  private String description;

  private String code;

  private BigDecimal amount;

  private String currencyCode;
}
