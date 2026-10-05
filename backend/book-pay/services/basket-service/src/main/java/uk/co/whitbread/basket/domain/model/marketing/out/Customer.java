package uk.co.whitbread.basket.domain.model.marketing.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

  private String title;
  private String firstName;
  private String lastName;
  private String countryOfResidence;
  private String language;

}
