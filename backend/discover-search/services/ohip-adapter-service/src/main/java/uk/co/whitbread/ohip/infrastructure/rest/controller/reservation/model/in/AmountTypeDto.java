package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AmountTypeDto {

  @NotNull
  @Valid
  private TotalTypeDto base;

  @NotNull
  @Schema(example = "2025-03-31", required = true)
  @Valid
  private String start;

  @NotNull
  @Schema(example = "2025-03-31", required = true)
  @Valid
  private String end;
}
