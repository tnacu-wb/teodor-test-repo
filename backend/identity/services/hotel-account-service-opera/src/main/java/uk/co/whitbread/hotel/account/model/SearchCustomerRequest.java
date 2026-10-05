package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchCustomerRequest {

  private String firstName;
  private String lastName;
  private String email;
  private String companyName;
  private String addressLine;
  private String postCode;
  private String mobile;
  private String telephone;
}
