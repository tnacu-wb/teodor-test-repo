package uk.co.whitbread.booking.domain.model.information.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingPrice {

  private BigDecimal amount;
  private String currency;
}
