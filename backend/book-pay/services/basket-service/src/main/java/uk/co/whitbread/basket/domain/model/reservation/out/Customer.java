package uk.co.whitbread.basket.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class Customer {

  private String title;
  private String firstName;
  private String lastName;
  private String country;
  private String language;

}
