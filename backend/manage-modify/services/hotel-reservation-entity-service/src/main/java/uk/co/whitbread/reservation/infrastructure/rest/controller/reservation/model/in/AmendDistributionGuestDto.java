package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AmendDistributionGuestDto {

  private String givenName;
  private String surName;
  private String nameTitle;
  private String email;
}
