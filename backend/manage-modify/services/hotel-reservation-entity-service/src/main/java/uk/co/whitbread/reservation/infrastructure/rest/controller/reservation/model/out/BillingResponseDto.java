package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillingResponseDto {

  private AddressResponseDto address;
  private String email;
  private String firstName;
  private String lastName;
  private String telephone;
  private String landline;
  private String title;

}
