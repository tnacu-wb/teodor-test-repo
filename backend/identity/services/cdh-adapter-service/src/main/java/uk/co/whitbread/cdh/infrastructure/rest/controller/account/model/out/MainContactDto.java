package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainContactDto {
  private String employeeAccountId;
  private String companyAccountId;
  private Integer bartEmployeeId;
  private String globalCompanyId;
  private String bartGuestHistoryNumber;
  private String bartGuestHistoryCreation;
  private String title;
  private String firstName;
  private String lastName;
  private String emailAddress;
  private String phoneNumber;
  private String mobileNumber;
  private String carRegistration;
  private boolean textConfirmation;
  private AddressDto address;
  private Integer centralCardId;
  private boolean lockedForEditing;
  private String accessLevel;
  private String status;
  private boolean awaitingApproval;
  private String position;
  private BusinessDto business;
  private BookingPreferenceDto bookingPreference;
  private String approxAnnualUKHotelSpend;
  private PaymentPreferenceDto paymentPreference;
  private EmployeeAnswersDto employeeAnswers;
  private String activationKey;
  private boolean mainEmployee;
}
