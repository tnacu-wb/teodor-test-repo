package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.out;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceStayDto {

  @Digits(integer = 9, fraction = 2)
  @NotNull
  private BigDecimal amount;

  private String currency;
}
