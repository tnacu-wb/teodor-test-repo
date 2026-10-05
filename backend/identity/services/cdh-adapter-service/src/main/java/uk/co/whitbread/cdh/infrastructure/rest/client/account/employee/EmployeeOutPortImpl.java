package uk.co.whitbread.cdh.infrastructure.rest.client.account.employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.domain.model.account.in.EmployeeSearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.in.GetEmployeeRequest;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeeResponse;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeesResponse;
import uk.co.whitbread.cdh.domain.model.feature.FeatureFlag;
import uk.co.whitbread.cdh.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.cdh.domain.ports.secondary.EmployeeOutPort;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.mapper.CdhEmployeeMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeOutPortImpl implements EmployeeOutPort {

  private final EmployeeClient employeeClient;

  private final CdhEmployeeMapper cdhEmployeeMapper;

  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  @Override
  public GetEmployeeResponse getEmployee(GetEmployeeRequest getEmployeeRequest) {
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())) {
      var employeeRequestDto = cdhEmployeeMapper.toDto(getEmployeeRequest);
      return employeeClient.getEmployeeV2(employeeRequestDto);
    }
    return employeeClient.getEmployee(getEmployeeRequest);
  }

  @Override
  public GetEmployeesResponse getEmployees(EmployeeSearchCriteria employeeSearchCriteria) {
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())) {
      var employeeSearchCriteriaDto = cdhEmployeeMapper.toDto(employeeSearchCriteria);
      return employeeClient.getEmployeesV2(employeeSearchCriteriaDto, employeeSearchCriteria.getAccessedBy(),
          employeeSearchCriteria.getAccessContext());
    }
    return employeeClient.getEmployees(employeeSearchCriteria);
  }
  
  @Override
  public GetEmployeesResponse getCompanyEmployees(String companyAccountId, String accessedBy, Integer pageSize,
                                                  String pageToken, String accessContext, Boolean awaitingApproval) {
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())) {
      var companyEmployeeSearchCriteriaDto = cdhEmployeeMapper.toCompanyEmployeeDto(pageSize,
                                                          pageToken, awaitingApproval);
      return employeeClient.getCompanyEmployeesV2(companyEmployeeSearchCriteriaDto, companyAccountId,
                                                          accessedBy, accessContext);
    }
    return employeeClient.getCompanyEmployees(companyAccountId, accessedBy, pageSize, pageToken, accessContext,
        awaitingApproval);
  }
}
