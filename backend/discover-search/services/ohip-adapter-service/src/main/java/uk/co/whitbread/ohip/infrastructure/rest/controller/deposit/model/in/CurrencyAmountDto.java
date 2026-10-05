package uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyAmountDto {

  @NotNull
  @Schema(required = true)
  private BigDecimal amount;

  @NotNull
  @Schema(required = true)
  private String currencyCode;

}
