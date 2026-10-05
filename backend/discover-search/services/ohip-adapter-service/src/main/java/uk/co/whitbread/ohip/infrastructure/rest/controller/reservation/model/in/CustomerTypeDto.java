package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
public class CustomerTypeDto {
  @Schema
  private List<PersonNameTypeDto> personName;
}
