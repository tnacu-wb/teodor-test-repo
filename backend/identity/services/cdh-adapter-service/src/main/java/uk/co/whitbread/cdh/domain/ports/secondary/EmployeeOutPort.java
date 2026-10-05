package uk.co.whitbread.cdh.domain.ports.secondary;

import uk.co.whitbread.cdh.domain.model.account.in.EmployeeSearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.in.GetEmployeeRequest;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeeResponse;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeesResponse;

public interface EmployeeOutPort {

  GetEmployeeResponse getEmployee(GetEmployeeRequest getEmployeeRequest);

  GetEmployeesResponse getEmployees(EmployeeSearchCriteria employeeSearchCriteria);
  
  GetEmployeesResponse getCompanyEmployees(String companyAccountId, String accessedBy, Integer pageSize,
                                           String pageToken, String accessContext, Boolean awaitingApproval);
}
