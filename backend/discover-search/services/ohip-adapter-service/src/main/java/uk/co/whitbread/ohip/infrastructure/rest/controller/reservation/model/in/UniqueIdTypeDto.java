package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UniqueIdTypeDto {

  @NotNull()
  @Valid
  @Schema(required = true)
  private String id;

  @NotNull()
  @Valid
  @Schema(required = true)
  private String type;
}
