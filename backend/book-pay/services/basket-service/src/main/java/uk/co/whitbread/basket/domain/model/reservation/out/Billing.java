package uk.co.whitbread.basket.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Billing {
  private Address address;
  private String email;
  private String firstName;
  private String lastName;
  private String telephone;
  private String title;
}

