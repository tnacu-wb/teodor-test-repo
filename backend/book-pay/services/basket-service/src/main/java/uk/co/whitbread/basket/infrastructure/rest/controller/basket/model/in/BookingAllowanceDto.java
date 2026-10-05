package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class BookingAllowanceDto implements SelfValidation<BookingAllowanceDto> {

  @NotNull
  private String allowance;
  @PositiveOrZero
  private BigDecimal budget;

  public BookingAllowanceDto(String allowance, BigDecimal budget) {
    this.allowance = allowance;
    this.budget = budget;
    this.validateSelf();
  }

}
