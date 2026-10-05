package uk.co.whitbread.hotel.register.service;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.co.whitbread.shared.auth.exception.Auth0ApiException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.register.exceptions.CdhServiceException;
import uk.co.whitbread.hotel.register.mapper.CompanyMapper;
import uk.co.whitbread.hotel.register.mapper.EmployeeMapper;
import uk.co.whitbread.hotel.register.model.CompanyType;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.CustomerResponse;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneResponse;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoRequest;
import uk.co.whitbread.hotel.register.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.register.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.register.service.auth0.Auth0BusinessService;
import uk.co.whitbread.hotel.register.service.auth0.Auth0InnBusinessService;
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
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@ExtendWith(MockitoExtension.class)
public class CdhBbRegisterServiceTest {

  private static final String GENERIC_ERROR_MESSAGE = "An error occurred when attempting to register a new customer.";
  private static final String DEFAULT_LANGUAGE = "en";
  private static final String COMPANY_ACCOUNT_ID = "COMPff352343-aace-497a-ba24-c62d191c997b";
  private static final String EMPLOYEE_ACCOUNT_ID = "EMPLb240ecec-41cd-43b9-a382-c2341502fa3e";
  private static final String ACTIVATION_KEY = "activation-key";
  private static final String DEPRECATED_SESSION_ID = "deprecated";

  @InjectMocks
  private CdhBbRegisterService service;
  @Mock
  private Auth0BusinessService auth0BusinessService;
  @Mock
  private CompanyDataService cdhCompanyService;
  @Mock
  private EmployeeDataService cdhEmployeeService;
  @Mock
  private EmailService emailService;
  @Mock
  private CompanyMapper companyMapper;
  @Mock
  private EmployeeMapper employeeMapper;
  @Mock
  private Auth0InnBusinessService auth0InnBusinessService;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @BeforeEach
  void setUp() {
    var featureFlag = new FeatureFlag();
    featureFlag.setCdhApiDeprecation(new FeatureFlag.Feature());
    lenient().when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    lenient().when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
  }

  @Test
  public void bbRegisterInCdh_newCompany_success() throws Auth0ApiException {
    // Given
    var newCustomer = random(Customer.class);
    var email = newCustomer.getContactDetail().getEmail();
    var companyAccountRequest = new CompanyAccountRequest();
    var createCompanyResponse = CreateCompanyResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .build();
    var employee = new GetEmployeeResponse();
    var customerResponse = new CustomerResponse(true, DEPRECATED_SESSION_ID, email, false, false);

    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(companyMapper.toCompanyAccountRequest(newCustomer)).thenReturn(companyAccountRequest);
    when(cdhCompanyService.createCompanyAccount(companyAccountRequest, email))
        .thenReturn(createCompanyResponse);
    when(cdhEmployeeService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email))
        .thenReturn(Optional.of(employee));

