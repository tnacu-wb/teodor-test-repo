package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PersonNameTypeDto {
  @Schema(example = "John")
  private String givenName;
  @Schema(example = "Doe")
  private String surname;
  @Schema(example = "mr")
  private String nameTitle;
  @Schema(example = "Primary")
  private String nameType;
}
