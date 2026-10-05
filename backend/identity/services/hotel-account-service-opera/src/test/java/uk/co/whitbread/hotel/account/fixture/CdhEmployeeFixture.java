package uk.co.whitbread.hotel.account.fixture;


import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

public class CdhEmployeeFixture {

  public static GetEmployeeResponse createGetEmployeeResponse(String companyAccountId,
      String employeeAccountId, String email) {
    return GetEmployeeResponse.builder()
        .companyAccountId(companyAccountId)
        .employeeAccountId(employeeAccountId)
        .emailAddress(email)
        .build();
  }

  public static EmployeeAccountRequest createEmployeeAccountRequest(String email) {
    return EmployeeAccountRequest.builder()
        .emailAddress(email)
        .build();
  }

  public static EmployeeAccountResponse createEmployeeAccountResponse(String employeeAccountId) {
    return EmployeeAccountResponse.builder()
        .employeeAccountId(employeeAccountId)
        .build();
  }
}
