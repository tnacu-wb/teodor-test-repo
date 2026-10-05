package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class SearchCustomerAccountV3Request {

  @JsonProperty("FirstName")
  private String firstName;

  @JsonProperty("LastName")
  private String lastName;

  @JsonProperty("Email")
  private String email;

  @JsonProperty("CompanyName")
  private String companyName;

  @JsonProperty("AddressLine1")
  private String addressLine;

  @JsonProperty("PostCode")
  private String postCode;

  @JsonProperty("Mobile")
  private String mobile;

  @JsonProperty("Telephone")
  private String telephone;
}
