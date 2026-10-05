package uk.co.whitbread.marketing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCountryOfResidence {

  private String customerId;
  private String countryOfResidence;
}
