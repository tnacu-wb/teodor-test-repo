package uk.co.whitbread.cdh.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.account.in.EmployeeSearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.in.GetEmployeeRequest;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeeResponse;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeesResponse;
import uk.co.whitbread.cdh.domain.ports.secondary.EmployeeOutPort;

@ExtendWith(MockitoExtension.class)
class EmployeeInPortImplTest {
  private static final String EMAIL = "jhon@yopmail.com";
  @InjectMocks
  private EmployeeInPortImpl employeeInPortImpl;

  @Mock
  private EmployeeOutPort employeeOutPort;

  @Test
  void getEmployee_success() {
    GetEmployeeResponse response = GetEmployeeResponse.builder().emailAddress(EMAIL)
        .build();
    when(employeeOutPort.getEmployee(any())).thenReturn(response);
    GetEmployeeResponse employee = employeeInPortImpl.getEmployee(
        GetEmployeeRequest.builder().build());
    assertThat(employee, Matchers.is(response));
  }

  @Test
  void getEmployees_success() {
    var response = GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
        .emailAddress(EMAIL)
        .build())).build();
    when(employeeOutPort.getEmployees(any())).thenReturn(response);
    GetEmployeesResponse employees = employeeInPortImpl.getEmployees(
        EmployeeSearchCriteria.builder().build());
    assertThat(employees, Matchers.is(response));
  }
  
  @Test
  void getECompanyEmployees_success() {
    var response = GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
        .emailAddress(EMAIL)
        .build())).build();
    when(employeeOutPort.getCompanyEmployees(any(), any(), anyInt(), any(), any(), anyBoolean()))
        .thenReturn(response);
    GetEmployeesResponse employees = employeeInPortImpl.getCompanyEmployees("testCompanyAccountId",
        EMAIL, 200, "testToken", "PI", false);
    assertThat(employees, Matchers.is(response));
  }
}