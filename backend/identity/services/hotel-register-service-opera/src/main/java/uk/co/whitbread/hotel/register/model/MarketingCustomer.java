package uk.co.whitbread.hotel.register.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingCustomer {
  private String title;
  private String firstName;
  private String lastName;
  private String nationality;
  private String userId;
  private String countryOfResidence;
  private String customerId;
  private String language;
}
