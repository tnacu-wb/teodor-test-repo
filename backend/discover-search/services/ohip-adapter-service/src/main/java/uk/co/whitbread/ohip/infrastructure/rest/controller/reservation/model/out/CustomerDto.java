package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerDto {

  private String title;
  private String firstName;
  private String lastName;
  private String country;
  private String language;

}
