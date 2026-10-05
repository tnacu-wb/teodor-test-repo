package uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositFolioChargeDto {

  @NotNull
  @Schema(required = true)
  private String transactionCode;

  @NotNull
  @Schema(required = true)
  private Integer quantity;

  @NotNull
  @Schema(required = true)
  private String reference;

  @NotNull
  @Schema(required = true)
  private CurrencyAmountDto currencyAmount;

}
