package uk.co.whitbread.domain.model.srp.out;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cost implements Comparable<Cost>, Serializable {

  private BigDecimal netTotal;
  private String currencyCode;

  @Override
  public int compareTo(Cost o) {
    return getNetTotal().compareTo(o.getNetTotal());
  }
}
