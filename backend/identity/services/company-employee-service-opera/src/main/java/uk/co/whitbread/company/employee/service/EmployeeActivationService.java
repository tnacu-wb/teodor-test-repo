package uk.co.whitbread.company.employee.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.Employee;
import uk.co.whitbread.company.employee.model.GetEmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.InnBusinessEmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.feature.FeatureFlag;
import uk.co.whitbread.company.employee.model.feature.UnleashWrapper;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeActivationService {

  private static final String DUMMY_EMAIL = "dummy@email.com";
  private final EmployeeDataService employeeDataService;
  private final CompanyDataService companyDataService;
  private final EmployeeMapper employeeMapper;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public InnBusinessEmployeeActivationResponse getInnBusinessEmployeeActivationResponse(String activationKey) {

    var employeeActivationResponse = getEmployeeActivationDetails(activationKey);
    var innBusinessEmployeeActivationResponse = employeeMapper.toInnBusinessEmployeeActivationResponse(employeeActivationResponse);

    var cdhCompany = companyDataService.getCompany(employeeActivationResponse.getCompanyId(), DUMMY_EMAIL);
    cdhCompany.ifPresent(company -> innBusinessEmployeeActivationResponse.setCompanyName(company.getCompanyName()));

    return innBusinessEmployeeActivationResponse;
  }

  public GetEmployeeActivationResponse getEmployeeActivationDetails(String activationKey) {
    log.debug("Called EmployeeActivationService.getEmployeeActivationDetails");

    var employeesQueryParams = GetEmployeesQueryParams.builder().activationKey(activationKey)
        .build();
    final Optional<GetEmployeesResponse> employees =
        unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
            ? employeeDataService.getEmployeesV2(employeesQueryParams, DUMMY_EMAIL)
            : employeeDataService.getEmployees(employeesQueryParams, DUMMY_EMAIL);
    if (employees.isEmpty() || employees.get().getResults() == null || employees.get().getResults().isEmpty()) {
      throw new EmployeeNotFoundException(
          String.format("Employee with activation key %s was not found", activationKey));
    }
    return employeeMapper.toGetEmployeeActivationResponse(employees.get().getResults().get(0));
  }

  public Employee activateTravelManagerInCdh(String activationKey) {
    var queryParams = GetEmployeesQueryParams.builder().activationKey(activationKey).build();
    Optional<GetEmployeesResponse> employees =
        unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
            ? employeeDataService.getEmployeesV2(queryParams, DUMMY_EMAIL)
            : employeeDataService.getEmployees(queryParams, DUMMY_EMAIL);
    if (employees.isEmpty() || employees.get().getResults() == null || employees.get().getResults().isEmpty()) {
      throw new EmployeeNotFoundException(
          String.format("Activation key %s is not valid", activationKey));
    }
    var employee = employees.get().getResults().get(0);
    Optional<GetEmployeeResponse> getEmployeeResponse = employeeDataService.activateEmployee(
        employee.getEmployeeAccountId(), activationKey, employee.getEmailAddress());
    if (getEmployeeResponse.isPresent()) {
      return employeeMapper.toEmployee(getEmployeeResponse.get());
    }
    throw new EmployeeNotFoundException(
        String.format("Activation key %s is not valid", activationKey));
  }
}
