package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class A2cDetailsDto {

  private boolean display;
  private String number;
  private String name;
  private String address;
  private String postcode;
}
