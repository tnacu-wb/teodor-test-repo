package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmailTypeDto {
  @Schema(example = "john.doe@wb.com")
  private String emailAddress;
}
