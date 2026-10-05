package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TotalTypeDto {

  @NotNull
  @Valid
  private BigDecimal amountBeforeTax;

  @NotEmpty
  @Valid
  private String currencyCode;
}
