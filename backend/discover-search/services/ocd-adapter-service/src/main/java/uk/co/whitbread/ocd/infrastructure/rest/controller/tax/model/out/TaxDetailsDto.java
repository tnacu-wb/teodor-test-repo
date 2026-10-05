package uk.co.whitbread.ocd.infrastructure.rest.controller.tax.model.out;

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
public class TaxDetailsDto {

  private List<TaxDto> tax;

  private BigDecimal amount;

  private String currencyCode;
}
