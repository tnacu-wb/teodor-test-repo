package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class PaymentErrorDto implements SelfValidation<PaymentErrorDto> {

  @NotNull
  private String code;
  @NotNull
  private String description;

  public PaymentErrorDto(String code, String description) {
    this.code = code;
    this.description = description;
    this.validateSelf();
  }
}
