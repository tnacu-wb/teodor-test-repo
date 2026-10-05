package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProfileTypeDto {
  @Schema
  private CustomerTypeDto customer;
  @Schema
  private ProfileTypeEmailsDto emails;
  @Schema
  private ProfileTypeAddressesDto addresses;
}
