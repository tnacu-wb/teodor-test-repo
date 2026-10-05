package uk.co.whitbread.cdh.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.domain.model.account.in.EmployeeSearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.in.GetEmployeeRequest;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeeResponse;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeesResponse;
import uk.co.whitbread.cdh.domain.ports.primary.EmployeeInPort;
import uk.co.whitbread.cdh.domain.ports.secondary.EmployeeOutPort;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmployeeInPortImpl implements EmployeeInPort {

  private final EmployeeOutPort employeeOutPort;

  @Override
  public GetEmployeeResponse getEmployee(GetEmployeeRequest getEmployeeRequest) {
    return this.employeeOutPort.getEmployee(getEmployeeRequest);
  }

  @Override
  public GetEmployeesResponse getEmployees(EmployeeSearchCriteria employeeSearchCriteria) {
    return this.employeeOutPort.getEmployees(employeeSearchCriteria);
  }
  
  @Override
  public GetEmployeesResponse getCompanyEmployees(String companyAccountId, String accessedBy, Integer pageSize,
                                                  String pageToken, String accessContext, Boolean awaitingApproval) {
    return this.employeeOutPort.getCompanyEmployees(companyAccountId, accessedBy, pageSize, pageToken, accessContext,
        awaitingApproval);
  }
}
