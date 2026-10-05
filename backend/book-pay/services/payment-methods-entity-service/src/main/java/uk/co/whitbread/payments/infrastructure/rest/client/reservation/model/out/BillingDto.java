package uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingDto {
  private AddressDto address;
  private String email;
  private String firstName;
  private String lastName;
  private String telephone;
  private String title;
}