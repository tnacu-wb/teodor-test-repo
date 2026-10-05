package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booker {

  @JsonProperty("Title")
  private String title;

  @JsonProperty("FirstName")
  private String firstName;

  @JsonProperty("LastName")
  private String lastName;

  @JsonProperty("EmailAddress")
  private String emailAddress;

  @JsonProperty("TelephoneNumber")
  private String telephoneNumber;

  @JsonProperty("MobileNumber")
  private String mobileNumber;

  @JsonProperty("CompanyName")
  private String companyName;

  @JsonProperty("Address")
  private Address address;
}
