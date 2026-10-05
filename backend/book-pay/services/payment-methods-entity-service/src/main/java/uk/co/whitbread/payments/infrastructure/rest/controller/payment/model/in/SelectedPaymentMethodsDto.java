package uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SelectedPaymentMethodsDto {

  @NotEmpty
  @Schema(example = "DJERSE8792035", required = true,
      description = "Basket reference for which the payment methods needs to be retrieved.")
  private String basketReference;

  @NotEmpty
  @Schema(example = "PIBA", required = true,
      description = "The selected card type: CARD or PIBA.")
  private String type;

  @NotEmpty
  @Schema(example = "PAY_NOW", required = true, description = "Selected payment option.")
  private String selectedPaymentOption;

  @Schema(example = "true", description = "isCiol flag indicating if the request is for CIOL.")
  private Boolean isCiol;
}
