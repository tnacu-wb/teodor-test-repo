package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class UpdateDiscountRequestDto implements SelfValidation<UpdateDiscountRequestDto> {
  @NotEmpty
  private String basketReference;
  @DecimalMin(value = "0.0")
  private BigDecimal discountAmount;

  public UpdateDiscountRequestDto(String basketReference, BigDecimal discountAmount) {
    this.basketReference = basketReference;
    this.discountAmount = discountAmount;
    this.validateSelf();
  }
}
