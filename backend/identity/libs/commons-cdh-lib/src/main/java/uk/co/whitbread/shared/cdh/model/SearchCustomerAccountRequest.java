package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchCustomerAccountRequest {

  private String firstName;
  private String lastName;
  private String email;
  private String companyName;
  private String addressLine;
  private String postCode;
  private String mobile;
  private String telephone;
}
