package uk.co.whitbread.cdh.domain.model.account.out.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.cdh.domain.model.account.out.Address;
import uk.co.whitbread.cdh.domain.model.account.out.BookingPreference;
import uk.co.whitbread.cdh.domain.model.account.out.Business;
import uk.co.whitbread.cdh.domain.model.account.out.EmployeeAnswers;
import uk.co.whitbread.cdh.domain.model.account.out.PaymentPreference;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class GetEmployeeResponse {

  @JsonProperty("MainEmployee")
  private boolean mainEmployee;

  @JsonProperty("EmployeeAccountId")
  private String employeeAccountId;

  @JsonProperty("CompanyAccountId")
  private String companyAccountId;

  @JsonProperty("BartEmployeeId")
  private String bartEmployeeId;

  @JsonProperty("GlobalCompanyId")
  private String globalCompanyId;

  @JsonProperty("BartGuestHistoryCreation")
  private String bartGuestHistoryCreation;

  @JsonProperty("BartGuestHistoryNumber")
  private String bartGuestHistoryNumber;

  @JsonProperty("ActivationKey")
  private String activationKey;

  @JsonProperty("Title")
  private String title;

  @JsonProperty("LastName")
  private String lastName;

  @JsonProperty("FirstName")
  private String firstName;

  @JsonProperty("PhoneNumber")
  private String phoneNumber;

  @JsonProperty("MobileNumber")
  private String mobileNumber;

  @JsonProperty("EmailAddress")
  private String emailAddress;

  @JsonProperty("TextConfirmation")
  private boolean textConfirmation;

  @JsonProperty("CarRegistration")
  private String carRegistration;

  @JsonProperty("Address")
  private Address address;

  @JsonProperty("CentralCardIdString")
  private String centralCardIdString;

  @JsonProperty("CentralCardId")
  private String centralCardId;

  @JsonProperty("LockedForEditing")
  private boolean lockedForEditing;

  @JsonProperty("AccessLevel")
  private String accessLevel;

  @JsonProperty("Status")
  private String employeeStatus;

  @JsonProperty("AwaitingApproval")
  private boolean awaitingApproval;

  @JsonProperty("Position")
  private String position;

  @JsonProperty("Business")
  private Business business;

  @JsonProperty("BookingPreference")
  private BookingPreference bookingPreference;

  @JsonProperty("PaymentPreference")
  private PaymentPreference paymentPreference;

  @JsonProperty("ApproxAnnualUKHotelSpend")
  private String approxAnnualUKHotelSpend;

  @JsonProperty("LatestKeyCreatedDateTime")
  private String latestKeyCreatedDateTime;

  @JsonProperty("EmployeeAnswers")
  private EmployeeAnswers employeeAnswers;
}