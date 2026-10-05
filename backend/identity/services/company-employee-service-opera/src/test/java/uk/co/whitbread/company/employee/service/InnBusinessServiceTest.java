package uk.co.whitbread.company.employee.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.company.employee.exceptions.CompanyNotFoundException;
import uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.employee.exceptions.InvalidOperationException;
import uk.co.whitbread.company.employee.mapper.EmployeeMapperImpl;
import uk.co.whitbread.company.employee.model.AccessLevel;
import uk.co.whitbread.company.employee.model.feature.FeatureFlag;
import uk.co.whitbread.company.employee.model.feature.UnleashWrapper;
import uk.co.whitbread.company.employee.model.innbusiness.ApproveRejectRequest;
import uk.co.whitbread.company.employee.model.innbusiness.SendActivationRequest;
import uk.co.whitbread.company.employee.properties.EmailProperties;
import uk.co.whitbread.company.employee.validation.InnbEmployeeValidator;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.azureemail.model.CompanyActivation;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinAccepted;
import uk.co.whitbread.shared.azureemail.model.RequestToJoinRejected;
import uk.co.whitbread.shared.azureemail.service.AzureEmailService;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InnBusinessServiceTest {
  
  private static final String EMAIL = "test@whitbread.com";
  private static final String LANGUAGE_EN = "en";
  private static final String COMPANY_NAME = "company";
  private static final String AUTHORIZATION = "Bearer ===";
  private static final String EMPLOYEE_ACTIVATION_URL = "en_activationUrl&key=";

  @Mock
  private AzureEmailService azureEmailService;
  @Mock
  private EmployeeDataService employeeDataService;
  @Mock
  private CompanyDataService companyDataService;
  @Mock
  private EmailProperties emailProperties;
  @Mock
  private TokenService authTokenService;
  @Mock
  private EmployeeMapperImpl employeeMapper;
  @Mock
  private InnbEmployeeValidator approveRejectValidator;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @InjectMocks
  private InnBusinessService innBusinessService;

  @BeforeEach
  void setup() {
    var mockedFeatureFlag = org.mockito.Mockito.mock(FeatureFlag.class);
    when(unleashWrapper.featureFlag()).thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(any())).thenReturn(false);
  }


  @Test
  void sendActivationEmail_WhenEmployeesApiReturnsEmptyOptional_ThenThrowException() {
    var request = SendActivationRequest.builder()
          .email(EMAIL)
          .language(LANGUAGE_EN)
          .companyName(COMPANY_NAME)
          .build();
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail())))
          .thenReturn(Optional.empty());

    assertThrows(EmployeeNotFoundException.class,
          () -> innBusinessService.sendActivationEmail(request));
    verify(employeeDataService).getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail()));
  }

  @Test
  void sendActivationEmail_WhenEmployeesApiReturnsNullResults_ThenThrowException() {
    var request = SendActivationRequest.builder()
          .email(EMAIL)
          .language(LANGUAGE_EN)
          .companyName(COMPANY_NAME)
          .build();
    var getEmployeesResponse = Optional.of(GetEmployeesResponse.builder().build());
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail())))
          .thenReturn(getEmployeesResponse);

    assertThrows(EmployeeNotFoundException.class,
          () -> innBusinessService.sendActivationEmail(request));
    verify(employeeDataService).getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail()));
  }

  @ParameterizedTest
  @MethodSource("provideEmployeeResponses")
  void sendActivationEmail_WhenEmployeesApiReturnsEmptyResults_ThenThrowException(GetEmployeesResponse getEmployeesResponse) {
    var request = SendActivationRequest.builder()
          .email(EMAIL)
          .language(LANGUAGE_EN)
          .companyName(COMPANY_NAME)
          .build();
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail())))
          .thenReturn(Optional.of(getEmployeesResponse));

    assertThrows(InvalidOperationException.class,
          () -> innBusinessService.sendActivationEmail(request));
    verify(employeeDataService).getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail()));
  }

  @Test
  void sendActivationEmail_WhenCompaniesApiReturnsEmptyOptional_ThenThrowException() {
    var request = SendActivationRequest.builder()
          .email(EMAIL)
          .language(LANGUAGE_EN)
          .companyName(COMPANY_NAME)
          .build();
    var foundEmployee = Optional.of(GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
          .employeeStatus("ACTIVE")
          .accessLevel("SUPER")
          .employeeAccountId("ea")
          .activationKey("ak")
          .companyAccountId("company1")
          .build()))
        .build());
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundEmployee);
    when(companyDataService.getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail())))
          .thenReturn(Optional.empty());

    assertThrows(CompanyNotFoundException.class,
          () -> innBusinessService.sendActivationEmail(request));
    verify(employeeDataService).getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail()));
    verify(companyDataService).getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail()));
  }

  @Test
  void sendActivationEmail_WhenCompaniesApiReturnsNullResults_ThenThrowException() {
    var request = SendActivationRequest.builder()
          .email(EMAIL)
          .language(LANGUAGE_EN)
          .companyName(COMPANY_NAME)
          .build();
    var foundEmployee = Optional.of(GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeStatus("ACTIVE")
                .accessLevel("SUPER")
                .employeeAccountId("ea")
                .activationKey("ak")
                .companyAccountId("company1")
                .build()))
          .build());
    var foundCompany = GetCompaniesResponse.builder().build();
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundEmployee);
    when(companyDataService.getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail())))
          .thenReturn(Optional.of(foundCompany));

    assertThrows(CompanyNotFoundException.class,
          () -> innBusinessService.sendActivationEmail(request));
    verify(employeeDataService).getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail()));
    verify(companyDataService).getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail()));
  }

  @ParameterizedTest
  @MethodSource("provideCompaniesResponses")
  void sendActivationEmail_WhenCompaniesApiReturnsEmptyResults_ThenThrowException(GetCompaniesResponse getCompaniesResponse) {
    var request = SendActivationRequest.builder()
          .email(EMAIL)
          .language(LANGUAGE_EN)
          .companyName(COMPANY_NAME)
          .build();
    var foundEmployee = Optional.of(GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeStatus("ACTIVE")
                .accessLevel("SUPER")
                .employeeAccountId("ea")
                .activationKey("ak")
                .companyAccountId("company1")
                .build()))
          .build());
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundEmployee);
    when(companyDataService.getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail())))
          .thenReturn(Optional.of(getCompaniesResponse));

    assertThrows(CompanyNotFoundException.class,
          () -> innBusinessService.sendActivationEmail(request));
    verify(employeeDataService).getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail()));
    verify(companyDataService).getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail()));
  }

  @Test
  void sendActivationEmail_WhenEmployeeDoesNotBelongToSelectedCompany_ThenThrowException() {
    var request = SendActivationRequest.builder()
          .email(EMAIL)
          .language(LANGUAGE_EN)
          .companyName(COMPANY_NAME)
          .build();
    var foundEmployee = Optional.of(GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeStatus("ACTIVE")
                .accessLevel("SUPER")
                .employeeAccountId("ea")
                .activationKey("ak")
                .companyAccountId("company1")
                .build()))
          .build());
    var foundCompany = Optional.of(GetCompaniesResponse.builder().results(List.of(GetCompanyResponse.builder()
                .status("ACTIVE")
                .companyAccountId("company2")
                .build()))
          .build());
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundEmployee);
    when(companyDataService.getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundCompany);

    var ex = assertThrows(EmployeeNotFoundException.class,
          () -> innBusinessService.sendActivationEmail(request));
    assertTrue(ex.getMessage().toLowerCase().contains("is not found in company"));
    verify(employeeDataService).getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail()));
    verify(companyDataService).getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail()));
  }

  @ParameterizedTest
  @MethodSource("provideEmployeeCompanyPairs")
  void sendActivationEmail_WhenCompanyOrEmployeeIsInactiveOrEmployeeNotSuperUser_ThenNoEmailIsSent(EmployeeCompanyPair employeeCompanyPair) {
    var request = SendActivationRequest.builder()
          .email(EMAIL)
          .language(LANGUAGE_EN)
          .companyName(COMPANY_NAME)
          .build();
    var foundEmployee = Optional.of(employeeCompanyPair.getEmployee());
    var foundCompany = Optional.of(employeeCompanyPair.getCompany());
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundEmployee);
    when(companyDataService.getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundCompany);

    assertThrows(InvalidOperationException.class, () -> innBusinessService.sendActivationEmail(request));

    verify(employeeDataService).getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail()));
    verify(companyDataService).getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail()));
    verify(azureEmailService, never()).sendBBCompanyActivationEmail(any());
  }

  @Test
  void sendActivationEmail_WhenLanguageIsEn_ThenEmailUrlIsForEnSite() {
    var request = SendActivationRequest.builder()
          .email(EMAIL)
          .language(LANGUAGE_EN)
          .companyName(COMPANY_NAME)
          .build();
    var foundEmployee = Optional.of(GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeStatus("INACTIVE")
                .accessLevel("SUPER")
                .employeeAccountId("ea")
                .activationKey("ak")
                .companyAccountId("company1")
                .build()))
          .build());
    var foundCompany = Optional.of(GetCompaniesResponse.builder().results(List.of(GetCompanyResponse.builder()
                .status("INACTIVE")
                .companyAccountId("company1")
                .build()))
          .build());
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundEmployee);
    when(companyDataService.getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundCompany);

    innBusinessService.sendActivationEmail(request);

    verify(employeeDataService).getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail()));
    verify(companyDataService).getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail()));
    verify(azureEmailService).sendBBCompanyActivationEmail(any());
    verify(emailProperties).getInnbCompanyActivationUrl();
    verify(emailProperties, never()).getInnbCompanyActivationUrlDe();
  }

  @Test
  void sendActivationEmail_WhenLanguageIsDe_ThenEmailUrlIsForDeSite() {
    var request = SendActivationRequest.builder()
          .email(EMAIL)
          .language("de")
          .companyName(COMPANY_NAME)
          .build();
    var foundEmployee = Optional.of(GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeStatus("INACTIVE")
                .accessLevel("SUPER")
                .employeeAccountId("ea")
                .activationKey("ak")
                .companyAccountId("company1")
                .build()))
          .build());
    var foundCompany = Optional.of(GetCompaniesResponse.builder().results(List.of(GetCompanyResponse.builder()
                .status("INACTIVE")
                .companyAccountId("company1")
                .build()))
          .build());
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundEmployee);
    when(companyDataService.getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundCompany);

    innBusinessService.sendActivationEmail(request);

    verify(employeeDataService).getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail()));
    verify(companyDataService).getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail()));
    verify(azureEmailService).sendBBCompanyActivationEmail(any());
    verify(emailProperties, never()).getInnbCompanyActivationUrl();
    verify(emailProperties).getInnbCompanyActivationUrlDe();
  }

  @Test
  void sendActivationEmail_WhenEmailSent_ThenCorrectDetailsProvided() {
    var request = SendActivationRequest.builder()
          .email(EMAIL)
          .language(LANGUAGE_EN)
          .companyName(COMPANY_NAME)
          .build();
    var foundEmployee = Optional.of(GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeStatus("INACTIVE")
                .accessLevel("SUPER")
                .employeeAccountId("ea")
                .activationKey("ak")
                .companyAccountId("company1")
                .build()))
          .build());
    var foundCompany = Optional.of(GetCompaniesResponse.builder().results(List.of(GetCompanyResponse.builder()
                .status("INACTIVE")
                .companyAccountId("company1")
                .build()))
          .build());
    when(employeeDataService.getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundEmployee);
    when(companyDataService.getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail())))
          .thenReturn(foundCompany);
    when(emailProperties.getInnbCompanyActivationUrl()).thenReturn("step2Url");

    innBusinessService.sendActivationEmail(request);

    var companyActivationCaptor = ArgumentCaptor.forClass(CompanyActivation.class);
    verify(employeeDataService).getEmployees(any(GetEmployeesQueryParams.class), eq(request.getEmail()));
    verify(companyDataService).getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail()));
    verify(azureEmailService).sendBBCompanyActivationEmail(companyActivationCaptor.capture());
    verify(emailProperties).getInnbCompanyActivationUrl();
    verify(emailProperties, never()).getInnbCompanyActivationUrlDe();
    assertEquals(request.getEmail(), companyActivationCaptor.getValue().getEmail());
    assertEquals(request.getLanguage(), companyActivationCaptor.getValue().getLanguage());
    assertEquals(request.getCompanyName(), companyActivationCaptor.getValue().getCompanyName());
    assertEquals("step2Url" + foundEmployee.get().getResults().get(0).getActivationKey(), companyActivationCaptor.getValue().getActivationLink());
  }

  @Test
  void approveRejectEmployee_WhenEmployeeRequestIsApproved_ThenFieldsSetCorrectlyAndEmailIsSent() {
    var request = new ApproveRejectRequest(EMAIL, AccessLevel.SELF, true, LANGUAGE_EN);
    var tokenClaims = CdhEmployeeDetails.builder()
          .employeeAccountId("managerId")
          .companyAccountId("managerCompId")
          .userEmail("manager@email.com")
          .build();
    when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION)).thenReturn(tokenClaims);
    var cdhManager = GetEmployeeResponse.builder()
          .employeeStatus("ACTIVE")
          .accessLevel("SUPER")
          .employeeAccountId(tokenClaims.getEmployeeAccountId())
          .companyAccountId(tokenClaims.getCompanyAccountId())
          .emailAddress(tokenClaims.getUserEmail())
          .build();
    var cdhEmployee = GetEmployeeResponse.builder()
          .employeeAccountId("employeeId")
          .companyAccountId(tokenClaims.getCompanyAccountId())
          .employeeStatus("INACTIVE")
          .emailAddress(EMAIL)
          .accessLevel("BOOKER")
          .awaitingApproval(true)
          .activationKey("activationKey")
          .build();
    var cdhManagerResponse = Optional.of(GetEmployeesResponse.builder().results(List.of(cdhManager)).build());
    var cdhEmployeeResponse = Optional.of(GetEmployeesResponse.builder().results(List.of(cdhEmployee)).build());
    doReturn(cdhManagerResponse)
          .when(employeeDataService)
          .getEmployees(argThat(p -> p.getEmailAddress().equals(tokenClaims.getUserEmail())), any());
    doReturn(cdhEmployeeResponse)
          .when(employeeDataService)
          .getEmployees(argThat(p -> p.getEmailAddress().equals(EMAIL)), any());
    when(employeeMapper.toEmployeeAccountRequest(cdhEmployee)).thenCallRealMethod();
    var cdhCompany = GetCompanyResponse.builder().companyName(COMPANY_NAME).build();
    when(companyDataService.getCompany(cdhEmployee.getCompanyAccountId(), cdhEmployee.getEmailAddress()))
          .thenReturn(Optional.of(cdhCompany));
    when(emailProperties.getInnbEmployeeActivationUrl()).thenReturn(EMPLOYEE_ACTIVATION_URL);

    innBusinessService.approveRejectEmployee(request, AUTHORIZATION);

    verify(approveRejectValidator).validateTokenClaims(tokenClaims);
    verify(approveRejectValidator).validateManagerAccessLevel(cdhManager);
    verify(approveRejectValidator).validateEmployeeSameCompanyWithManager(cdhManager, cdhEmployee);
    verify(approveRejectValidator).validateUserAndManagerStatus(cdhManager, cdhEmployee);
    var updateRequest = ArgumentCaptor.forClass(EmployeeAccountRequest.class);
    verify(employeeDataService)
          .updateEmployeeAccount(eq(cdhManager.getCompanyAccountId()), eq(cdhEmployee.getEmployeeAccountId()),
                updateRequest.capture(), eq(cdhManager.getEmailAddress()));
    assertEquals(request.getAccessLevel().toString(), updateRequest.getValue().getAccessLevel());
    assertFalse(updateRequest.getValue().isAwaitingApproval());
    assertTrue(updateRequest.getValue().getEmployeeStatus().equalsIgnoreCase("inactive"));
    var emailRequest = ArgumentCaptor.forClass(RequestToJoinAccepted.class);
    verify(azureEmailService, timeout(5000)).sendBBRequestToJoinAcceptedEmail(emailRequest.capture());
    assertEquals(cdhEmployee.getEmailAddress(), emailRequest.getValue().getEmail());
    assertEquals(cdhEmployee.getFirstName(), emailRequest.getValue().getFirstName());
    assertEquals(cdhEmployee.getLastName(), emailRequest.getValue().getLastName());
    assertEquals(EMPLOYEE_ACTIVATION_URL+cdhEmployee.getActivationKey(), emailRequest.getValue().getBbActivatedVerificationUrl());
    assertEquals(cdhCompany.getCompanyName(), emailRequest.getValue().getCompanyName());
    assertEquals(request.getLanguage(), emailRequest.getValue().getLanguage());
  }

  @Test
  void approveRejectEmployee_WhenEmployeeRequestIsRejected_ThenEmployeeIsDeletedAndEmailIsSent() {
    var request = new ApproveRejectRequest(EMAIL, AccessLevel.SELF, false, LANGUAGE_EN);
    var tokenClaims = CdhEmployeeDetails.builder()
          .employeeAccountId("managerId")
          .companyAccountId("managerCompId")
          .userEmail("manager@email.com")
          .build();
    when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION)).thenReturn(tokenClaims);
    var cdhManager = GetEmployeeResponse.builder()
          .employeeStatus("ACTIVE")
          .accessLevel("SUPER")
          .employeeAccountId(tokenClaims.getEmployeeAccountId())
          .companyAccountId(tokenClaims.getCompanyAccountId())
          .emailAddress(tokenClaims.getUserEmail())
          .build();
    var cdhEmployee = GetEmployeeResponse.builder()
          .employeeAccountId("employeeId")
          .companyAccountId(tokenClaims.getCompanyAccountId())
          .employeeStatus("INACTIVE")
          .emailAddress(EMAIL)
          .accessLevel("BOOKER")
          .awaitingApproval(true)
          .activationKey("activationKey")
          .build();
    var cdhManagerResponse = Optional.of(GetEmployeesResponse.builder().results(List.of(cdhManager)).build());
    var cdhEmployeeResponse = Optional.of(GetEmployeesResponse.builder().results(List.of(cdhEmployee)).build());
    doReturn(cdhManagerResponse)
          .when(employeeDataService)
          .getEmployees(argThat(p -> p.getEmailAddress().equals(tokenClaims.getUserEmail())), any());
    doReturn(cdhEmployeeResponse)
          .when(employeeDataService)
          .getEmployees(argThat(p -> p.getEmailAddress().equals(EMAIL)), any());
    var cdhCompany = GetCompanyResponse.builder().companyName(COMPANY_NAME).build();
    when(companyDataService.getCompany(cdhManager.getCompanyAccountId(), cdhManager.getEmailAddress()))
          .thenReturn(Optional.of(cdhCompany));

    innBusinessService.approveRejectEmployee(request, AUTHORIZATION);

    verify(approveRejectValidator).validateTokenClaims(tokenClaims);
    verify(approveRejectValidator).validateManagerAccessLevel(cdhManager);
    verify(approveRejectValidator).validateEmployeeSameCompanyWithManager(cdhManager, cdhEmployee);
    verify(approveRejectValidator).validateUserAndManagerStatus(cdhManager, cdhEmployee);
    verify(employeeDataService)
          .deleteEmployeeAccount(cdhEmployee.getCompanyAccountId(), cdhEmployee.getEmployeeAccountId(),
                cdhManager.getEmailAddress());
    var emailRequest = ArgumentCaptor.forClass(RequestToJoinRejected.class);
    verify(azureEmailService, timeout(5000)).sendRequestToJoinRejectedEmail(emailRequest.capture());
    assertEquals(cdhCompany.getCompanyName(), emailRequest.getValue().getCompanyName());
    assertEquals(cdhEmployee.getFirstName(), emailRequest.getValue().getFirstName());
    assertEquals(cdhEmployee.getLastName(), emailRequest.getValue().getLastName());
    assertEquals(cdhEmployee.getEmailAddress(), emailRequest.getValue().getEmailAddress());
    assertEquals(request.getLanguage(), emailRequest.getValue().getLanguage());
  }

  @Test
  void sendActivationEmail_WithFeatureFlagEnabled_WhenEmployeesApiReturnsEmptyOptional_ThenThrowException() {
    var request = SendActivationRequest.builder()
        .email(EMAIL)
        .language(LANGUAGE_EN)
        .companyName(COMPANY_NAME)
        .build();
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(employeeDataService.getEmployeesV2(any(GetEmployeesQueryParams.class),
        eq(request.getEmail())))
        .thenReturn(Optional.empty());

    assertThrows(EmployeeNotFoundException.class,
        () -> innBusinessService.sendActivationEmail(request));
    verify(employeeDataService).getEmployeesV2(any(GetEmployeesQueryParams.class),
        eq(request.getEmail()));
  }

  @Test
  void sendActivationEmail_WithFeatureFlagEnabled_WhenEmailSent_ThenCorrectDetailsProvided() {
    var request = SendActivationRequest.builder()
        .email(EMAIL)
        .language(LANGUAGE_EN)
        .companyName(COMPANY_NAME)
        .build();
    var foundEmployee = Optional.of(
        GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeStatus("INACTIVE")
                .accessLevel("SUPER")
                .employeeAccountId("ea")
                .activationKey("ak")
                .companyAccountId("company1")
                .build()))
            .build());
    var foundCompany = Optional.of(
        GetCompaniesResponse.builder().results(List.of(GetCompanyResponse.builder()
                .status("INACTIVE")
                .companyAccountId("company1")
                .build()))
            .build());
    when(unleashWrapper.isEnabled(any())).thenReturn(true);
    when(employeeDataService.getEmployeesV2(any(GetEmployeesQueryParams.class),
        eq(request.getEmail())))
        .thenReturn(foundEmployee);
    when(
        companyDataService.getCompanies(any(GetCompaniesQueryParams.class), eq(request.getEmail())))
        .thenReturn(foundCompany);
    when(emailProperties.getInnbCompanyActivationUrl()).thenReturn("step2Url");

    innBusinessService.sendActivationEmail(request);

    var companyActivationCaptor = ArgumentCaptor.forClass(CompanyActivation.class);
    verify(employeeDataService).getEmployeesV2(any(GetEmployeesQueryParams.class),
        eq(request.getEmail()));
    verify(companyDataService).getCompanies(any(GetCompaniesQueryParams.class),
        eq(request.getEmail()));
    verify(azureEmailService).sendBBCompanyActivationEmail(companyActivationCaptor.capture());
    verify(emailProperties).getInnbCompanyActivationUrl();
    verify(emailProperties, never()).getInnbCompanyActivationUrlDe();
    assertEquals(request.getEmail(), companyActivationCaptor.getValue().getEmail());
    assertEquals(request.getLanguage(), companyActivationCaptor.getValue().getLanguage());
    assertEquals(request.getCompanyName(), companyActivationCaptor.getValue().getCompanyName());
    assertEquals("step2Url" + foundEmployee.get().getResults().get(0).getActivationKey(),
        companyActivationCaptor.getValue().getActivationLink());
  }

  private static Stream<GetEmployeesResponse> provideEmployeeResponses() {
    return Stream.of(
          GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeAccountId("").companyAccountId("ca").employeeStatus("es").accessLevel("al").activationKey("ak").build())).build(),
          GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeAccountId("ea").companyAccountId("").employeeStatus("es").accessLevel("al").activationKey("ak").build())).build(),
          GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeAccountId("ea").companyAccountId("ca").employeeStatus("").accessLevel("al").activationKey("ak").build())).build(),
          GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeAccountId("ea").companyAccountId("ca").employeeStatus("es").accessLevel("").activationKey("ak").build())).build(),
          GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                .employeeAccountId("ea").companyAccountId("ca").employeeStatus("es").accessLevel("al").activationKey("").build())).build());
  }

  private static Stream<GetCompaniesResponse> provideCompaniesResponses() {
    return Stream.of(
          GetCompaniesResponse.builder().results(List.of(GetCompanyResponse.builder()
                .status("").companyAccountId("ca").build())).build(),
          GetCompaniesResponse.builder().results(List.of(GetCompanyResponse.builder()
                .status("st").companyAccountId("").build())).build());
  }

  private static Stream<EmployeeCompanyPair> provideEmployeeCompanyPairs() {
    return Stream.of(
          EmployeeCompanyPair.builder()
                .employee(GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                      .employeeStatus("ACTIVE")
                      .accessLevel("SUPER")
                      .employeeAccountId("ea")
                      .activationKey("ak")
                      .companyAccountId("company1")
                      .build()))
                      .build())
                .company(GetCompaniesResponse.builder().results(List.of(GetCompanyResponse.builder()
                      .status("INACTIVE")
                      .companyAccountId("company1")
                      .build()))
                      .build())
                .build(),
          EmployeeCompanyPair.builder()
                .employee(GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                            .employeeStatus("INACTIVE")
                            .accessLevel("BOOKER")
                            .employeeAccountId("ea")
                            .activationKey("ak")
                            .companyAccountId("company1")
                            .build()))
                      .build())
                .company(GetCompaniesResponse.builder().results(List.of(GetCompanyResponse.builder()
                            .status("INACTIVE")
                            .companyAccountId("company1")
                            .build()))
                      .build())
                .build(),
          EmployeeCompanyPair.builder()
                .employee(GetEmployeesResponse.builder().results(List.of(GetEmployeeResponse.builder()
                            .employeeStatus("INACTIVE")
                            .accessLevel("SUPER")
                            .employeeAccountId("ea")
                            .activationKey("ak")
                            .companyAccountId("company1")
                            .build()))
                      .build())
                .company(GetCompaniesResponse.builder().results(List.of(GetCompanyResponse.builder()
                            .status("ACTIVE")
                            .companyAccountId("company1")
                            .build()))
                      .build())
                .build());
  }

  @Getter
  @Builder
  private static class EmployeeCompanyPair {
    private GetEmployeesResponse employee;
    private GetCompaniesResponse company;
  }
}