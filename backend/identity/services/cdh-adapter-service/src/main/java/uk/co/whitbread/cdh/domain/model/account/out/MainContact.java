package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainContact {
  @JsonProperty("EmployeeAccountId")
  private String employeeAccountId;
  @JsonProperty("CompanyAccountId")
  private String companyAccountId;
  @JsonProperty("BartEmployeeId")
  private Integer bartEmployeeId;
  @JsonProperty("GlobalCompanyId")
  private String globalCompanyId;
  @JsonProperty("BartGuestHistoryNumber")
  private String bartGuestHistoryNumber;
  @JsonProperty("BartGuestHistoryCreation")
  private String bartGuestHistoryCreation;
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
  private Address address;
  @JsonProperty("CentralCardId")
  private Integer centralCardId;
  @JsonProperty("LockedForEditing")
  private boolean lockedForEditing;
  @JsonProperty("AccessLevel")
  private String accessLevel;
  @JsonProperty("Status")
  private String status;
  @JsonProperty("AwaitingApproval")
  private boolean awaitingApproval;
  @JsonProperty("Position")
  private String position;
  @JsonProperty("Business")
  private Business business;
  @JsonProperty("BookingPreference")
  private BookingPreference bookingPreference;
  @JsonProperty("ApproxAnnualUKHotelSpend")
  private String approxAnnualUKHotelSpend;
  @JsonProperty("PaymentPreference")
  private PaymentPreference paymentPreference;
  @JsonProperty("EmployeeAnswers")
  private EmployeeAnswers employeeAnswers;
  @JsonProperty("ActivationKey")
  private String activationKey;
  @JsonProperty("MainEmployee")
  private boolean mainEmployee;
}
