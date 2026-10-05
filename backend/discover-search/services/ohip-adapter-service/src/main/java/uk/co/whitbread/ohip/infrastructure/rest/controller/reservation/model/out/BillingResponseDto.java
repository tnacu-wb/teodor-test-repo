package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillingResponseDto {

  private AddressResponseDto address;
  private String email;
  private String firstName;
  private String lastName;
  private String telephone;
  private String landline;
  private String title;
}
