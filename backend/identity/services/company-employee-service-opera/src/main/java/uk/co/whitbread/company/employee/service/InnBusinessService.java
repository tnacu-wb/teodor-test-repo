package uk.co.whitbread.company.employee.service;

import static uk.co.whitbread.company.employee.service.ServiceUtils.sendAsyncRequestToJoinAccepted;
import static uk.co.whitbread.company.employee.service.ServiceUtils.sendRequestToJoinRejectedEmail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.employee.exceptions.CompanyNotFoundException;
import uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.employee.exceptions.InvalidOperationException;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.AccessLevel;
import uk.co.whitbread.company.employee.model.feature.FeatureFlag;
import uk.co.whitbread.company.employee.model.feature.UnleashWrapper;
import uk.co.whitbread.company.employee.model.innbusiness.ApproveRejectRequest;
import uk.co.whitbread.company.employee.model.innbusiness.SendActivationRequest;
import uk.co.whitbread.company.employee.properties.EmailProperties;
import uk.co.whitbread.company.employee.validation.InnbEmployeeValidator;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.azureemail.model.CompanyActivation;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class InnBusinessService {

  private static final String INACTIVE_STATUS = "INACTIVE";
  private static final String LANGUAGE_EN = "en";

  private final AzureEmailService azureEmailService;
  private final EmployeeDataService employeeDataService;
  private final CompanyDataService companyDataService;
  private final EmailProperties emailProperties;
  private final TokenService authTokenService;
  private final EmployeeMapper employeeMapper;
  private final InnbEmployeeValidator approveRejectValidator;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public void approveRejectEmployee(ApproveRejectRequest approveRejectRequest, String authorization) {
    var tokenClaims = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
    approveRejectValidator.validateTokenClaims(tokenClaims);
    var manager = getCdhEmployeeByEmail(tokenClaims.getUserEmail());
    approveRejectValidator.validateManagerAccessLevel(manager);
    var cdhEmployee = getCdhEmployeeByEmail(approveRejectRequest.getEmail());
    approveRejectValidator.validateEmployeeAccessLevelsBasedOnManagerAccessLevel(
          manager.getAccessLevel(), approveRejectRequest.getAccessLevel());
    approveRejectValidator.validateEmployeeSameCompanyWithManager(manager, cdhEmployee);
    approveRejectValidator.validateUserAndManagerStatus(manager, cdhEmployee);

    if (approveRejectRequest.getApproved()) {
      log.info("Approving the employee (Id = {}, CompanyId = {}) to join Travel Manager's (Id = {})"
            + " company (Id = {}).", cdhEmployee.getEmployeeAccountId(), cdhEmployee.getCompanyAccountId(),
            manager.getEmployeeAccountId(), manager.getCompanyAccountId());
      EmployeeAccountRequest employeeAccountRequest = employeeMapper.toEmployeeAccountRequest(cdhEmployee);
      employeeAccountRequest.setAccessLevel(approveRejectRequest.getAccessLevel().toString());
      employeeAccountRequest.setAwaitingApproval(false);
      employeeDataService
            .updateEmployeeAccount(cdhEmployee.getCompanyAccountId(), cdhEmployee.getEmployeeAccountId(),
                  employeeAccountRequest, manager.getEmailAddress());
      var employeeActivationUrl = approveRejectRequest.getLanguage().toLowerCase().contains(LANGUAGE_EN)
            ? emailProperties.getInnbEmployeeActivationUrl() + cdhEmployee.getActivationKey()
            : emailProperties.getInnbEmployeeActivationUrlDe() + cdhEmployee.getActivationKey();
      sendAsyncRequestToJoinAccepted(companyDataService, azureEmailService, cdhEmployee,
            approveRejectRequest.getLanguage(), employeeActivationUrl);
    } else {
      log.info("Rejecting the employee (Id = {}, CompanyId = {}) to join Manager's (Id = {})"
                  + " company (Id = {}).", cdhEmployee.getEmployeeAccountId(), cdhEmployee.getCompanyAccountId(),
            manager.getEmployeeAccountId(), manager.getCompanyAccountId());
      employeeDataService
            .deleteEmployeeAccount(cdhEmployee.getCompanyAccountId(), cdhEmployee.getEmployeeAccountId(),
                  manager.getEmailAddress());
      sendRequestToJoinRejectedEmail(companyDataService, azureEmailService, cdhEmployee, tokenClaims,
            approveRejectRequest.getLanguage());
    }
  }

  public void sendActivationEmail(SendActivationRequest sendActivationRequest) {
    var employee = getCdhEmployeeByEmail(sendActivationRequest.getEmail());
    validateEmployeeFieldsForResendActivationEmail(employee);
    var company = getCompanyFromCdh(sendActivationRequest);
    var hasSuperAccess = employee.getAccessLevel().equalsIgnoreCase(AccessLevel.SUPER.toString());

    if (!employee.getCompanyAccountId().equals(company.getCompanyAccountId())) {
      String sanitizedCompanyName = sendActivationRequest.getCompanyName()
            .replace("\n", "").replace("\r", "");
      log.error("Employee with id = {}, is not found in company with id = {}, companyName = {}.",
            employee.getEmployeeAccountId(), company.getCompanyAccountId(),
            sanitizedCompanyName);
      throw new EmployeeNotFoundException(String.format("Employee is not found in company %s.",
            sendActivationRequest.getCompanyName()));
    }

    if (employee.getEmployeeStatus().equalsIgnoreCase(INACTIVE_STATUS)
          && hasSuperAccess && company.getStatus().equalsIgnoreCase(INACTIVE_STATUS)) {
      var companyActivationUrl = sendActivationRequest.getLanguage().toLowerCase().contains(LANGUAGE_EN)
            ? emailProperties.getInnbCompanyActivationUrl()
            : emailProperties.getInnbCompanyActivationUrlDe();
      azureEmailService.sendBBCompanyActivationEmail(CompanyActivation.builder()
            .language(sendActivationRequest.getLanguage())
            .companyName(sendActivationRequest.getCompanyName())
            .email(sendActivationRequest.getEmail())
            .activationLink(companyActivationUrl + employee.getActivationKey())
            .build());
    } else {
      log.error("Employee (Id = {}, Status = {}, AccessLevel = {}) or company (Id = {}, Status = {})"
                  + " is not in the correct state.", employee.getEmployeeAccountId(),
            employee.getEmployeeStatus(), employee.getAccessLevel(), company.getCompanyAccountId(),
            company.getStatus());
      throw new InvalidOperationException(String.format("Employee or company (Name = %s) is not in"
            + " the correct state.", company.getCompanyName()));
    }
  }

  private GetCompanyResponse getCompanyFromCdh(SendActivationRequest sendActivationRequest) {
    return companyDataService
          .getCompanies(GetCompaniesQueryParams.builder()
                .companyName(sendActivationRequest.getCompanyName())
                .build(), sendActivationRequest.getEmail())
          .filter(p -> p.getResults() != null)
          .flatMap(p -> p.getResults().stream().findFirst())
          .filter(p -> StringUtils.isNotBlank(p.getStatus())
                && StringUtils.isNotBlank(p.getCompanyAccountId()))
          .orElseThrow(() -> {
            log.error("Cannot find company (Name = {}).", sendActivationRequest.getCompanyName());
            return new CompanyNotFoundException(String.format("Cannot find company (Name=%s).",
                  sendActivationRequest.getCompanyName()));
          });
  }

  private void validateEmployeeFieldsForResendActivationEmail(GetEmployeeResponse cdhEmployee) {
    if (StringUtils.isBlank(cdhEmployee.getAccessLevel())
          || StringUtils.isBlank(cdhEmployee.getCompanyAccountId())
          || StringUtils.isBlank(cdhEmployee.getEmployeeAccountId())
          || StringUtils.isBlank(cdhEmployee.getEmployeeStatus())
          || StringUtils.isBlank(cdhEmployee.getActivationKey())) {
      log.error("Employee (Id = {}, CompanyAccountId = {}, Status = {}) found in CDH but it has "
            + "empty mandatory fields.", cdhEmployee.getEmployeeAccountId(),
            cdhEmployee.getCompanyAccountId(), cdhEmployee.getEmployeeStatus());
      throw new InvalidOperationException("Employee found in CDH but it has empty mandatory fields.");
    }
  }

  private GetEmployeeResponse getCdhEmployeeByEmail(String email) {
    GetEmployeesQueryParams employeesQueryParams = GetEmployeesQueryParams.builder()
        .emailAddress(email).build();
    var employees = unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation())
        ? employeeDataService.getEmployeesV2(employeesQueryParams, email)
        : employeeDataService.getEmployees(employeesQueryParams, email);
    return employees
          .filter(p -> p.getResults() != null)
          .flatMap(p -> p.getResults().stream().findFirst())
          .orElseThrow(() -> {
            log.error("Cannot find employee by email.");
            return new EmployeeNotFoundException("Cannot find employee by email.");
          });
  }
}
