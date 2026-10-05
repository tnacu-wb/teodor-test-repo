package uk.co.whitbread.ohip.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingResponse {

  private AddressResponse address;
  private String email;
  private String firstName;
  private String lastName;
  private String telephone;
  private String landline;
  private String title;

}