package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessAllowanceDto {

  @NotNull
  @Schema(required = true)
  private BigDecimal budget;

  @NotNull
  @Schema(required = true)
  private String allowance;

  @NotNull
  @Schema(required = true)
  private Boolean isAuthorised;
}
