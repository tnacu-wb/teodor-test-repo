package uk.co.whitbread.ohip.infrastructure.rest.controller.packages.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationPackageDto {

  private String code;
  private BigDecimal unitPrice;
  private String currency;
}
