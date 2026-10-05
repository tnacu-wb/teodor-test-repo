package uk.co.whitbread.basket.domain.model.payments.in;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequest implements SelfValidation<DiscountRequest> {
  @NotEmpty
  private String basketReference;
  @DecimalMin(value = "0.0")
  private BigDecimal discountAmount;
}
