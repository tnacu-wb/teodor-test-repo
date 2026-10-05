package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RatePrice implements SelfValidation<RatePrice> {
  @NotNull
  @FutureOrPresent
  private LocalDate priceStartDate;
  @NotNull
  @FutureOrPresent
  private LocalDate priceEndDate;
  @NotNull
  @Positive
  private BigDecimal amount;

  public RatePrice(LocalDate priceStartDate, LocalDate priceEndDate, BigDecimal amount) {
    this.priceStartDate = priceStartDate;
    this.priceEndDate = priceEndDate;
    this.amount = amount;
    this.validateSelf();
  }
}
