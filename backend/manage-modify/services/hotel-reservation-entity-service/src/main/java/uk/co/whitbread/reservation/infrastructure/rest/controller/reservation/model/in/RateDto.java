package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RateDto {

  @NotEmpty
  @Schema(example = "2015-10-20", required = true)
  private String startDate;
  @NotEmpty
  @Schema(example = "2015-10-21", required = true)
  private String endDate;
  @NotNull
  @Schema(example = "100.4", required = true)
  private BigDecimal amountBeforeTax;
  @NotEmpty
  @Schema(example = "EUR", required = true)
  private String currencyCode;
}
