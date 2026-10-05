package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@Builder
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
