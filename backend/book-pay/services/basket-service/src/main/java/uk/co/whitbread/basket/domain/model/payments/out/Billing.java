package uk.co.whitbread.basket.domain.model.payments.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Billing {

  private String title;
  private String firstName;
  private String lastName;
  private String email;
  private String telephone;
  private Address address;
}
