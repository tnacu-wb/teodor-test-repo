package uk.co.whitbread.ocd.domain.model.tax.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxResponse {

  private String roomType;

  private String ratePlanCode;

  private PriceInfo total;
}
