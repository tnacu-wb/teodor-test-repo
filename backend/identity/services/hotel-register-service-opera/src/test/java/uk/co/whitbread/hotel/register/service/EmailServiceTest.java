package uk.co.whitbread.hotel.register.service;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.register.exceptions.CdhServiceException;
import uk.co.whitbread.hotel.register.model.CompanyType;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.model.InnBCompanyAddress;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;
import uk.co.whitbread.hotel.register.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.register.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.register.properties.EmailProperties;
import uk.co.whitbread.hotel.register.utils.register.CustomerTransformer;
import uk.co.whitbread.shared.azureemail.model.CompanyActivation;
import uk.co.whitbread.shared.azureemail.model.EmployeeRegistrationNotification;
import uk.co.whitbread.shared.azureemail.model.PiRegister;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.GetCompanyEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesResponse;

import java.util.Optional;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  private static final String DEFAULT_LANGUAGE = "en";
  private static final String GERMAN_LANGUAGE = "de";
  private static final String DEFAULT_COUNTRY = "gb";
  private static final String COMPANY_ACTIVATION_URL = "premierinn.com?key=";
  private static final String COMPANY_ACTIVATION_URL_DE = "premierinn.com/de?key=";
  private static final String INNB_COMPANY_ACTIVATION_URL = "innb.premierinn.com?key=";
  private static final String INNB_COMPANY_ACTIVATION_URL_DE = "innb.premierinn.com/de?key=";

  @InjectMocks
  private EmailService service;
  @Mock
  private AzureEmailService azureEmailService;
  @Mock
  private CompanyDataService cdhCompanyService;
  @Mock
  private EmployeeDataService cdhEmployeeService;
  @Mock
  private CountriesService countriesService;
  @Mock
  private CustomerTransformer customerTransformer;
  @Mock
  private EmailProperties emailProperties;
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
  void sendAsyncPiRegisterEmail_success() {
    // Given
    var newCustomer = random(Customer.class);
    var piRegister = PiRegister.builder().build();

    when(countriesService.getCountryLegend(anyString())).thenReturn(DEFAULT_COUNTRY);
    when(customerTransformer.toPiRegister(newCustomer, DEFAULT_LANGUAGE,
        DEFAULT_COUNTRY)).thenReturn(piRegister);

    // When
    service.sendAsyncPiRegisterEmail(newCustomer, DEFAULT_LANGUAGE);

    // Then
    verify(azureEmailService).sendPiRegisterEmail(piRegister);
  }

  @Test
  void sendAsyncBBCompanyActivationEmail_en_success() {
    // Given
    var employee = random(GetEmployeeResponse.class);
    var companyName = "Company name";
    var captor = ArgumentCaptor.forClass(CompanyActivation.class);

    when(emailProperties.getCompanyActivationUrl()).thenReturn(COMPANY_ACTIVATION_URL);

    // When
    service.sendAsyncBBCompanyActivationEmail(employee, companyName, DEFAULT_LANGUAGE, CompanyType.BB);

    // Then
    verify(azureEmailService).sendBBCompanyActivationEmail(captor.capture());
    var companyActivation = captor.getValue();
    assertEquals(COMPANY_ACTIVATION_URL + employee.getActivationKey(),
        companyActivation.getActivationLink());
  }

  @Test
  void sendAsyncINNBCompanyActivationEmail_en_success() {
    // Given
    var employee = random(GetEmployeeResponse.class);
    var companyName = "Company name";
    var captor = ArgumentCaptor.forClass(CompanyActivation.class);

    when(emailProperties.getInnbCompanyActivationUrl()).thenReturn(INNB_COMPANY_ACTIVATION_URL);

    // When
    service.sendAsyncBBCompanyActivationEmail(employee, companyName, DEFAULT_LANGUAGE, CompanyType.INNB);

    // Then
    verify(azureEmailService).sendBBCompanyActivationEmail(captor.capture());
    var companyActivation = captor.getValue();
    assertEquals(INNB_COMPANY_ACTIVATION_URL + employee.getActivationKey(),
        companyActivation.getActivationLink());
  }

  @Test
  void sendAsyncBBCompanyActivationEmail_de_success() {
    // Given
    var employee = random(GetEmployeeResponse.class);
    var companyName = "Company name";
    var captor = ArgumentCaptor.forClass(CompanyActivation.class);

    when(emailProperties.getCompanyActivationUrlDe()).thenReturn(COMPANY_ACTIVATION_URL_DE);

    // When
    service.sendAsyncBBCompanyActivationEmail(employee, companyName, GERMAN_LANGUAGE, CompanyType.BB);

    // Then
    verify(azureEmailService).sendBBCompanyActivationEmail(captor.capture());
    var companyActivation = captor.getValue();
    assertEquals(COMPANY_ACTIVATION_URL_DE + employee.getActivationKey(),
        companyActivation.getActivationLink());
  }

  @Test
  void sendAsyncINNBCompanyActivationEmail_de_success() {
    // Given
    var employee = random(GetEmployeeResponse.class);
    var companyName = "Company name";
    var captor = ArgumentCaptor.forClass(CompanyActivation.class);

    when(emailProperties.getInnbCompanyActivationUrlDe()).thenReturn(INNB_COMPANY_ACTIVATION_URL_DE);

    // When
    service.sendAsyncBBCompanyActivationEmail(employee, companyName, GERMAN_LANGUAGE, CompanyType.INNB);

    // Then
    verify(azureEmailService).sendBBCompanyActivationEmail(captor.capture());
    var companyActivation = captor.getValue();
    assertEquals(INNB_COMPANY_ACTIVATION_URL_DE + employee.getActivationKey(),
        companyActivation.getActivationLink());
  }

  @Test
  void sendAsyncRegisterNotificationEmails_success() {
    // Given
    var newCustomer = random(Customer.class);
    var response = random(GetCompaniesResponse.class);
    var employees = random(GetEmployeesResponse.class);
    employees.setContinuationToken(null);

    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), anyString()))
        .thenReturn(Optional.of(response));
    when(cdhEmployeeService.getCompanyEmployeesV2(anyString(),
        any(GetCompanyEmployeesQueryParams.class), anyString())).thenReturn(Optional.of(employees));

    // When
    service.sendAsyncRegisterNotificationEmails(newCustomer, DEFAULT_LANGUAGE);

    // Then
    verify(azureEmailService, times(employees.getResults().size()))
        .sendEmployeeRegistrationNotificationEmail(any(EmployeeRegistrationNotification.class));
  }

  @Test
  void sendAsyncRegisterNotificationEmails_companyNotFound() {
    // Given
    var newCustomer = random(Customer.class);

    when(cdhCompanyService.getCompanies(any(GetCompaniesQueryParams.class), anyString()))
        .thenReturn(Optional.empty());

    assertThrows(CdhServiceException.class, () -> service.sendAsyncRegisterNotificationEmails(newCustomer, DEFAULT_LANGUAGE));

    // Then
    verify(azureEmailService, times(0)).sendEmployeeRegistrationNotificationEmail(
        any(EmployeeRegistrationNotification.class));
  }

  @Test
  void testSendAsyncRegisterNotificationEmails() {
    // Given
    InnBRegistrationStepOneRequest request = new InnBRegistrationStepOneRequest();
    request.setEmail("test@example.com");
    request.setCompanyName("Test Company");
    request.setAddress(new InnBCompanyAddress());
    request.setLanguage("en");
    GetCompanyResponse company = mock(GetCompanyResponse.class);
    when(company.getCompanyType()).thenReturn(CompanyType.BB.getValue());

    GetCompaniesResponse companiesResponse = new GetCompaniesResponse();
    companiesResponse.setResults(Collections.singletonList(company));

    var employees = new GetEmployeesResponse();
    employees.setContinuationToken(null);
    employees.setResults(Collections.singletonList(new GetEmployeeResponse()));

    when(cdhCompanyService.getCompanies(any(), eq(request.getEmail()))).thenReturn(Optional.of(companiesResponse));
    when(cdhEmployeeService.getCompanyEmployeesV2(any(), any(), eq(request.getEmail()))).thenReturn(Optional.of(employees));

    // When
    service.sendAsyncRegisterNotificationEmails(request);

    // Then
    verify(azureEmailService, times(1)).sendEmployeeRegistrationNotificationEmail(any(EmployeeRegistrationNotification.class));
  }

  @Test
  void testSendAsyncRegisterNotificationEmails_companyTypeBP_usesBusinessPayManagerAccessLevel() {
    // Given
    InnBRegistrationStepOneRequest request = new InnBRegistrationStepOneRequest();
    request.setEmail("test@example.com");
    request.setCompanyName("Test Company");
    request.setAddress(new InnBCompanyAddress());
    request.setLanguage("en");

    GetCompanyResponse company = mock(GetCompanyResponse.class);
    when(company.getCompanyType()).thenReturn(CompanyType.BP.getValue());

    GetCompaniesResponse companiesResponse = new GetCompaniesResponse();
    companiesResponse.setResults(Collections.singletonList(company));

    var employees = new GetEmployeesResponse();
    employees.setContinuationToken(null);
    employees.setResults(Collections.singletonList(new GetEmployeeResponse()));

    when(cdhCompanyService.getCompanies(any(), eq(request.getEmail()))).thenReturn(Optional.of(companiesResponse));
    when(cdhEmployeeService.getCompanyEmployeesV2(any(), any(), eq(request.getEmail()))).thenReturn(Optional.of(employees));

    ArgumentCaptor<GetCompanyEmployeesQueryParams> paramsCaptor = ArgumentCaptor.forClass(GetCompanyEmployeesQueryParams.class);

    // When
    service.sendAsyncRegisterNotificationEmails(request);

    // Then
    verify(cdhEmployeeService).getCompanyEmployeesV2(any(), paramsCaptor.capture(), eq(request.getEmail()));
    assertEquals(uk.co.whitbread.shared.cdh.AccessLevel.BUSINESS_PAY_MANAGER, paramsCaptor.getValue().getAccessLevel());
    verify(azureEmailService, times(1)).sendEmployeeRegistrationNotificationEmail(any(EmployeeRegistrationNotification.class));
  }

  @Test
  void testSendAsyncRegisterNotificationEmails_flagDisabled_usesLegacyCompanyEmployeesCall() {
    // Given
    InnBRegistrationStepOneRequest request = new InnBRegistrationStepOneRequest();
    request.setEmail("test@example.com");
    request.setCompanyName("Test Company");
    request.setAddress(new InnBCompanyAddress());
    request.setLanguage("en");
    GetCompanyResponse company = mock(GetCompanyResponse.class);
    when(company.getCompanyType()).thenReturn(CompanyType.BP.getValue());

    GetCompaniesResponse companiesResponse = new GetCompaniesResponse();
    companiesResponse.setResults(Collections.singletonList(company));

    var employees = new GetEmployeesResponse();
    employees.setContinuationToken(null);
    employees.setResults(Collections.singletonList(new GetEmployeeResponse()));

    var featureFlag = unleashWrapper.featureFlag().getCdhApiDeprecation();
    when(unleashWrapper.isEnabled(featureFlag)).thenReturn(false);
    when(cdhCompanyService.getCompanies(any(), eq(request.getEmail()))).thenReturn(Optional.of(companiesResponse));
    when(cdhEmployeeService.getCompanyEmployees(any(), any(), eq(request.getEmail()))).thenReturn(Optional.of(employees));

    // When
    service.sendAsyncRegisterNotificationEmails(request);

    // Then
    verify(cdhEmployeeService).getCompanyEmployees(any(), any(), eq(request.getEmail()));
    verify(cdhEmployeeService, never()).getCompanyEmployeesV2(any(), any(), eq(request.getEmail()));
  }

}
