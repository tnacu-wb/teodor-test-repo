package uk.co.whitbread.cdh.domain.model.report.out;

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

  @JsonProperty("FirstName")
  private String firstName;

  @JsonProperty("LastName")
  private String lastName;

  @JsonProperty("TelephoneNumber")
  private String telephoneNumber;

  @JsonProperty("EmailAddress")
  private String emailAddress;

  @JsonProperty("CompanyName")
  private String companyName;

  @JsonProperty("Mobile")
  private String mobile;


}
