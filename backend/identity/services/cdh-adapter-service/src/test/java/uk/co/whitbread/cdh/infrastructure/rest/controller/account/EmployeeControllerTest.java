package uk.co.whitbread.cdh.infrastructure.rest.controller.account;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.account.in.EmployeeSearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeeResponse;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeesResponse;
import uk.co.whitbread.cdh.domain.ports.primary.EmployeeInPort;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper.EmployeeMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.EmployeeSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.employee.GetEmployeeResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.employee.GetEmployeesResponseDto;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {
  private static final String EMAIL = "jhon@yopmail.com";

  @InjectMocks
  EmployeeController employeeController;

  @Mock
  EmployeeMapper employeeMapper;

  @Mock
  EmployeeInPort employeeInPort;


  @Test
  void test_getEmployees() {
    var employeeSearchCriteriaDto = new EmployeeSearchCriteriaDto();
    when(employeeMapper.toModel(any(EmployeeSearchCriteriaDto.class)))
        .thenReturn(createEmployeeSearchCriteria());
    when(employeeInPort.getEmployees(any(EmployeeSearchCriteria.class)))
        .thenReturn(createGetEmployeesResponse());
    when(employeeMapper.toDto(any(GetEmployeesResponse.class)))
        .thenReturn(GetEmployeesResponseDto.builder()
            .totalEmployeesInCompany(1)
            .continuationToken("token")
            .results(List.of(GetEmployeeResponseDto.builder()
                .emailAddress(EMAIL)
                .build()))
            .build());

    var response = employeeController.getEmployees(employeeSearchCriteriaDto);
    assertNotNull(response);

  }
  
  @Test
  void test_getCompanyEmployees() {
    
    when((employeeInPort.getCompanyEmployees(any(), any(), anyInt(), any(), any(), anyBoolean())))
        .thenReturn(createGetEmployeesResponse());
    when(employeeMapper.toDto(any(GetEmployeesResponse.class)))
        .thenReturn(GetEmployeesResponseDto.builder()
            .totalEmployeesInCompany(1)
            .continuationToken("token")
            .results(List.of(GetEmployeeResponseDto.builder()
                .emailAddress(EMAIL)
                .build()))
            .build());
    var response = employeeController.getCompanyEmployees("testCompanyAccountId", EMAIL,
        200, "testToken", "PI", false);
    assertNotNull(response);
  }

  private GetEmployeesResponse createGetEmployeesResponse() {
    return GetEmployeesResponse.builder()
        .totalEmployeesInCompany(1)
        .continuationToken("token")
        .results(List.of(GetEmployeeResponse.builder()
            .emailAddress(EMAIL)
            .build()))
        .build();
  }

  private EmployeeSearchCriteria createEmployeeSearchCriteria() {
    return EmployeeSearchCriteria.builder()
        .emailAddress(EMAIL)
        .build();
  }
}