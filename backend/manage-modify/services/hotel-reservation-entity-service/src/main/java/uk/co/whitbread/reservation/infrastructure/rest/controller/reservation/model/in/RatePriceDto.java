package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatePriceDto {

  @NotNull
  @Schema(example = "2015-10-20", required = true)
  @FutureOrPresent
  private LocalDate priceStartDate;
  @NotNull
  @Schema(example = "2015-10-21", required = true)
  @FutureOrPresent
  private LocalDate priceEndDate;
  @Positive
  private BigDecimal amount;
}
