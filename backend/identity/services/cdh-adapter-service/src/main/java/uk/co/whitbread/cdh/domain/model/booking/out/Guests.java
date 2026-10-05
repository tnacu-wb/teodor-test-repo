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
public class Guests {

  @JsonProperty("LeadGuest")
  private boolean leadGuest;

  @JsonProperty("Title")
  private String title;

  @JsonProperty("Initials")
  private String initials;

  @JsonProperty("FirstName")
  private String firstName;

  @JsonProperty("LastName")
  private String lastName;

  @JsonProperty("BartGuestHistoryNumber")
  private String bartGuestHistoryNumber;

  @JsonProperty("EmployeeAccountId")
  private String employeeAccountId;

  @JsonProperty("GlobalCompanyId")
  private String globalCompanyId;

  @JsonProperty("BartEmployeeId")
  private String bartEmployeeId;

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

  @JsonProperty("Nationality")
  private String nationality;

  @JsonProperty("Passport")
  private Passport passport;
}
