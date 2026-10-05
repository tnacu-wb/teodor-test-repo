package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import uk.co.whitbread.ohip.domain.model.reservation.in.AddressType;

@Data
public class AddressInfoTypeDto {

  @Schema
  private AddressType address;

}
