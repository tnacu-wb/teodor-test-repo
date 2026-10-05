package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.AddressDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.BookingPreferenceDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.BusinessDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.EmployeeAnswersDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.PaymentPreferenceDto;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetEmployeeResponseDto {

  private boolean mainEmployee;
  private String employeeAccountId;
  private String companyAccountId;
  private String bartEmployeeId;
  private String globalCompanyId;
  private String bartGuestHistoryCreation;
  private String bartGuestHistoryNumber;
  private String activationKey;
  private String title;
  private String firstName;
  private String lastName;
  private String emailAddress;
  private String phoneNumber;
  private String mobileNumber;
  private String carRegistration;
  private boolean textConfirmation;
  private AddressDto address;
  private String centralCardId;
  private String centralCardIdString;
  private boolean lockedForEditing;
  private String accessLevel;
  private String employeeStatus;
  private boolean awaitingApproval;
  private String position;
  private BusinessDto business;
  private BookingPreferenceDto bookingPreference;
  private PaymentPreferenceDto paymentPreference;
  private String approxAnnualUKHotelSpend;
  private String latestKeyCreatedDateTime;
  private EmployeeAnswersDto employeeAnswers;
}