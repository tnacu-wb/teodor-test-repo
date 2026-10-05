package uk.co.whitbread.employee.bulk.model.companyEmployees;

import lombok.Data;

@Data
public class GetEmployeeResponse {
  
  private String accessLevel;
  private String activationKey;
  private Address address;
  private String approxAnnualUKHotelSpend;
  private Boolean awaitingApproval;
  private String bartEmployeeId;
  private String bartGuestHistoryCreation;
  private String bartGuestHistoryNumber;
  private Business business;
  private String carRegistration;
  private String centralCardId;
  private String centralCardIdString;
  private String companyAccountId;
  private String emailAddress;
  private String employeeAccountId;
  private EmployeeAnswers employeeAnswers;
  private String employeeStatus;
  private String firstName;
  private String globalCompanyId;
  private String lastName;
  private String latestKeyCreatedDateTime;
  private Boolean lockedForEditing;
  private Boolean mainEmployee;
  private String mobileNumber;
  private PaymentPreference paymentPreference;
  private String phoneNumber;
  private String position;
  private Boolean textConfirmation;
  private String title;
}
