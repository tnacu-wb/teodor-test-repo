package uk.co.whitbread.hotel.register.service;

import static uk.co.whitbread.hotel.register.utils.register.cdh.CompanyUtils.buildGetCompaniesQueryParams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.register.exceptions.CdhServiceException;
import uk.co.whitbread.hotel.register.mapper.CompanyMapper;
import uk.co.whitbread.hotel.register.mapper.EmployeeMapper;
import uk.co.whitbread.hotel.register.model.CompanyType;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.CustomerResponse;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneResponse;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoResponse;
import uk.co.whitbread.hotel.register.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.register.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.register.service.auth0.Auth0InnBusinessService;
import uk.co.whitbread.hotel.register.service.auth0.Auth0Service;
import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import uk.co.whitbread.shared.auth.exception.AuthServiceException;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.CreateCompanyResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesResponse;

import java.util.Objects;
import java.util.Optional;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhBbRegisterService {

  private static final String GENERIC_ERROR_MESSAGE = "An error occurred when attempting to register a new customer.";

  private final CompanyDataService cdhCompanyService;
  private final EmployeeDataService cdhEmployeeService;
  private final Auth0Service auth0BusinessService;
  private final Auth0InnBusinessService auth0InnBusinessService;
  private final EmailService emailService;
  private final CompanyMapper companyMapper;
  private final EmployeeMapper employeeMapper;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public CustomerResponse bbRegisterInCdh(Customer newCustomer, String language) {
    final String email = newCustomer.getContactDetail().getEmail();
    try {
      auth0BusinessService.saveUserInAuth0(email, newCustomer.getPassword(), null);
      final CustomerResponse employeeResponse = createBbCustomerInCdh(newCustomer,
          language);
      return employeeResponse;
    } catch (Auth0ApiException e) {
      throw new AuthServiceException(
          String.format("Could not create BB user %s in Auth0.", email), e);
    }
  }

  public InnBRegistrationStepOneResponse registerInnBStepOneInCdh(
      InnBRegistrationStepOneRequest request) {
    log.debug("Called CdhBbRegisterService.registerInnBStepOneInCdh");

    return createInnBCustomerInChd(request);
  }

  public InnBRegistrationStepTwoResponse registerInnBStepTwoInCdh(
      InnBRegistrationStepTwoRequest request) {
    log.debug("Called CdhBbRegisterService.registerInnBStepTwoInCdh");

    var employee = getEmployeeByActivationKey(request.getActivationKey());
    var email = employee.getEmailAddress();
    if (hasCompanyDetailsToUpdate(request)) {
      updateCompany(employee.getCompanyAccountId(), request, email);
    }
    try {
      auth0InnBusinessService.saveUserInAuth0(email, request.getPassword(),
          employee.getCompanyAccountId(),
          employee.getEmployeeAccountId());
      updateInnBStepTwoEmployeeDetails(request, employee);
    } catch (Auth0ApiException e) {
      throw new AuthServiceException(
          String.format("Could not create InnBusiness user %s in Auth0.", email), e);
    }
    return InnBRegistrationStepTwoResponse.builder().email(email).companyId(employee.getCompanyAccountId()).build();
  }

  private void updateInnBStepTwoEmployeeDetails(InnBRegistrationStepTwoRequest request,
      GetEmployeeResponse employee) {
    var email = employee.getEmailAddress();
    var employeeAccountId = employee.getEmployeeAccountId();
    var companyAccountId = employee.getCompanyAccountId();

    var employeeAccountRequest = createEmployeeAccountRequest(request, employee);

    updateEmployeeAccount(companyAccountId, employeeAccountId, employeeAccountRequest, email);

    activateEmployee(employeeAccountId, request.getActivationKey(), email);
  }

  private boolean hasCompanyDetailsToUpdate(InnBRegistrationStepTwoRequest request) {
    return request.getCompanySector() != null
        || request.getAverageMonthlyBooking() != null
        || request.getNumberOfEmployee() != null;
  }

  public void updateCompany(String companyId, InnBRegistrationStepTwoRequest request, String userEmail) {
    GetCompanyResponse getCompanyResponse = getCompanyDetails(companyId, userEmail);

    CompanyAccountRequest companyAccountRequest = companyMapper
        .toCompanyAccountRequest(getCompanyResponse);
    companyMapper.toUpdatedCompanyAccountRequest(request, companyAccountRequest);

    cdhCompanyService.updateCompany(companyId, companyAccountRequest, userEmail);
  }

  public GetCompanyResponse getCompanyDetails(String companyId, String accessedBy) {
    Optional<GetCompanyResponse> optionalGetCompanyResponse = cdhCompanyService
        .getCompany(companyId, accessedBy);

    return optionalGetCompanyResponse.orElseThrow(
        () -> new CdhServiceException("Company with id " + companyId + " not found in CDH")
    );
  }


  private EmployeeAccountRequest createEmployeeAccountRequest(
      InnBRegistrationStepTwoRequest request, GetEmployeeResponse employee) {
    var employeeAccountRequest = employeeMapper.toEmployeeAccountRequest(employee);
    employeeMapper.toEmployeeAccountRequest(request, employeeAccountRequest);
    return employeeAccountRequest;
  }

  private void updateEmployeeAccount(String companyAccountId, String employeeAccountId,
      EmployeeAccountRequest employeeAccountRequest, String email) {
    cdhEmployeeService.updateEmployeeAccount(companyAccountId, employeeAccountId,
        employeeAccountRequest, email);
  }

  private void activateEmployee(String employeeAccountId, String activationKey, String email) {
    cdhEmployeeService.activateEmployee(employeeAccountId, activationKey, email);
  }


  private InnBRegistrationStepOneResponse createInnBCustomerInChd(
      InnBRegistrationStepOneRequest request) {
    var email = request.getEmail();
    var employeeExists = checkIfEmployeeExists(email);

    var companiesQueryParams = buildGetCompaniesQueryParams(request.getCompanyName(),
        request.getAddress());
    var companies = cdhCompanyService.getCompanies(companiesQueryParams, email);

    var companyExists = !(companies.isEmpty() || Objects.isNull(companies.get().getResults()));
    var companyType = companyExists ? companies.get().getResults().get(0).getCompanyType() : null;

    if (!employeeExists && !companyExists) {
      createInnBChdCompany(request);
    } else if (!employeeExists) {
      addInnBCdhEmployee(request, companies.get());
    }

    return InnBRegistrationStepOneResponse.builder()
        .existingEmployee(employeeExists)
        .existingCompany(companyExists)
        .existingCompanyType(companyType)
        .build();
  }


  private CustomerResponse createBbCustomerInCdh(Customer newCustomer, String language) {
    var email = newCustomer.getContactDetail().getEmail();
    var employeeExists = checkIfEmployeeExists(email);

    var companiesQueryParams = buildGetCompaniesQueryParams(newCustomer);
    var companies = cdhCompanyService.getCompanies(companiesQueryParams, email);

    var companyExists = !(companies.isEmpty() || Objects.isNull(companies.get().getResults()));
    var deprecatedSessionId = "deprecated";

    if (!employeeExists && !companyExists) {
      createCdhCompany(newCustomer, language);
      return new CustomerResponse(true, deprecatedSessionId,
          newCustomer.getContactDetail().getEmail(), false, false);
    }

    if (!employeeExists) {
      addCdhEmployee(newCustomer, companies.get(), language);
      return new CustomerResponse(true, deprecatedSessionId,
          newCustomer.getContactDetail().getEmail(), true, false);
    }

    return CustomerResponse.builder()
        .success(false)
        .existingEmployee(employeeExists)
        .existingCompany(companyExists)
        .sessionId(deprecatedSessionId)
        .build();
  }

  private boolean checkIfEmployeeExists(String email) {
    var employeesQueryParams = GetEmployeesQueryParams.builder()
        .emailAddress(email)
        .build();
    var employeeExisting = false;
    try {
      var employees = getEmployees(employeesQueryParams, email);
      if (employees.isPresent() && Objects.nonNull(employees.get().getResults()) && !employees.get()
          .getResults().isEmpty()) {
        log.error("Employee with email already exists in CDH");
        employeeExisting = true;
      }
    } catch (CDHException _) {
      throw new CdhServiceException(
          "Something went wrong when trying to get the employees from CDH");
    }
    return employeeExisting;
  }

  private GetEmployeeResponse getEmployeeByActivationKey(String activationKey) {
    var employeesQueryParams = GetEmployeesQueryParams.builder()
        .activationKey(activationKey)
        .build();
    try {
      var inactiveStatus = "INACTIVE";
      var employees = getEmployees(employeesQueryParams, "anonymous");
      if (employees.isPresent() && Objects.nonNull(employees.get().getResults()) && !employees.get()
          .getResults().isEmpty() &&
          employees.get().getResults().get(0).getEmployeeStatus().equals(inactiveStatus)) {
        return employees.get().getResults().get(0);
      } else {
        throw new CdhServiceException("Employee with activation key not found in CDH");
      }
    } catch (CDHException _) {
      throw new CdhServiceException(
          "Something went wrong when trying to get the employees from CDH");
    }
  }

  private CreateCompanyResponse createCdhCompany(Customer newCustomer, String language) {
    var email = newCustomer.getContactDetail().getEmail();
    var crateCompanyAccountRequest = companyMapper.toCompanyAccountRequest(newCustomer);
    var response = cdhCompanyService.createCompanyAccount(crateCompanyAccountRequest,
        email);
    var employee = getEmployeeDetails(response.getCompanyAccountId(),
        response.getEmployeeAccountId(), email);
    emailService.sendAsyncBBCompanyActivationEmail(employee, newCustomer.getCompanyName(),
        language, CompanyType.BB);
    return response;
  }

  private CreateCompanyResponse createInnBChdCompany(InnBRegistrationStepOneRequest request) {
    var email = request.getEmail();
    var createCompanyAccountRequest = companyMapper.toCompanyAccountRequest(request);

    if (CompanyType.BP.equals(request.getCompanyType())) {
      createCompanyAccountRequest.setCompanyType(CompanyType.BP.getValue());
    }

    var response = cdhCompanyService.createCompanyAccount(createCompanyAccountRequest, email);
    var employee = getEmployeeDetails(response.getCompanyAccountId(),
        response.getEmployeeAccountId(), email);
    emailService.sendAsyncBBCompanyActivationEmail(employee, request.getCompanyName(),
        request.getLanguage(), CompanyType.INNB);
    return response;
  }

  private EmployeeAccountResponse addCdhEmployee(Customer newCustomer,
      GetCompaniesResponse companies, String language) {
    var employeeAccountRequest = employeeMapper.toEmployeeAccountRequest(newCustomer);
    employeeAccountRequest.setAwaitingApproval(true);
    var companyAccountId = companies.getResults().get(0).getCompanyAccountId();
    var response = cdhEmployeeService.createEmployeeAccount(companyAccountId,
        employeeAccountRequest,
        newCustomer.getContactDetail().getEmail());
    emailService.sendAsyncRegisterNotificationEmails(newCustomer, language);
    return response;
  }

  private EmployeeAccountResponse addInnBCdhEmployee(InnBRegistrationStepOneRequest request,
      GetCompaniesResponse companies) {
    var employeeAccountRequest = employeeMapper.toEmployeeAccountRequest(request);
    employeeAccountRequest.setAwaitingApproval(true);
    var companyAccountId = companies.getResults().get(0).getCompanyAccountId();
    var response = cdhEmployeeService.createEmployeeAccount(companyAccountId,
        employeeAccountRequest,
        request.getEmail());
    emailService.sendAsyncRegisterNotificationEmails(request);
    return response;
  }

  private GetEmployeeResponse getEmployeeDetails(String companyAccountId, String employeeAccountId,
      String businessEmail) {
    if (isCdhApiDeprecationEnabled()) {
      return cdhEmployeeService.getEmployeeV2(companyAccountId, employeeAccountId, businessEmail)
          .orElseThrow(() -> new CdhServiceException(GENERIC_ERROR_MESSAGE));
    }
    return cdhEmployeeService.getEmployee(companyAccountId, employeeAccountId, businessEmail)
        .orElseThrow(() -> new CdhServiceException(GENERIC_ERROR_MESSAGE));
  }

  private Optional<GetEmployeesResponse> getEmployees(
      GetEmployeesQueryParams employeesQueryParams, String accessedBy) {
    if (isCdhApiDeprecationEnabled()) {
      return cdhEmployeeService.getEmployeesV2(employeesQueryParams, accessedBy);
    }
    return cdhEmployeeService.getEmployees(employeesQueryParams, accessedBy);
  }

  private boolean isCdhApiDeprecationEnabled() {
    return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCdhApiDeprecation());
  }

}