    // When
    var response = service.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE);

    // Then
    assertEquals(customerResponse, response);
    verify(emailService).sendAsyncBBCompanyActivationEmail(employee, newCustomer.getCompanyName(),
        DEFAULT_LANGUAGE, CompanyType.BB);
    verify(auth0BusinessService).saveUserInAuth0(email, newCustomer.getPassword(), null);
  }

  @Test
  void bbRegisterInCdh_flagDisabled_usesLegacyEmployeesCall() throws Auth0ApiException {
    // Given
    var newCustomer = random(Customer.class);
    var email = newCustomer.getContactDetail().getEmail();
    var companyAccountRequest = new CompanyAccountRequest();
    var createCompanyResponse = CreateCompanyResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .build();
    var employee = new GetEmployeeResponse();
    var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();

    when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
    when(cdhEmployeeService.getEmployees(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(companyMapper.toCompanyAccountRequest(newCustomer)).thenReturn(companyAccountRequest);
    when(cdhCompanyService.createCompanyAccount(companyAccountRequest, email))
        .thenReturn(createCompanyResponse);
    when(cdhEmployeeService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email))
        .thenReturn(Optional.of(employee));

    // When
    service.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE);

    // Then
    verify(cdhEmployeeService).getEmployees(any(GetEmployeesQueryParams.class), eq(email));
    verify(cdhEmployeeService, times(0)).getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email));
    verify(cdhEmployeeService).getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email);
    verify(cdhEmployeeService, times(0)).getEmployeeV2(anyString(), anyString(), anyString());
  }

  @Test
  public void bbRegisterInCdh_newCompany_nullValuesCdh_success() throws Auth0ApiException {
    // Given
    var newCustomer = random(Customer.class);
    var email = newCustomer.getContactDetail().getEmail();
    var companyAccountRequest = new CompanyAccountRequest();
    var createCompanyResponse = CreateCompanyResponse.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
            .build();
    var employee = new GetEmployeeResponse();
    var customerResponse = new CustomerResponse(true, DEPRECATED_SESSION_ID, email, false, false);

    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
            .thenReturn(Optional.ofNullable(GetEmployeesResponse.builder()
                    .results(null)
                    .build()));
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
            .thenReturn(Optional.ofNullable(GetCompaniesResponse.builder()
                    .results(null)
                    .build()));
    when(companyMapper.toCompanyAccountRequest(newCustomer)).thenReturn(companyAccountRequest);
    when(cdhCompanyService.createCompanyAccount(companyAccountRequest, email))
            .thenReturn(createCompanyResponse);
    when(cdhEmployeeService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email))
            .thenReturn(Optional.of(employee));

    // When
    var response = service.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE);

    // Then
    assertEquals(customerResponse, response);
    verify(emailService).sendAsyncBBCompanyActivationEmail(employee, newCustomer.getCompanyName(),
            DEFAULT_LANGUAGE, CompanyType.BB);
    verify(auth0BusinessService).saveUserInAuth0(email, newCustomer.getPassword(), null);
  }

  @Test
  public void bbRegisterInCdh_newCompany_throwsCdhServiceException_whenGetEmployeeV2ReturnsEmpty() throws Auth0ApiException {
    // Given
    var newCustomer = random(Customer.class);
    var email = newCustomer.getContactDetail().getEmail();
    var companyAccountRequest = new CompanyAccountRequest();
    var createCompanyResponse = CreateCompanyResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .build();
    var employee = new GetEmployeeResponse();

    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(companyMapper.toCompanyAccountRequest(newCustomer)).thenReturn(companyAccountRequest);
    when(cdhCompanyService.createCompanyAccount(companyAccountRequest, email))
        .thenReturn(createCompanyResponse);
    when(cdhEmployeeService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email))
        .thenReturn(Optional.empty());

    assertThrows(CdhServiceException.class, () -> service.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE), GENERIC_ERROR_MESSAGE);

    // Then
    verify(emailService, times(0)).sendAsyncBBCompanyActivationEmail(employee,
        newCustomer.getCompanyName(), DEFAULT_LANGUAGE, CompanyType.BB);
    verify(auth0BusinessService).saveUserInAuth0(email, newCustomer.getPassword(), null);
  }

  @Test
  public void bbRegisterInCdh_newCompany_flagDisabled_throwsCdhServiceException_whenGetEmployeeReturnsEmpty() throws Auth0ApiException {
    // Given
    var newCustomer = random(Customer.class);
    var email = newCustomer.getContactDetail().getEmail();
    var companyAccountRequest = new CompanyAccountRequest();
    var createCompanyResponse = CreateCompanyResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .build();
    var employee = new GetEmployeeResponse();
    var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();

    when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
    when(cdhEmployeeService.getEmployees(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(companyMapper.toCompanyAccountRequest(newCustomer)).thenReturn(companyAccountRequest);
    when(cdhCompanyService.createCompanyAccount(companyAccountRequest, email))
        .thenReturn(createCompanyResponse);
    when(cdhEmployeeService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email))
        .thenReturn(Optional.empty());

    assertThrows(CdhServiceException.class, () -> service.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE), GENERIC_ERROR_MESSAGE);

    // Then
    verify(cdhEmployeeService).getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email);
    verify(cdhEmployeeService, times(0)).getEmployeeV2(anyString(), anyString(), anyString());
    verify(emailService, times(0)).sendAsyncBBCompanyActivationEmail(employee,
        newCustomer.getCompanyName(), DEFAULT_LANGUAGE, CompanyType.BB);
  }

  @Test
  public void bbRegisterInCdh_existingCompany_success() throws Auth0ApiException {
    // Given
    var newCustomer = random(Customer.class);
    var email = newCustomer.getContactDetail().getEmail();
    var companies = random(GetCompaniesResponse.class);
    var employeeAccountRequest = new EmployeeAccountRequest();
    var employeeAccountResponse = EmployeeAccountResponse.builder()
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .activationKey(ACTIVATION_KEY)
        .build();
    var customerResponse = new CustomerResponse(true, DEPRECATED_SESSION_ID, email, true, false);

    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.of(companies));
    when(employeeMapper.toEmployeeAccountRequest(newCustomer)).thenReturn(employeeAccountRequest);
    when(cdhEmployeeService.createEmployeeAccount(anyString(), eq(employeeAccountRequest),
        eq(email))).thenReturn(employeeAccountResponse);

    // When
    var response = service.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE);

    // Then
    assertEquals(customerResponse, response);
    verify(emailService).sendAsyncRegisterNotificationEmails(newCustomer, DEFAULT_LANGUAGE);
    verify(auth0BusinessService).saveUserInAuth0(email, newCustomer.getPassword(), null);
  }

  @Test
  public void bbRegisterInCdh_employeeAlreadyExists()
      throws Auth0ApiException {
    // Given
    var newCustomer = random(Customer.class);
    var email = newCustomer.getContactDetail().getEmail();
    var employees = random(GetEmployeesResponse.class);
    var customerResponse = CustomerResponse.builder()
            .success(false)
            .existingEmployee(true)
            .existingCompany(false)
            .sessionId(DEPRECATED_SESSION_ID)
            .build();

    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.of(employees));

    // When
    var response = service.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE);

    // Then
    assertEquals(customerResponse, response);
  }

  @Test
  public void bbRegisterInCdh_companyAlreadyExists() {
    // Given
    var newCustomer = random(Customer.class);
    var email = newCustomer.getContactDetail().getEmail();
    var companies = random(GetCompaniesResponse.class);
    var employeeAccountRequest = new EmployeeAccountRequest();
    var employeeAccountResponse = EmployeeAccountResponse.builder()
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
            .activationKey(ACTIVATION_KEY)
            .build();
    var customerResponse = CustomerResponse.builder()
            .success(true)
            .customerId(newCustomer.getContactDetail().getEmail())
            .existingEmployee(false)
            .existingCompany(true)
            .sessionId(DEPRECATED_SESSION_ID)
            .build();

    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
            .thenReturn(Optional.of(companies));
    when(employeeMapper.toEmployeeAccountRequest(newCustomer)).thenReturn(employeeAccountRequest);
    when(cdhEmployeeService.createEmployeeAccount(anyString(), eq(employeeAccountRequest),
            eq(email))).thenReturn(employeeAccountResponse);

    // When
    var response = service.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE);

    // Then
    assertEquals(customerResponse, response);
  }


  @Test
  public void bbRegisterInCdh_companyAlreadyExists_employeeAlreadyExists() {
    // Given
    var newCustomer = random(Customer.class);
    var email = newCustomer.getContactDetail().getEmail();
    var companies = random(GetCompaniesResponse.class);
    var employees = random(GetEmployeesResponse.class);
    var customerResponse = CustomerResponse.builder()
            .success(false)
            .existingEmployee(true)
            .existingCompany(true)
            .sessionId(DEPRECATED_SESSION_ID)
            .build();

    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
            .thenReturn(Optional.of(companies));
    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
            .thenReturn(Optional.of(employees));
    // When
    var response = service.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE);

    // Then
    assertEquals(customerResponse, response);
  }

  @Test
  public void bbRegisterInCdh_throwsAuthServiceException() throws Auth0ApiException {
    // Given
    var newCustomer = random(Customer.class);
    var email = newCustomer.getContactDetail().getEmail();

    doThrow(new Auth0ApiException("error", null)).when(auth0BusinessService)
        .saveUserInAuth0(email, newCustomer.getPassword(), null);

    assertThrows(AuthServiceException.class, () -> service.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE));
  }

  @Test
  public void bbRegisterInCdh_throwsCdhServiceException() {
    // Given
    var newCustomer = random(Customer.class);
    var email = newCustomer.getContactDetail().getEmail();

    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
            .thenThrow(new CDHException());

    assertThrows(CdhServiceException.class, () -> service.bbRegisterInCdh(newCustomer, DEFAULT_LANGUAGE), GENERIC_ERROR_MESSAGE);
  }

  @Test
  void registerInnBStepOneInCdh_newCompany_success() {
    // Given
    var request = random(InnBRegistrationStepOneRequest.class);
    var email = request.getEmail();
    var companyAccountRequest = new CompanyAccountRequest();
    var createCompanyResponse = CreateCompanyResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .build();
    var employee = new GetEmployeeResponse();
    var response = InnBRegistrationStepOneResponse.builder()
        .existingCompany(false)
        .existingEmployee(false)
        .existingCompanyType(null)
        .build();

    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(companyMapper.toCompanyAccountRequest(request)).thenReturn(companyAccountRequest);
    when(cdhCompanyService.createCompanyAccount(companyAccountRequest, email))
        .thenReturn(createCompanyResponse);
    when(cdhEmployeeService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email))
        .thenReturn(Optional.of(employee));

    // When
    var result = service.registerInnBStepOneInCdh(request);

    // Then
    assertEquals(response, result);
    verify(emailService).sendAsyncBBCompanyActivationEmail(employee, request.getCompanyName(), request.getLanguage(), CompanyType.INNB);
  }

  @Test
  void registerInnBStepOneInCdh_newCompany_companyTypeBP_setsCompanyTypeBP() {
    // Given
    var request = random(InnBRegistrationStepOneRequest.class);
    var email = request.getEmail();
    request.setCompanyType(CompanyType.BP);
    var companyAccountRequest = new CompanyAccountRequest();
    var createCompanyResponse = CreateCompanyResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .build();
    var employee = new GetEmployeeResponse();
    var response = InnBRegistrationStepOneResponse.builder()
        .existingCompany(false)
        .existingEmployee(false)
        .existingCompanyType(null)
        .build();

    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(companyMapper.toCompanyAccountRequest(request)).thenReturn(companyAccountRequest);
    when(cdhCompanyService.createCompanyAccount(any(CompanyAccountRequest.class), eq(email)))
        .thenReturn(createCompanyResponse);
    when(cdhEmployeeService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email))
        .thenReturn(Optional.of(employee));

    ArgumentCaptor<CompanyAccountRequest> captor = ArgumentCaptor.forClass(CompanyAccountRequest.class);

    // When
    var result = service.registerInnBStepOneInCdh(request);

    // Then
    assertEquals(response, result);
    verify(cdhCompanyService).createCompanyAccount(captor.capture(), eq(email));
    assertEquals(CompanyType.BP.getValue(), captor.getValue().getCompanyType());
  }

  @Test
  void registerInnBStepOneInCdh_existingCompany_success() {
    // Given
    var request = random(InnBRegistrationStepOneRequest.class);
    var email = request.getEmail();
    var companies = random(GetCompaniesResponse.class);
    var employeeAccountRequest = new EmployeeAccountRequest();
    var employeeAccountResponse = EmployeeAccountResponse.builder()
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .activationKey(ACTIVATION_KEY)
        .build();
    var existingCompany = true;
    var existingEmployee = false;
    var companyType = companies.getResults().get(0).getCompanyType();
    var response = new InnBRegistrationStepOneResponse(existingCompany, existingEmployee, companyType);

    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.of(companies));
    when(employeeMapper.toEmployeeAccountRequest(request)).thenReturn(employeeAccountRequest);
    when(cdhEmployeeService.createEmployeeAccount(anyString(), eq(employeeAccountRequest), eq(email)))
        .thenReturn(employeeAccountResponse);

    // When
    var result = service.registerInnBStepOneInCdh(request);

    // Then
    assertEquals(response, result);
    verify(emailService).sendAsyncRegisterNotificationEmails(request);
  }

  @Test
  void registerInnBStepOneInCdh_employeeAlreadyExists() {
    // Given
    var request = random(InnBRegistrationStepOneRequest.class);
    var email = request.getEmail();
    var employees = random(GetEmployeesResponse.class);
    var response = InnBRegistrationStepOneResponse.builder()
        .existingEmployee(true)
        .existingCompany(false)
        .existingCompanyType(null)
        .build();

    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.of(employees));

    // When
    var result = service.registerInnBStepOneInCdh(request);

    // Then
    assertEquals(response, result);
  }

  @Test
  void registerInnBStepOneInCdh_companyAlreadyExists() {
    // Given
    var request = random(InnBRegistrationStepOneRequest.class);
    var email = request.getEmail();
    var companies = random(GetCompaniesResponse.class);
    var employeeAccountRequest = new EmployeeAccountRequest();
    var employeeAccountResponse = EmployeeAccountResponse.builder()
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .activationKey(ACTIVATION_KEY)
        .build();
    var response = InnBRegistrationStepOneResponse.builder()
        .existingEmployee(false)
        .existingCompany(true)
        .existingCompanyType(companies.getResults().get(0).getCompanyType())
        .build();

    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.of(companies));
    when(employeeMapper.toEmployeeAccountRequest(request)).thenReturn(employeeAccountRequest);
    when(cdhEmployeeService.createEmployeeAccount(anyString(), eq(employeeAccountRequest), eq(email)))
        .thenReturn(employeeAccountResponse);

    // When
    var result = service.registerInnBStepOneInCdh(request);

    // Then
    assertEquals(response, result);
  }

  @Test
  void registerInnBStepOneInCdh_companyAlreadyExists_employeeAlreadyExists() {
    // Given
    var request = random(InnBRegistrationStepOneRequest.class);
    var email = request.getEmail();
    var companies = random(GetCompaniesResponse.class);
    var employees = random(GetEmployeesResponse.class);
    var response = InnBRegistrationStepOneResponse.builder()
        .existingEmployee(true)
        .existingCompany(true)
        .existingCompanyType(companies.getResults().get(0).getCompanyType())
        .build();

    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.of(companies));
    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.of(employees));

    // When
    var result = service.registerInnBStepOneInCdh(request);

    // Then
    assertEquals(response, result);
  }

  @Test
  void registerInnBStepOneInCdh_newCompany_throwsCdhServiceException_whenGetEmployeeV2ReturnsEmpty() {
    // Given
    var request = random(InnBRegistrationStepOneRequest.class);
    var email = request.getEmail();
    var companyAccountRequest = new CompanyAccountRequest();
    var createCompanyResponse = CreateCompanyResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .build();

    when(cdhEmployeeService.getEmployeesV2(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(companyMapper.toCompanyAccountRequest(request)).thenReturn(companyAccountRequest);
    when(cdhCompanyService.createCompanyAccount(companyAccountRequest, email))
        .thenReturn(createCompanyResponse);
    when(cdhEmployeeService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email))
        .thenReturn(Optional.empty());

    // When / Then
    assertThrows(CdhServiceException.class, () -> service.registerInnBStepOneInCdh(request), GENERIC_ERROR_MESSAGE);
    verify(cdhEmployeeService).getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email);
  }

  @Test
  void registerInnBStepOneInCdh_newCompany_flagDisabled_usesLegacyGetEmployeeCall() {
    // Given
    var request = random(InnBRegistrationStepOneRequest.class);
    var email = request.getEmail();
    var companyAccountRequest = new CompanyAccountRequest();
    var createCompanyResponse = CreateCompanyResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .build();
    var employee = new GetEmployeeResponse();
    var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();

    when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
    when(cdhEmployeeService.getEmployees(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(companyMapper.toCompanyAccountRequest(request)).thenReturn(companyAccountRequest);
    when(cdhCompanyService.createCompanyAccount(companyAccountRequest, email))
        .thenReturn(createCompanyResponse);
    when(cdhEmployeeService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email))
        .thenReturn(Optional.of(employee));

    // When
    var result = service.registerInnBStepOneInCdh(request);

    // Then
    verify(cdhEmployeeService).getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email);
    verify(cdhEmployeeService, times(0)).getEmployeeV2(anyString(), anyString(), anyString());
    verify(emailService).sendAsyncBBCompanyActivationEmail(employee, request.getCompanyName(), request.getLanguage(), CompanyType.INNB);
  }

  @Test
  void registerInnBStepOneInCdh_newCompany_flagDisabled_throwsCdhServiceException_whenGetEmployeeReturnsEmpty() {
    // Given
    var request = random(InnBRegistrationStepOneRequest.class);
    var email = request.getEmail();
    var companyAccountRequest = new CompanyAccountRequest();
    var createCompanyResponse = CreateCompanyResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .build();
    var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();

    when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
    when(cdhEmployeeService.getEmployees(any(GetEmployeesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), eq(email)))
        .thenReturn(Optional.empty());
    when(companyMapper.toCompanyAccountRequest(request)).thenReturn(companyAccountRequest);
    when(cdhCompanyService.createCompanyAccount(companyAccountRequest, email))
        .thenReturn(createCompanyResponse);
    when(cdhEmployeeService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email))
        .thenReturn(Optional.empty());

    // When / Then
    assertThrows(CdhServiceException.class, () -> service.registerInnBStepOneInCdh(request), GENERIC_ERROR_MESSAGE);
    verify(cdhEmployeeService).getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, email);
    verify(cdhEmployeeService, times(0)).getEmployeeV2(anyString(), anyString(), anyString());
  }

  @Test
  void registerInnBStepTwoInCdh_success() throws Auth0ApiException {
    // Given
    var request = random(InnBRegistrationStepTwoRequest.class);
    var employee = random(GetEmployeeResponse.class);
    employee.setEmployeeStatus("INACTIVE");
    var employeesResponse = GetEmployeesResponse.builder()
        .results(List.of(employee))
        .build();
    var getCompanyResponse = random(GetCompanyResponse.class);
    var companyAccountRequest = random(CompanyAccountRequest.class);

    when(cdhEmployeeService.getEmployeesV2(any(), anyString())).thenReturn(Optional.of(employeesResponse));
    doNothing().when(auth0InnBusinessService).saveUserInAuth0(anyString(), anyString(), anyString(), anyString());
    when(cdhCompanyService.getCompany(eq(employee.getCompanyAccountId()), anyString())).thenReturn(Optional.of(getCompanyResponse));
    when(companyMapper.toCompanyAccountRequest(getCompanyResponse)).thenReturn(companyAccountRequest);

    // When
    var response = service.registerInnBStepTwoInCdh(request);

    // Then
    verify(auth0InnBusinessService).saveUserInAuth0(
        employee.getEmailAddress(),
        request.getPassword(),
        employee.getCompanyAccountId(),
        employee.getEmployeeAccountId()
    );
    verify(cdhEmployeeService).updateEmployeeAccount(anyString(), anyString(), any(), anyString());
    verify(cdhEmployeeService).activateEmployee(anyString(), eq(request.getActivationKey()), anyString());
    verify(companyMapper).toUpdatedCompanyAccountRequest(request, companyAccountRequest);
    verify(cdhCompanyService).updateCompany(eq(employee.getCompanyAccountId()), eq(companyAccountRequest), anyString());
    assertEquals(employee.getEmailAddress(), response.getEmail());
  }

  @Test
  void registerInnBStepTwoInCdh_noCompanyDetailsToUpdate_doesNotUpdateCompany() throws Auth0ApiException {
    // Given
    var request = random(InnBRegistrationStepTwoRequest.class);
    request.setCompanySector(null);
    request.setAverageMonthlyBooking(null);
    request.setNumberOfEmployee(null);
    var employee = random(GetEmployeeResponse.class);
    employee.setEmployeeStatus("INACTIVE");
    var employeesResponse = GetEmployeesResponse.builder()
        .results(List.of(employee))
        .build();

    when(cdhEmployeeService.getEmployeesV2(any(), anyString())).thenReturn(Optional.of(employeesResponse));
    doNothing().when(auth0InnBusinessService).saveUserInAuth0(anyString(), anyString(), anyString(), anyString());

    // When
    var response = service.registerInnBStepTwoInCdh(request);

    // Then
    verify(cdhEmployeeService).updateEmployeeAccount(anyString(), anyString(), any(), anyString());
    verify(cdhEmployeeService).activateEmployee(anyString(), eq(request.getActivationKey()), anyString());
    verify(cdhCompanyService, never()).getCompany(anyString(), anyString());
    verify(companyMapper, never()).toUpdatedCompanyAccountRequest(any(), any());
    verify(cdhCompanyService, never()).updateCompany(anyString(), any(), anyString());
    assertEquals(employee.getEmailAddress(), response.getEmail());
  }

  @Test
  void registerInnBStepTwoInCdh_flagDisabled_usesLegacyEmployeesCall() throws Auth0ApiException {
    // Given
    var request = random(InnBRegistrationStepTwoRequest.class);
    request.setCompanySector(null);
    request.setAverageMonthlyBooking(null);
    request.setNumberOfEmployee(null);
    var employee = random(GetEmployeeResponse.class);
    employee.setEmployeeStatus("INACTIVE");
    var employeesResponse = GetEmployeesResponse.builder()
        .results(List.of(employee))
        .build();
    var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();

    when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
    when(cdhEmployeeService.getEmployees(any(), anyString())).thenReturn(Optional.of(employeesResponse));
    doNothing().when(auth0InnBusinessService).saveUserInAuth0(anyString(), anyString(), anyString(), anyString());

    // When
    var response = service.registerInnBStepTwoInCdh(request);

    // Then
    verify(cdhEmployeeService).getEmployees(any(), eq("anonymous"));
    verify(cdhEmployeeService, times(0)).getEmployeesV2(any(), anyString());
    assertEquals(employee.getEmailAddress(), response.getEmail());
  }

  @Test
  void registerInnBStepTwoInCdh_Auth0ApiException() throws Auth0ApiException {
    // Given
    var request = random(InnBRegistrationStepTwoRequest.class);
    request.setCompanySector(null);
    request.setAverageMonthlyBooking(null);
    request.setNumberOfEmployee(null);
    var employees = random(GetEmployeesResponse.class);
    employees.getResults().get(0).setEmployeeStatus("INACTIVE");
    when(cdhEmployeeService.getEmployeesV2(any(), anyString())).thenReturn(Optional.of(employees));
    doThrow(Auth0ApiException.class).when(auth0InnBusinessService).saveUserInAuth0(anyString(), anyString(), anyString(), anyString());

    // When / Then
    assertThrows(AuthServiceException.class, () -> service.registerInnBStepTwoInCdh(request));
  }

  @Test
  void registerInnBStepTwoInCdh_employeeStatusNotInactive() {
    // Given
    var request = random(InnBRegistrationStepTwoRequest.class);
    var employee = random(GetEmployeeResponse.class);
    employee.setEmployeeStatus("ACTIVE");
    var employeesResponse = GetEmployeesResponse.builder()
        .results(List.of(employee))
        .build();

    when(cdhEmployeeService.getEmployeesV2(any(), anyString())).thenReturn(Optional.of(employeesResponse));

    // When / Then
    assertThrows(CdhServiceException.class, () -> service.registerInnBStepTwoInCdh(request), "Employee with activation key not found in CDH");
  }

}
