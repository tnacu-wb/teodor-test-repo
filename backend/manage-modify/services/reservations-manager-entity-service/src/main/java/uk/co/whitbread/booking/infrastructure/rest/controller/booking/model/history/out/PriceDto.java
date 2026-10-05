package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.history.out;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PriceDto {

  @Digits(integer = 9, fraction = 2)
  @NotNull
  private BigDecimal amount;

  private String currency;
}
