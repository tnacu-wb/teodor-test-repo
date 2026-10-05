package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public class EmployeeAccountRequest {

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

  @JsonProperty("FirstName")
  private String firstName;

  @JsonProperty("LastName")
  private String lastName;

  @JsonProperty("EmailAddress")
  private String emailAddress;

  @JsonProperty("PhoneNumber")
  private String phoneNumber;

  @JsonProperty("MobileNumber")
  private String mobileNumber;

  @JsonProperty("CarRegistration")
  private String carRegistration;

  @JsonProperty("TextConfirmation")
  private boolean textConfirmation;

  @JsonProperty("Address")
  private BusinessAddress address;

  @JsonProperty("Status")
  private String employeeStatus;

  @JsonProperty("CentralCardId")
  private String centralCardId;

  @JsonProperty("CentralCardIdString")
  private String centralCardIdString;

  @JsonProperty("LockedForEditing")
  private boolean lockedForEditing;

  @JsonProperty("AccessLevel")
  private String accessLevel;

  @JsonProperty("AwaitingApproval")
  private boolean awaitingApproval;

  @JsonProperty("Position")
  private String position;

  @JsonProperty("Business")
  private Business business;

  @JsonProperty("BookingPreference")
  private BusinessBookingPreference bookingPreference;

  @JsonProperty("PaymentPreference")
  private BusinessPaymentPreference paymentPreference;

  @JsonProperty("ApproxAnnualUKHotelSpend")
  private String approxAnnualUKHotelSpend;

  @JsonProperty("EmployeeAnswers")
  private EmployeeAnswers employeeAnswers;
}
