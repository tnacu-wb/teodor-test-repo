package uk.co.whitbread.domain.model.packages.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Extras {

  private String name;
  private String id;
  private BigDecimal price;
  private String currency;
  private Integer available;
  private BigDecimal basePrice;
  private Boolean isFree;
}
