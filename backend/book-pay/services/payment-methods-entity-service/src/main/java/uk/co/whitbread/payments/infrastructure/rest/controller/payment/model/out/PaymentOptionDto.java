package uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentOptionDto {

  @Schema(example = "Pay Now/Pay on Arrival", description = "Payment option",
      implementation = PaymentType.class, required = true)
  @NotNull
  private PaymentType type;

  @Schema(example = "1", description = "order of payment option")
  @NotNull
  private int order;

  @Schema(example = "true", description = "flag to enable payment option")
  @NotNull
  private boolean enabled;

}
