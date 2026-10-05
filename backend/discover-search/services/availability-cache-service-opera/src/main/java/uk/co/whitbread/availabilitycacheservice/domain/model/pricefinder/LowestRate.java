package uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LowestRate implements Comparable<LowestRate> {

  private LocalDate availableDate;
  private BigDecimal minimumRate;

  @Override
  public int compareTo(@NotNull LowestRate o) {
    return this.availableDate.compareTo(o.availableDate);
  }
}
