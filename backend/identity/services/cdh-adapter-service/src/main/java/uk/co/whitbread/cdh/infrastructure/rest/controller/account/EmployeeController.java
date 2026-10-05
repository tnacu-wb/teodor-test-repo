package uk.co.whitbread.cdh.infrastructure.rest.controller.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.cdh.domain.model.account.in.GetEmployeeRequest;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeeResponse;
import uk.co.whitbread.cdh.domain.ports.primary.EmployeeInPort;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.mapper.EmployeeMapper;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.EmployeeSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.GetEmployeeRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.employee.GetEmployeeResponseDto;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out.employee.GetEmployeesResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/cdh")
public class EmployeeController implements EmployeeControllerApiDocumentation {

  private final EmployeeMapper employeeMapper;
  private final EmployeeInPort employeeInPort;

  @GetMapping(value = "/account/employee", produces = MediaType.APPLICATION_JSON_VALUE)
  public GetEmployeeResponseDto getEmployee(@Validated GetEmployeeRequestDto getEmployeeRequestDto) {

    GetEmployeeRequest getEmployeeRequest = employeeMapper.toModel(getEmployeeRequestDto);
    GetEmployeeResponse employee = employeeInPort.getEmployee(getEmployeeRequest);
    return employeeMapper.toDto(employee);
  }

  @GetMapping(value = "/account/employees", produces = MediaType.APPLICATION_JSON_VALUE)
  public GetEmployeesResponseDto getEmployees(
      @Validated EmployeeSearchCriteriaDto employeeSearchCriteriaDto) {
    var employeeSearchCriteria = employeeMapper.toModel(employeeSearchCriteriaDto);
    var response = employeeInPort.getEmployees(employeeSearchCriteria);
    return employeeMapper.toDto(response);
  }
  
  @GetMapping(value = "/account/employees/company/{companyAccountId}")
  public GetEmployeesResponseDto getCompanyEmployees(
      @PathVariable String companyAccountId, @RequestParam String accessedBy,
      @RequestParam Integer pageSize, @RequestParam(name = "pageToken", required = false) String pageToken,
      @RequestParam(name = "accessContext", required = false) String accessContext,
      @RequestParam(name = "awaitingApproval", required = false) Boolean awaitingApproval) {
    
    var response = employeeInPort.getCompanyEmployees(companyAccountId, accessedBy, pageSize, pageToken, accessContext,
        awaitingApproval);
    return employeeMapper.toDto(response);
  }
  
}
