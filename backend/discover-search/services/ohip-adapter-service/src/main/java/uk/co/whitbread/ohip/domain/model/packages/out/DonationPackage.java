package uk.co.whitbread.ohip.domain.model.packages.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationPackage {

  private String code;
  private BigDecimal unitPrice;
  private String currency;
}
