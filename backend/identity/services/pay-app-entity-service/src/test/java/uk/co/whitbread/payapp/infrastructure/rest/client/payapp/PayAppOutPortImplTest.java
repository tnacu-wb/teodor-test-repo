package uk.co.whitbread.payapp.infrastructure.rest.client.payapp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.payapp.ErrorCode.APPLICATION_ACCESS_DENIED;
import static uk.co.whitbread.payapp.ErrorCode.CDH_APPLICATION_NOT_FOUND_ERROR;
import static uk.co.whitbread.payapp.ErrorCode.EMPLOYEE_NOT_IN_COMPANY_EXCEPTION;
import static uk.co.whitbread.payapp.ErrorCode.USER_ROLE_MISMATCH_ERROR;
import static uk.co.whitbread.payapp.infrastructure.rest.client.payapp.PayAppOutPortImpl.APP_ALREADY_SHARED_MESSAGE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.whitbread.payapp.ErrorCode;
import uk.co.whitbread.payapp.domain.model.in.AddApplicationCardDetails;
import uk.co.whitbread.payapp.domain.model.in.AddApplicationCardRequest;
import uk.co.whitbread.payapp.domain.model.in.AppCompanyDetails;
import uk.co.whitbread.payapp.domain.model.in.AppCompanyDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.AppPreCheckRequest;
import uk.co.whitbread.payapp.domain.model.in.ApplicationDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.DeleteCardRequest;
import uk.co.whitbread.payapp.domain.model.in.DeletePayApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.DirectDebitOption;
import uk.co.whitbread.payapp.domain.model.in.DirectDebitRequest;
import uk.co.whitbread.payapp.domain.model.in.GetAppCardsRequest;
import uk.co.whitbread.payapp.domain.model.in.GetAppLookupRequest;
import uk.co.whitbread.payapp.domain.model.in.GetDdSepaFormStatusRequest;
import uk.co.whitbread.payapp.domain.model.in.InitializeApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.LookupName;
import uk.co.whitbread.payapp.domain.model.in.RemoveParticipantRequest;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.domain.model.in.ShareAppRequest;
import uk.co.whitbread.payapp.domain.model.in.SubmitApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateAppCompanyDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateAppContactDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateResumeUrlRequest;
import uk.co.whitbread.payapp.domain.model.in.jwt.JwtTokenClaims;
import uk.co.whitbread.payapp.domain.model.out.AddApplicationCardResponse;
import uk.co.whitbread.payapp.domain.model.out.AppPreCheckResponse;
import uk.co.whitbread.payapp.domain.model.out.ApplicationDetails;
import uk.co.whitbread.payapp.domain.model.out.ApplicationDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.CardDetails;
import uk.co.whitbread.payapp.domain.model.out.CompanyDetails;
import uk.co.whitbread.payapp.domain.model.out.CreditLimit;
import uk.co.whitbread.payapp.domain.model.out.GetAppCardsResponse;
import uk.co.whitbread.payapp.domain.model.out.GetUserPreferencesResponse;
import uk.co.whitbread.payapp.domain.model.out.SubmitApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.UserPreferenceDetails;
import uk.co.whitbread.payapp.domain.model.out.UserPreferenceSettingsItem;
import uk.co.whitbread.payapp.domain.ports.secondary.EmailNotificationOutPort;
import uk.co.whitbread.payapp.generated.models.company.CompanyDetailsDto;
import uk.co.whitbread.payapp.generated.models.company.CompanyDetailsResponseDto;
import uk.co.whitbread.payapp.generated.models.company.CompanyDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.CdhAdapterClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.CdhClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions.AppAlreadySharedException;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions.CdhNotFoundException;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions.EmployeeNotInCompanyException;
import uk.co.whitbread.payapp.infrastructure.rest.client.company.CompanyClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.exceptions.ApplicationAccessException;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AddApplicationCardResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AppCompanyDetailsLookupMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AppInitRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.AppPreCheckResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.ApplicationDetailsResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.GetApplicationCardsResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.GetUserPreferencesResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.SubmitApplicationRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.SubmitApplicationResponseMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.UpdateAppCompanyDetailsRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.UpdateAppContactDetailsRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper.UpdateApplicationRequestMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.WorldlineClient;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.exceptions.WorldlineResponseException;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.SubmitApplicationRequestDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WLAppCompanyDetailsUpdateRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WLAppContactDetailsUpdateRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppCancelRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppInitRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineAppPreCheckRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.WorldlineHeadersDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppCompanyDetailsLookupDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppDetailsDataDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppInitDataDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.AppPreCheckDataDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.BankDetailsStatusDataDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.CardDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.CreditLimitDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.FetchApplicationDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.GetAppCardsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.GetUserPreferencesResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.SubmitApplicationDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.UserPreferenceDataDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.UserPreferenceDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.UserPreferenceSettingsItemDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAddApplicationCardDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAddApplicationCardResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppCompanyDetailsLookupResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppCompanyDetailsUpdateResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppContactDetailsUpdateResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLAppInitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLErrorDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLHostedPageAppInitDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WLHostedPageAppInitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WorldlineAppPreCheckResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.WorldlineBankDetailsStatusResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties.WorldlineProperties;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties.WorldlineProperties.PropertiesByLocation;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.mapper.ApplicationDetailsMapper;
import uk.co.whitbread.payapp.infrastructure.security.JwtUtils;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;
import uk.co.whitbread.shared.cdh.model.Participants;
import uk.co.whitbread.shared.cdh.model.StartApplicationRequest;
import uk.co.whitbread.shared.cdh.model.StartApplicationResponse;
import uk.co.whitbread.shared.cdh.model.UpdateApplicationRequest;
import uk.co.whitbread.shared.cdh.model.UpdateApplicationResponse;
import uk.co.whitbread.shared.cdh.model.applications.UpdateApplicationStatusRequest;
import uk.co.whitbread.shared.cdh.model.applications.UpdateApplicationStatusResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationParticipant;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.CardHolder;
import uk.co.whitbread.shared.cdh.model.spending.application.UpdateApplicationCardHoldersRequest;

@ExtendWith(MockitoExtension.class)
class PayAppOutPortImplTest {

  @Mock
  private WorldlineClient worldlineClient;

  @Mock
  private CdhClient cdhClient;

  @Mock
  private CompanyClient companyClient;

  @Mock
  private WorldlineProperties worldlineProperties;

  @Mock
  private UpdateAppContactDetailsRequestMapper updateAppContactDetailsRequestMapper;

  @Mock
  private UpdateAppCompanyDetailsRequestMapper updateAppCompanyDetailsRequestMapper;

  @Mock
  private AppInitRequestMapper appInitRequestMapper;

  @Mock
  private ApplicationDetailsMapper applicationDetailsMapper;

  @Mock
  private AppCompanyDetailsLookupMapper appCompanyDetailsLookupMapper;

  @Mock
  private GetUserPreferencesResponseMapper getUserPreferencesMapper;

  @Mock
  private ApplicationDetailsResponseMapper applicationDetailsResponseMapper;

  @Mock
  private GetApplicationCardsResponseMapper getApplicationCardsResponseMapper;

  @Mock
  private AppPreCheckResponseMapper appPreCheckResponseMapper;

  @Mock
  private AuthenticatedUserService authenticatedUserService;

  @Mock
  private EmailNotificationOutPort emailNotificationOutPort;

  @Mock
  private AddApplicationCardResponseMapper addApplicationCardResponseMapper;

  @Mock
  private SubmitApplicationRequestMapper submitApplicationRequestMapper;

  @Mock
  private SubmitApplicationResponseMapper submitApplicationResponseMapper;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private CdhAdapterClient cdhAdapterClient;

  private final Executor worldlineExecutor = Runnable::run;

  @Mock
  private CacheManager cacheManager1Hour;

  @Spy
  private UpdateApplicationRequestMapper updateApplicationRequestMapper = Mappers.getMapper(
      UpdateApplicationRequestMapper.class);

  @InjectMocks
  private PayAppOutPortImpl payAppOutPort;

  private static final String APPLICATION_ID = "1122";
  private static final String APPLICATION_GUID = "123456";
  private static final String EMAIL = "test@gmail.com";
  private static final String CLIENT_IP = "1.1.1.1";
  private static final String COMPANY_ID = "COMP_213123";
  private static final String CARD_GUID = "7a7d764f-da39-4f52-9a3f-d74c407f467d";
  private static final String SUCCESS_MESSAGE = "Thank you for submitting your details.";
  private static final int EMPLOYEE_ID = 12345;
  private static final String FULL_NAME = "FullName";
  private static final String HOSTED_PAGE_GUID = "7a4e96f2-e4a1-4b68-b03e-30240ac0a514";


  @BeforeEach
  void setUp() {
     ReflectionTestUtils.setField(payAppOutPort, "worldlineExecutor", worldlineExecutor);
  }

  @Test
  void initializeApplicationDE__ShouldReturnOk() {
    // Arrange
    var userAccount = createAccount();

    when(appInitRequestMapper.toModel(any(InitializeApplicationRequest.class))).thenReturn(
        createWorldlineAppInitRequestDto());
    when(worldlineClient.appInitWorldline(any(WorldlineAppInitRequestDto.class),
        any(WorldlineHeadersDto.class)))
        .thenReturn(createWorldlineAppInitResponseDto());
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(createCustomJwtAuthenticationTokenTM());
    when(companyClient.getCompanyDetails(any(), any()))
        .thenReturn(createCompanyDetailsResponseDto());
    when(cdhClient.startApplication(any(StartApplicationRequest.class))).thenReturn(
        createStartApplicationResponse());
    when(worldlineProperties.getDe()).thenReturn(createPropertiesDE());

    // Act
    var initializeApplicationResponse = payAppOutPort.initializeApplication(
        createInitializeApplicationRequestDE(), CLIENT_IP);

    // Assert
    assertThat(initializeApplicationResponse, notNullValue());
    assertEquals("123", initializeApplicationResponse.getApplicationGUID());
    verify(appInitRequestMapper).toModel(any(InitializeApplicationRequest.class));
  }

  @Test
  void initializeApplicationGB__ShouldReturnOk() {
    // Arrange
    var userAccount = createAccount();

    when(appInitRequestMapper.toModel(any(InitializeApplicationRequest.class))).thenReturn(
        createWorldlineAppInitRequestDto());
    when(worldlineClient.appInitWorldline(any(WorldlineAppInitRequestDto.class),
        any(WorldlineHeadersDto.class)))
        .thenReturn(createWorldlineAppInitResponseDto());
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(createCustomJwtAuthenticationTokenTM());
    when(companyClient.getCompanyDetails(any(), any()))
        .thenReturn(createCompanyDetailsResponseDto());
    when(cdhClient.startApplication(any(StartApplicationRequest.class))).thenReturn(
        createStartApplicationResponse());
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());

    // Act
    var initializeApplicationResponse = payAppOutPort.initializeApplication(
        createInitializeApplicationRequestGB(), CLIENT_IP);

    // Assert
    assertThat(initializeApplicationResponse, notNullValue());
    assertEquals("123", initializeApplicationResponse.getApplicationGUID());
    verify(appInitRequestMapper).toModel(any(InitializeApplicationRequest.class));
  }

  @Test
  void fetchApplicationsDetails__ShouldReturnOkAccountNameFromWL() {
    // Arrange
    var jwtTokenClaims = createJwtTokenClaims();
    var applicationResponse = createApplicationResponse();
    var fetchApplicationDetailsResponseDto = createFetchApplicationDetailsResponseDto();

    when(cdhClient.fetchApplicationsByUser(any(JwtTokenClaims.class))).thenReturn(
        List.of(applicationResponse));
    when(worldlineClient.fetchWorldlineApplicationDetails(any(String.class), any())).thenReturn(
        fetchApplicationDetailsResponseDto);
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(applicationDetailsMapper.toModel(any(ApplicationResponse.class))).thenReturn(
        createApplicationDetails());

    // Act
    var fetchApplicationsResponse = payAppOutPort.fetchApplicationsDetails(jwtTokenClaims,
        CLIENT_IP);

    // Assert
    assertThat(fetchApplicationsResponse, notNullValue());
    assertEquals(1, fetchApplicationsResponse.getApplications().size());
    assertEquals("PENDING", fetchApplicationsResponse.getApplications().getFirst().getStatus());
    assertEquals(fetchApplicationDetailsResponseDto.getData().getCompanyDetails().getCompanyName(),
        fetchApplicationsResponse.getApplications().getFirst().getAccountName());
    assertEquals("applicationId",
        fetchApplicationsResponse.getApplications().getFirst().getApplicationId());
    assertEquals("applicationGuid",
        fetchApplicationsResponse.getApplications().getFirst().getApplicationGuid());
    assertEquals("resumeUrl",
        fetchApplicationsResponse.getApplications().getFirst().getResumeUrl());
    verify(cdhClient).fetchApplicationsByUser(any(JwtTokenClaims.class));
    verify(worldlineClient).fetchWorldlineApplicationDetails(any(String.class), any());
  }

  @Test
  void fetchApplicationsDetails__ShouldReturnOkAccountNameFromCdh() {
    // Arrange
    var jwtTokenClaims = createJwtTokenClaims();
    var applicationResponse = createApplicationResponse();
    var fetchApplicationDetailsResponseDto = createFetchApplicationDetailsResponseDto();
    fetchApplicationDetailsResponseDto.getData().getCompanyDetails().setCompanyName(null);

    when(cdhClient.fetchApplicationsByUser(any(JwtTokenClaims.class))).thenReturn(
        List.of(applicationResponse));
    when(worldlineClient.fetchWorldlineApplicationDetails(any(String.class), any())).thenReturn(
        fetchApplicationDetailsResponseDto);
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(applicationDetailsMapper.toModel(any(ApplicationResponse.class))).thenReturn(
        createApplicationDetails());

    // Act
    var fetchApplicationsResponse = payAppOutPort.fetchApplicationsDetails(jwtTokenClaims,
        CLIENT_IP);

    // Assert
    assertThat(fetchApplicationsResponse, notNullValue());
    assertEquals(1, fetchApplicationsResponse.getApplications().size());
    assertEquals("PENDING", fetchApplicationsResponse.getApplications().getFirst().getStatus());
    assertEquals(applicationResponse.getAccountName(),
        fetchApplicationsResponse.getApplications().getFirst().getAccountName());
    assertEquals("applicationId",
        fetchApplicationsResponse.getApplications().getFirst().getApplicationId());
    assertEquals("applicationGuid",
        fetchApplicationsResponse.getApplications().getFirst().getApplicationGuid());
    assertEquals("resumeUrl",
        fetchApplicationsResponse.getApplications().getFirst().getResumeUrl());
    verify(cdhClient).fetchApplicationsByUser(any(JwtTokenClaims.class));
    verify(worldlineClient).fetchWorldlineApplicationDetails(any(String.class), any());
  }

  @Test
  void fetchApplicationsDetails_ShouldBuildHeadersMapCorrectly() {
    // Arrange
    var jwtTokenClaims = createJwtTokenClaims();
    var applicationResponseDE = ApplicationResponse.builder().scheme("DE").applicationGuid("1")
        .build();
    var applicationResponseGB = ApplicationResponse.builder().scheme("GB").applicationGuid("2")
        .build();
    var applicationResponseInvalid = ApplicationResponse.builder().scheme("INVALID")
        .applicationGuid("3").build();
    var applicationResponseNull = ApplicationResponse.builder().scheme(null).applicationGuid("4")
        .build();
    var applicationResponses = List.of(applicationResponseDE, applicationResponseGB,
        applicationResponseInvalid, applicationResponseNull);
    var fetchApplicationDetailsResponseDto = createFetchApplicationDetailsResponseDto();

    when(cdhClient.fetchApplicationsByUser(any(JwtTokenClaims.class))).thenReturn(
        applicationResponses);
    when(worldlineClient.fetchWorldlineApplicationDetails(any(String.class), any())).thenReturn(
        fetchApplicationDetailsResponseDto);
    when(worldlineProperties.getDe()).thenReturn(createPropertiesDE());
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(applicationDetailsMapper.toModel(any(ApplicationResponse.class))).thenReturn(
        createApplicationDetails());

    // Act
    var fetchApplicationsResponse = payAppOutPort.fetchApplicationsDetails(jwtTokenClaims,
        CLIENT_IP);

    // Assert
    assertThat(fetchApplicationsResponse, notNullValue());
    assertThat(fetchApplicationsResponse.getApplications(), hasSize(4));

    verify(worldlineProperties, times(1)).getDe();
    verify(worldlineProperties, times(3)).getGb();
  }

  @Test
  void getUserPreferences__ShouldReturnOk() {
    // Arrange
    when(worldlineClient.getUserPreferences(any(), any())).thenReturn(
        GetUserPreferencesResponseDto.builder().data(UserPreferenceDataDto.builder()
            .details(UserPreferenceDetailsDto.builder().accountName("accountName").build())
            .settings(List.of(UserPreferenceSettingsItemDto.builder().build())).build()).build());
    when(getUserPreferencesMapper.toModel(any(GetUserPreferencesResponseDto.class))).thenReturn(
        GetUserPreferencesResponse.builder()
            .details(UserPreferenceDetails.builder().accountName("accountName").build())
            .settings(List.of(UserPreferenceSettingsItem.builder().build())).build());
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());

    // Act
    var userPreferences = payAppOutPort.getUserPreferences(Set.of("tetheredUserGuid"), CLIENT_IP);

    // Assert
    assertThat(userPreferences, notNullValue());
    assertThat(userPreferences, hasSize(1));

    var userPreferencesResponse = userPreferences.getFirst();

    var details = userPreferencesResponse.getDetails();
    assertThat(details, notNullValue());
    assertEquals("accountName", details.getAccountName());

    var settings = userPreferencesResponse.getSettings();
    assertThat(settings, notNullValue());
    assertThat(settings, hasSize(1));

    verify(worldlineClient).getUserPreferences(any(), any());
  }

  @Test
  void getAppLookup__ShouldReturnOk() {
    // Arrange
    var getAppLookupRequest = createGetAppLookupRequest();

    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(worldlineClient.getAppLookup(any(), any())).thenReturn(createGetAppLookupResponseWL());

    // Act
    var getAppLookupResponse = payAppOutPort.getAppLookup(getAppLookupRequest, CLIENT_IP);

    // Assert
    assertThat(getAppLookupResponse, notNullValue());
    verify(worldlineClient).getAppLookup(any(), any());
  }

  @Test
  void updateAppContactDetails__ShouldReturnOk() {
    // Arrange
    var jwtTokenClaims = createJwtTokenClaims();
    jwtTokenClaims.setEmail("john.doe@email.com");

    when(updateAppContactDetailsRequestMapper.toModel(any(UpdateAppContactDetailsRequest.class)))
        .thenReturn(createWLAppContactDetailsUpdateRequestDto());
    when(worldlineClient.appContactDetailsUpdateWorldline(any(String.class), any(
        WLAppContactDetailsUpdateRequestDto.class), any(WorldlineHeadersDto.class))).thenReturn(
        createWLAppContactDetailsUpdateResponseDto());
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    when(cdhClient.updateApplication(any(UpdateApplicationRequest.class), any(String.class)))
        .thenReturn(createUpdateApplicationResponse());
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(updateApplicationRequestMapper.toModel(any(ApplicationResponse.class)))
        .thenReturn(createUpdateApplicationRequest());

    // Act
    var updateAppContactDetailsResponse = payAppOutPort.updateAppContactDetails(
        createUpdateAppContactDetailsRequest(), jwtTokenClaims, CLIENT_IP);

    // Assert
    assertThat(updateAppContactDetailsResponse, notNullValue());
    assertEquals(200, updateAppContactDetailsResponse.getStatus());
    verify(updateAppContactDetailsRequestMapper).toModel(any(UpdateAppContactDetailsRequest.class));
    verify(worldlineClient).appContactDetailsUpdateWorldline(any(String.class), any(
        WLAppContactDetailsUpdateRequestDto.class), any(WorldlineHeadersDto.class));
    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verify(cdhClient).updateApplication(any(UpdateApplicationRequest.class), any(String.class));
  }

  @Test
  void updateAppContactDetails__ShouldThrowException() {
    // Arrange
    var expectedMessage =
        "Application not found in CDH: applicationId=123 ; applicationGuid=123";

    var jwtTokenClaims = createJwtTokenClaims();
    jwtTokenClaims.setEmail("john.doe@email.com");
    var updateAppContactDetailsRequest = createUpdateAppContactDetailsRequest();

    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(null);

    // Act
    var actual = assertThrows(CdhNotFoundException.class,
        () -> payAppOutPort.updateAppContactDetails(
            updateAppContactDetailsRequest, jwtTokenClaims, CLIENT_IP));

    // Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(CDH_APPLICATION_NOT_FOUND_ERROR.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(CDH_APPLICATION_NOT_FOUND_ERROR.getCode()));
  }
  @Test
  void updateAppContactDetails__ShouldThrowAccessException() {
    // Arrange
    var expectedMessage =
        "User does not have access to application with applicationId = 123 and applicationGuid = 1234-f123";

    var jwtTokenClaims = createJwtTokenClaims();
    jwtTokenClaims.setEmail("john.doe@email.com");
    jwtTokenClaims.setEmployeeId("312");
    var updateAppContactDetailsRequest = createUpdateAppContactDetailsRequest();

    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());

    // Act
    var actual = assertThrows(ApplicationAccessException.class,
        () -> payAppOutPort.updateAppContactDetails(
            updateAppContactDetailsRequest, jwtTokenClaims, CLIENT_IP));

    // Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(APPLICATION_ACCESS_DENIED.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(APPLICATION_ACCESS_DENIED.getCode()));
  }

  @Test
  void updateAppCompanyDetails__ShouldReturnOk() {
    // Arrange
    var userAccount = Optional.of(Account.builder()
        .email("john.doe@email.com")
        .companyId(COMPANY_ID)
        .bartId(String.valueOf(EMPLOYEE_ID))
        .bartEmployeeId("123456")
        .build());

    when(updateAppCompanyDetailsRequestMapper.toModel(any(AppCompanyDetails.class)))
        .thenReturn(createWLAppCompanyDetailsUpdateRequestDto());
    when(worldlineClient.appCompanyDetailsUpdate(any(String.class), any(
        WLAppCompanyDetailsUpdateRequestDto.class), any(WorldlineHeadersDto.class))).thenReturn(
        createWLAppCompanyDetailsUpdateResponseDto());
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    when(cdhClient.updateApplication(any(UpdateApplicationRequest.class), any(String.class)))
        .thenReturn(createUpdateApplicationResponse());
    when(updateApplicationRequestMapper.toModel(any(ApplicationResponse.class)))
        .thenReturn(createUpdateApplicationRequest());
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());

    // Act
    var updateAppCompanyDetailsResponse = payAppOutPort.updateAppCompanyDetails(
        createUpdateAppCompanyDetailsRequest(), CLIENT_IP);

    // Assert
    assertThat(updateAppCompanyDetailsResponse, notNullValue());
    assertEquals(200, updateAppCompanyDetailsResponse.getStatus());
    verify(updateAppCompanyDetailsRequestMapper).toModel(any(AppCompanyDetails.class));
    verify(worldlineClient).appCompanyDetailsUpdate(any(String.class), any(
        WLAppCompanyDetailsUpdateRequestDto.class), any(WorldlineHeadersDto.class));
    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verify(cdhClient).updateApplication(any(UpdateApplicationRequest.class), any(String.class));
  }

  @Test
  void updateAppCompanyDetails__ShouldThrowError() {
    // Arrange
    var userAccount = createAccount();


    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());

    // Act
    var actual = assertThrows(ApplicationAccessException.class,
        () -> payAppOutPort.updateAppCompanyDetails(
            createUpdateAppCompanyDetailsRequest(),  CLIENT_IP));


    // Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(APPLICATION_ACCESS_DENIED.getMessage()));
    assertThat(actual.getMessage(), is("User does not have access to application with applicationId = 123 and applicationGuid = 1234-f123"));
    assertThat(actual.getErrorCode(), is(APPLICATION_ACCESS_DENIED.getCode()));
  }

  @Test
  void updateAppCompanyDetailsForDECompany__ShouldReturnOk() {
    // Arrange
    var userAccount = Optional.of(Account.builder()
        .email("john.doe@email.com")
        .companyId(COMPANY_ID)
        .bartId(String.valueOf(EMPLOYEE_ID))
        .bartEmployeeId("123456")
        .build());

    when(updateAppCompanyDetailsRequestMapper.toModel(any(AppCompanyDetails.class)))
        .thenReturn(createWLAppCompanyDetailsUpdateRequestDto());
    when(worldlineClient.appCompanyDetailsUpdate(any(String.class), any(
        WLAppCompanyDetailsUpdateRequestDto.class), any(WorldlineHeadersDto.class))).thenReturn(
        createWLAppCompanyDetailsUpdateResponseDto());
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    when(cdhClient.updateApplication(any(UpdateApplicationRequest.class), any(String.class)))
        .thenReturn(createUpdateApplicationResponse());
    when(updateApplicationRequestMapper.toModel(any(ApplicationResponse.class)))
        .thenReturn(createUpdateApplicationRequest());
    when(worldlineProperties.getDe()).thenReturn(createPropertiesDE());

    // Act
    var updateAppCompanyDetailsResponse = payAppOutPort.updateAppCompanyDetails(
        createUpdateAppCompanyDetailsRequestForDECompany(), CLIENT_IP);

    // Assert
    assertThat(updateAppCompanyDetailsResponse, notNullValue());
    assertEquals(200, updateAppCompanyDetailsResponse.getStatus());
    verify(updateAppCompanyDetailsRequestMapper).toModel(any(AppCompanyDetails.class));
    verify(worldlineClient).appCompanyDetailsUpdate(any(String.class), any(
        WLAppCompanyDetailsUpdateRequestDto.class), any(WorldlineHeadersDto.class));
    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verify(cdhClient).updateApplication(any(UpdateApplicationRequest.class), any(String.class));
  }



  @Test
  void updateAppCompanyDetails__ShouldThrowException() {
    // Arrange
    var expectedMessage =
        "Application not found in CDH: applicationId=1122 ; applicationGuid=123456";
    var updateAppCompanyDetailsRequest = createUpdateAppCompanyDetailsRequest();

    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(null);

    // Act
    var actual = assertThrows(CdhNotFoundException.class,
        () -> payAppOutPort.updateAppCompanyDetails(
            updateAppCompanyDetailsRequest, CLIENT_IP));

    // Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(CDH_APPLICATION_NOT_FOUND_ERROR.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(CDH_APPLICATION_NOT_FOUND_ERROR.getCode()));
  }

  @Test
  void deleteApplication_ShouldCallCdhClientWithCancelledStage() {
    // Arrange
    var deleteRequest = DeletePayApplicationRequest.builder().applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID).scheme(Scheme.GB).build();
    var applicationResponse = createApplicationResponse();
    var clientIp = "1.2.3.4";
    var cdhResponse = UpdateApplicationStatusResponse.builder().status(200).message("Success")
        .build();
    when(cdhClient.fetchApplicationsByUser(any(JwtTokenClaims.class))).thenReturn(
        List.of(applicationResponse));
    when(cdhClient.updateApplicationStatus(any(), any())).thenReturn(cdhResponse);
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());

    // Act
    var response = payAppOutPort.deleteApplication(deleteRequest, createJwtTokenClaims(), clientIp);

    // Capture the arguments
    ArgumentCaptor<UpdateApplicationStatusRequest> cdhRequestCaptor = ArgumentCaptor.forClass(
        UpdateApplicationStatusRequest.class);
    ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).updateApplicationStatus(cdhRequestCaptor.capture(), emailCaptor.capture());

    // Assert the captured arguments
    UpdateApplicationStatusRequest capturedCdhRequest = cdhRequestCaptor.getValue();
    assertEquals(APPLICATION_ID, capturedCdhRequest.getApplicationId());
    assertEquals("Cancelled", capturedCdhRequest.getStage());

    String capturedEmail = emailCaptor.getValue();
    assertEquals(EMAIL, capturedEmail);

    // Verify Worldline client call is made
    verify(worldlineClient).appCancelWorldline(any(String.class), any(WorldlineHeadersDto.class),
        any(WorldlineAppCancelRequestDto.class));

    assertThat(response, notNullValue());
    assertEquals(200, response.getStatus());
    assertEquals("Successful application deletion.", response.getMessage());
  }

  @Test
  void deleteApplication_ShouldCallWorldlineClientWithCorrectParams() {
    // Arrange
    var deleteRequest = DeletePayApplicationRequest.builder().applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID).scheme(Scheme.GB).build();
    var applicationResponse = createApplicationResponse();
    var clientIp = "1.2.3.4";
    var cdhResponse = UpdateApplicationStatusResponse.builder().status(200).message("Success")
        .build();
    when(cdhClient.fetchApplicationsByUser(any(JwtTokenClaims.class))).thenReturn(
        List.of(applicationResponse));
    when(cdhClient.updateApplicationStatus(any(), any())).thenReturn(cdhResponse);
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());

    // Act
    var response = payAppOutPort.deleteApplication(deleteRequest, createJwtTokenClaims(), clientIp);

    // Verify CDH client is called
    verify(cdhClient).updateApplicationStatus(any(), any());

    // Capture the arguments for Worldline client call
    ArgumentCaptor<String> guidCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<WorldlineHeadersDto> headersCaptor = ArgumentCaptor.forClass(
        WorldlineHeadersDto.class);
    ArgumentCaptor<WorldlineAppCancelRequestDto> cancelRequestCaptor = ArgumentCaptor.forClass(
        WorldlineAppCancelRequestDto.class);
    verify(worldlineClient).appCancelWorldline(guidCaptor.capture(), headersCaptor.capture(),
        cancelRequestCaptor.capture());

    // Assert the captured arguments for Worldline client
    assertEquals(APPLICATION_GUID, guidCaptor.getValue());
    assertEquals("User triggered cancel.", cancelRequestCaptor.getValue().getReasonDescription());

    assertThat(response, notNullValue());
    assertEquals(200, response.getStatus());
    assertEquals("Successful application deletion.", response.getMessage());
  }

  @Test
  void lookupCompanyDetails_ShouldCallWorldlineClientWithCorrectParams() {
    // Arrange
    var companyDetailsRequest = AppCompanyDetailsRequest.builder()
        .companyRegistrationNumber("dummy-company").scheme(Scheme.GB).build();
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(worldlineClient.lookupCompanyDetailsWorldline(anyString(),
        any(WorldlineHeadersDto.class))).thenReturn(createWLAppCompanyDetailsLookupResponseDto());
    when(appCompanyDetailsLookupMapper.toModel(any())).thenReturn(createCompanyDetails());

    // Act
    var response = payAppOutPort.lookupCompanyDetails(companyDetailsRequest, CLIENT_IP);

    // Assert
    assertThat(response, notNullValue());
    assertEquals(1, response.getData().size());
    assertEquals("Company Name", response.getData().getFirst().getCompanyName());
    assertEquals("123", response.getData().getFirst().getCreditAgencyReference());
    verify(worldlineClient).lookupCompanyDetailsWorldline(anyString(),
        any(WorldlineHeadersDto.class));
  }

  @Test
  void lookupCompanyDetails_ShouldThrowException() {
    // Arrange
    var companyDetailsRequest = AppCompanyDetailsRequest.builder()
        .companyRegistrationNumber("dummy-company").scheme(Scheme.GB).build();
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(
        worldlineClient.lookupCompanyDetailsWorldline(anyString(), any(WorldlineHeadersDto.class))).
        thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_COMPANY_DETAILS_LOOKUP_ERROR,
            "Error when calling WorldLine REST API: "));
    // Act
    var response = assertThrows(WorldlineResponseException.class,
        () -> payAppOutPort.lookupCompanyDetails(companyDetailsRequest, CLIENT_IP));

    // Assert
    assertThat(response, notNullValue());
    assertThat(response.getErrorCode(),
        is(ErrorCode.WORLDLINE_COMPANY_DETAILS_LOOKUP_ERROR.getCode()));
    verify(worldlineClient).lookupCompanyDetailsWorldline(anyString(),
        any(WorldlineHeadersDto.class));
  }

  @Test
  void deleteApplication_ShouldThrowException_WhenUserDoesNotHaveAccessToApplication() {
    // Arrange
    var clientIp = "1.2.3.4";
    var deleteRequest = DeletePayApplicationRequest.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .scheme(Scheme.GB)
        .build();
    when(cdhClient.fetchApplicationsByUser(any(JwtTokenClaims.class))).thenReturn(List.of());
    var jwtTokenClaims = createJwtTokenClaims();

    // Act & Assert
    assertThrows(ApplicationAccessException.class,
        () -> payAppOutPort.deleteApplication(deleteRequest, jwtTokenClaims, clientIp));
    verifyNoInteractions(worldlineClient);
    verifyNoMoreInteractions(cdhClient);
  }

  @Test
  void applicationDetails_ShouldReturnOK() {
    // Arrange
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    when(worldlineClient.fetchWorldlineApplicationDetails(any(String.class), any())).thenReturn(
        createFetchApplicationDetailsResponseDto());
    when(applicationDetailsResponseMapper.toModel(any(), any()))
        .thenReturn(createApplicationDetailsResponse());
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());

    // Act
    var response = payAppOutPort.applicationDetails(createApplicationDetailsRequest(),
        createJwtTokenClaims(), CLIENT_IP);

    // Assert
    assertThat(response, notNullValue());
    assertEquals(APPLICATION_ID, response.applicationId());
    assertEquals(APPLICATION_GUID, response.applicationGuid());

    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verify(worldlineClient).fetchWorldlineApplicationDetails(anyString(), any());
  }

  @Test
  void addApplicationCard_ShouldReturnOk() {
    // Arrange
    when(worldlineClient.addApplicationCard(anyString(),  any(), any()))
        .thenReturn(createWLAddApplicationCardResponse());
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(addApplicationCardResponseMapper.toModel(any(WLAddApplicationCardResponseDto.class)))
        .thenReturn(createAddApplicationCardResponse());
    when(jwtUtils.hasRights(any())).thenReturn(true);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(createCustomJwtAuthenticationTokenTM());
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    // Act
    var response = payAppOutPort.addApplicationCard(createAddApplicationCardRequest(Scheme.GB), CLIENT_IP);

    // Assert
    assertThat(response, notNullValue());

    verify(worldlineClient).addApplicationCard(anyString(), any(), any());
  }

  @Test
  void addApplicationCard_ShouldThrowException() {
    // Arrange
    var expectedMessage =
        "User does not have rights to add card";
    when(jwtUtils.hasRights(any())).thenReturn(false);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(
        createCustomJwtAuthenticationTokenTM());
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    // Act
    var actual = assertThrows(WorldlineResponseException.class,
        () -> payAppOutPort.addApplicationCard(createAddApplicationCardRequest(Scheme.GB),
            CLIENT_IP));

    // Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(USER_ROLE_MISMATCH_ERROR.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(USER_ROLE_MISMATCH_ERROR.getCode()));
  }

  @Test
  void addApplicationCardForDE_ShouldReturnOk() {
    // Arrange
    when(worldlineClient.addApplicationCard(anyString(), any(), any()))
        .thenReturn(createWLAddApplicationCardResponse());
    when(worldlineProperties.getDe()).thenReturn(createPropertiesDE());
    when(addApplicationCardResponseMapper.toModel(any(WLAddApplicationCardResponseDto.class)))
        .thenReturn(createAddApplicationCardResponse());
    when(jwtUtils.hasRights(any())).thenReturn(true);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(
        createCustomJwtAuthenticationTokenTM());
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    // Act
    var response = payAppOutPort.addApplicationCard(createAddApplicationCardRequest(Scheme.DE),
        CLIENT_IP);

    // Assert
    assertThat(response, notNullValue());

    verify(worldlineClient).addApplicationCard(anyString(), any(), any());
  }

  @Test
  void addApplicationCard_ForExistentEmployee_ShouldReturnOk() {
    // Arrange
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(createCustomJwtAuthenticationTokenTM());
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createApplicationResponse());
    when(worldlineClient.addApplicationCard(anyString(),  any(), any()))
        .thenReturn(createWLAddApplicationCardResponse());
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(addApplicationCardResponseMapper.toModel(any(WLAddApplicationCardResponseDto.class)))
        .thenReturn(createAddApplicationCardResponse());
    when(jwtUtils.hasRights(any())).thenReturn(true);
    // Act
    var response = payAppOutPort.addApplicationCard(
        createAddApplicationCardForExistentEmployeeRequest(), CLIENT_IP);

    // Assert
    assertThat(response, notNullValue());

    verify(worldlineClient).addApplicationCard(anyString(), any(), any());
  }

  @Test
  void addApplicationCard_ForExistentEmployee_ShouldNotUpdateApplicationInCDH() {
    // Arrange
    when(worldlineClient.addApplicationCard(anyString(),  any(), any()))
        .thenReturn(createWLAddApplicationCardErrorResponse());
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(addApplicationCardResponseMapper.toModel(any(WLAddApplicationCardResponseDto.class)))
        .thenReturn(createAddApplicationCardResponse());
    when(jwtUtils.hasRights(any())).thenReturn(true);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(createCustomJwtAuthenticationTokenTM());
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createApplicationResponse());
    // Act
    var response = payAppOutPort.addApplicationCard(
        createAddApplicationCardForExistentEmployeeRequest(), CLIENT_IP);

    // Assert
    assertThat(response, notNullValue());

    verify(worldlineClient).addApplicationCard(anyString(), any(), any());
  }

  @Test
  void shareApp_ShouldReturnOK() {
    // Arrange
    var userAccount = createAccount();

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    when(cdhClient.updateApplication(any(UpdateApplicationRequest.class), any(String.class)))
        .thenReturn(createUpdateApplicationResponse());
    when(updateApplicationRequestMapper.toModel(any(ApplicationResponse.class)))
        .thenReturn(createUpdateApplicationRequest());
    when(cdhAdapterClient.getEmployees(any(), anyString()))
        .thenReturn(createEmployeesResponse());
    Cache cache = mock(Cache.class);
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(cache);

    // Act
    var shareAppResponse = payAppOutPort.shareApp(createShareAppRequest());

    // Assert
    assertThat(shareAppResponse, notNullValue());
    assertEquals(200, shareAppResponse.getStatus());
    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verify(cdhClient).updateApplication(any(UpdateApplicationRequest.class), any(String.class));
  }

  @Test
  void shareApp_ShouldThrowException() {
    // Arrange
    var expectedMessage =
        "Application not found in CDH: applicationId=1122 ; applicationGuid=123456";
    var userAccount = createAccount();
    var shareAppRequest = createShareAppRequest();

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(null);
    when(cdhAdapterClient.getEmployees(any(), anyString()))
        .thenReturn(createEmployeesResponse());
    Cache cache = mock(Cache.class);
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(cache);

    // Act
    var actual = assertThrows(CdhNotFoundException.class,
        () -> payAppOutPort.shareApp(shareAppRequest));

    // Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(CDH_APPLICATION_NOT_FOUND_ERROR.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(CDH_APPLICATION_NOT_FOUND_ERROR.getCode()));
  }

  @Test
  void shareApp_ShouldThrowEmployeeNotInCompanyException() {
    // Arrange
    var expectedMessage = "Employee not found within your company";
    var userAccount = createAccount();
    var shareAppRequest = createShareAppRequest();

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);

    // Act
    var actual = assertThrows(EmployeeNotInCompanyException.class,
        () -> payAppOutPort.shareApp(shareAppRequest));

    // Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(EMPLOYEE_NOT_IN_COMPANY_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(EMPLOYEE_NOT_IN_COMPANY_EXCEPTION.getCode()));
  }

  @Test
  void shareApp_InvalidRequestShouldThrowEmployeeNotInCompanyException() {
    // Arrange
    var expectedMessage = "Employee not found within your company";
    var userAccount = createAccount();
    var shareAppRequest = ShareAppRequest.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .email("invalid-email") // Invalid email
        .fullName("Invalid User")
        .build();

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);

    // Act
    var actual = assertThrows(EmployeeNotInCompanyException.class,
        () -> payAppOutPort.shareApp(shareAppRequest));

    // Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(EMPLOYEE_NOT_IN_COMPANY_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(EMPLOYEE_NOT_IN_COMPANY_EXCEPTION.getCode()));
  }

  @Test
  void removeParticipant_ShouldCallCdhClient() {
    // Arrange
    var userAccount = createAccount();
    var removeParticipantRequest = new RemoveParticipantRequest(
        APPLICATION_ID, APPLICATION_GUID, 2);

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    when(cdhClient.updateApplication(any(UpdateApplicationRequest.class), any(String.class)))
        .thenReturn(createUpdateApplicationResponse());
    when(updateApplicationRequestMapper.toModel(any(ApplicationResponse.class)))
        .thenReturn(createUpdateApplicationRequest());
    // Act
    payAppOutPort.removeParticipant(removeParticipantRequest);

    // Capture the arguments
    ArgumentCaptor<UpdateApplicationRequest> cdhRequestCaptor = ArgumentCaptor.forClass(
        UpdateApplicationRequest.class);
    verify(cdhClient).updateApplication(cdhRequestCaptor.capture(), any(String.class));

    // Assert the captured arguments
    UpdateApplicationRequest capturedCdhRequest = cdhRequestCaptor.getValue();
    assertEquals(0, capturedCdhRequest.getParticipants().size());

    // Assert
    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
  }

  @Test
  void removeParticipant_ShouldThrowExceptionWhenApplicationNotFound() {
    // Arrange
    var userAccount = createAccount();
    var removeParticipantRequest = new RemoveParticipantRequest(
        APPLICATION_ID, APPLICATION_GUID, 2);

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(null);

    // Act & Assert
    assertThrows(CdhNotFoundException.class,
        () -> payAppOutPort.removeParticipant(removeParticipantRequest));

    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verifyNoMoreInteractions(cdhClient);
  }

  @Test
  void deleteApplicationCard_ShouldCallCdhAndWorldlineClients() {
    // Arrange
    var appGuid = "app-guid";
    var appId = "app-id";
    var cardGuid = "card-guid";
    var deleteRequest = new DeleteCardRequest(appGuid, appId, cardGuid);
    var jwtTokenClaims = createJwtTokenClaims();
    var applicationResponse = createApplicationResponse();
    applicationResponse.setApplicationId(appId);
    applicationResponse.setApplicationGuid(appGuid);

    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(cdhClient.fetchApplication(deleteRequest.applicationId(), deleteRequest.applicationGuid(),
        jwtTokenClaims.getEmail()))
        .thenReturn(applicationResponse);
    when(cdhClient.fetchApplicationsByUser(any(JwtTokenClaims.class))).thenReturn(
        List.of(applicationResponse));

    // Act
    payAppOutPort.deleteApplicationCard(deleteRequest, jwtTokenClaims, CLIENT_IP);

    // Assert
    verify(cdhClient).fetchApplication(deleteRequest.applicationId(),
        deleteRequest.applicationGuid(), jwtTokenClaims.getEmail());
    verify(cdhClient).updateApplicationCardHolders(any(UpdateApplicationCardHoldersRequest.class),
        eq(jwtTokenClaims.getEmail()));
    verify(worldlineClient).deleteApplicationCard(eq(deleteRequest.applicationGuid()),
        eq(deleteRequest.cardGuid()), any(WorldlineHeadersDto.class));
  }

  @Test
  void deleteApplicationCard_ShouldThrowExceptionWhenApplicationNotFound() {
    // Arrange
    var appGuid = "app-guid";
    var appId = "app-id";
    var cardGuid = "card-guid";
    var deleteRequest = new DeleteCardRequest(appGuid, appId, cardGuid);
    var jwtTokenClaims = createJwtTokenClaims();
    var applicationResponse = createApplicationResponse();
    applicationResponse.setApplicationId(appId);
    applicationResponse.setApplicationGuid(appGuid);

    when(cdhClient.fetchApplication(deleteRequest.applicationId(), deleteRequest.applicationGuid(),
        jwtTokenClaims.getEmail()))
        .thenReturn(null);
    when(cdhClient.fetchApplicationsByUser(any(JwtTokenClaims.class))).thenReturn(
        List.of(applicationResponse));

    // Act & Assert
    assertThrows(CdhNotFoundException.class, () ->
        payAppOutPort.deleteApplicationCard(deleteRequest, jwtTokenClaims, CLIENT_IP));

    verify(cdhClient).fetchApplication(deleteRequest.applicationId(),
        deleteRequest.applicationGuid(), jwtTokenClaims.getEmail());
    verifyNoMoreInteractions(cdhClient);
    verifyNoInteractions(worldlineClient);
  }

  @Test
  void deleteApplicationCard_ShouldThrowException_WhenAccessValidationFails() {
    // Arrange
    var deleteRequest = new DeleteCardRequest("appGuid", "appId", "cardGuid");
    var jwtTokenClaims = createJwtTokenClaims();

    when(cdhClient.fetchApplicationsByUser(any(JwtTokenClaims.class))).thenReturn(
        List.of());

    // Act & Assert
    assertThrows(ApplicationAccessException.class, () ->
        payAppOutPort.deleteApplicationCard(deleteRequest, jwtTokenClaims, CLIENT_IP));

    verify(cdhClient).fetchApplicationsByUser(any(JwtTokenClaims.class));
  }

  @Test
  void deleteApplicationCard_ShouldRemoveCardHolderFromCdhRequest() {
    // Arrange
    var appGuid = "app-guid";
    var appId = "app-id";
    var cardGuid = "card-guid-to-remove";
    var deleteRequest = new DeleteCardRequest(appGuid, appId, cardGuid);
    var jwtTokenClaims = createJwtTokenClaims();
    var applicationResponse = createApplicationResponse();
    applicationResponse.setApplicationId(appId);
    applicationResponse.setApplicationGuid(appGuid);
    applicationResponse.setCardHolders(List.of(
        CardHolder.builder().userGuid("card-guid-to-remove").build(),
        CardHolder.builder().userGuid("other-card-guid").build()
    ));

    when(cdhClient.fetchApplicationsByUser(any(JwtTokenClaims.class))).thenReturn(
        List.of(applicationResponse));
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(cdhClient.fetchApplication(deleteRequest.applicationId(), deleteRequest.applicationGuid(),
        jwtTokenClaims.getEmail()))
        .thenReturn(applicationResponse);

    // Act
    payAppOutPort.deleteApplicationCard(deleteRequest, jwtTokenClaims, CLIENT_IP);

    // Assert
    ArgumentCaptor<UpdateApplicationCardHoldersRequest> captor = ArgumentCaptor.forClass(
        UpdateApplicationCardHoldersRequest.class);
    verify(cdhClient).updateApplicationCardHolders(captor.capture(), eq(jwtTokenClaims.getEmail()));

    var capturedRequest = captor.getValue();
    assertThat(capturedRequest.getCardHolders(), hasSize(1));
    assertThat(capturedRequest.getCardHolders().getFirst().getUserGuid(), is("other-card-guid"));
  }

  @Test
  void getApplicationCards_ShouldReturnOK() {
    // Arrange
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(worldlineClient.getAppCards(anyString(), anyInt(), anyInt(), any())).thenReturn(createGetAppCardsResponseDto());
    when(getApplicationCardsResponseMapper.toModel(any(GetAppCardsResponseDto.class)))
        .thenReturn(createGetAppCardsResponse());

    // Act
    var getApplicationCardsResponse = payAppOutPort.getApplicationCards(
        createGetAppCardsRequest(), CLIENT_IP);

    // Assert
    assertThat(getApplicationCardsResponse, notNullValue());
    assertEquals(1, getApplicationCardsResponse.getAppCards().size());

    verify(worldlineClient).getAppCards(anyString(), anyInt(), anyInt(), any());
    verify(getApplicationCardsResponseMapper).toModel(any(GetAppCardsResponseDto.class));
  }


  @Test
  void submitApplication_ShouldReturnOk() {
    // Arrange
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(submitApplicationRequestMapper.toModel(any())).thenReturn(createSubmitApplicationRequestDto());
    when(worldlineClient.submitApplication(any(), any())).thenReturn(createSubmitApplicationDto());
    when(submitApplicationResponseMapper.toModel(any())).thenReturn(createSubmitApplicationResponse());

    // Act
    var submitApplicationResponse = payAppOutPort.submitApplication(
        createSubmitApplicationRequest(), CLIENT_IP);

    // Assert
    assertThat(submitApplicationResponse, notNullValue());
    assertThat(submitApplicationResponse.getMessage(), is(SUCCESS_MESSAGE));

    verify(worldlineClient).submitApplication(any(), any());
    verify(submitApplicationRequestMapper).toModel(any(SubmitApplicationRequest.class));
    verify(submitApplicationResponseMapper).toModel(any(SubmitApplicationDto.class));
  }

  @Test
  void submitApplication_ShouldThrowError() {
    // Arrange
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(submitApplicationRequestMapper.toModel(any())).thenReturn(createSubmitApplicationRequestDto());
    when(worldlineClient.submitApplication(any(), any())).thenReturn(createInvalidSubmitApplicationDto());

    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> payAppOutPort.submitApplication(createSubmitApplicationRequest(), CLIENT_IP));
  }

  @Test
  void appPreCheck_ShouldReturnOK() {
    // Arrange
    var properties = createPropertiesGB();
    String email = "test@example.com";
    Boolean isTetheredUser = true;
    String applicationGuid = "123456";
    var appPreCheckRequest = new AppPreCheckRequest(email, Scheme.GB);
    WorldlineAppPreCheckResponseDto responseDto = new WorldlineAppPreCheckResponseDto("200",
        new AppPreCheckDataDto(isTetheredUser, applicationGuid), null);
    AppPreCheckResponse response = new AppPreCheckResponse(isTetheredUser, applicationGuid);

    when(worldlineProperties.getGb()).thenReturn(properties);
    ArgumentCaptor<WorldlineAppPreCheckRequestDto> wlRequestCaptor = ArgumentCaptor.forClass(
        WorldlineAppPreCheckRequestDto.class);
    when(worldlineClient.appPreCheck(wlRequestCaptor.capture(), any(WorldlineHeadersDto.class)))
        .thenReturn(responseDto);
    when(appPreCheckResponseMapper.toModel(responseDto))
        .thenReturn(response);

    // Act
    var getApplicationCardsResponse = payAppOutPort.appPreCheck(appPreCheckRequest, CLIENT_IP);

    // Assert
    assertThat(getApplicationCardsResponse, notNullValue());
    assertSame(getApplicationCardsResponse, response);
    WorldlineAppPreCheckRequestDto wlRequest = wlRequestCaptor.getValue();
    assertEquals(email, wlRequest.email());
  }

  @Test
  void directDebit_ShouldCallCdhClient_WhenByPost() {
    // Arrange
    var userAccount = createAccount();
    var directDebitRequest = new DirectDebitRequest(
        APPLICATION_ID, APPLICATION_GUID, "resumeUrl", DirectDebitOption.BY_POST);

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    when(cdhClient.updateApplication(any(UpdateApplicationRequest.class), any(String.class)))
        .thenReturn(createUpdateApplicationResponse());
    when(updateApplicationRequestMapper.toModel(any(ApplicationResponse.class)))
        .thenReturn(createUpdateApplicationRequest());
    // Act
    var directDebitResponse = payAppOutPort.directDebit(directDebitRequest, CLIENT_IP);

    // Assert
    assertThat(directDebitResponse, notNullValue());

    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verify(cdhClient).updateApplication(any(UpdateApplicationRequest.class), any(String.class));
  }

  @Test
  void directDebit_ShouldCallCdhClient_WhenDirect() {
    // Arrange
    var userAccount = createAccount();
    var directDebitRequest = new DirectDebitRequest(
        APPLICATION_ID, APPLICATION_GUID, "resumeUrl", DirectDebitOption.DIRECT);

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    when(cdhClient.updateApplication(any(UpdateApplicationRequest.class), any(String.class)))
        .thenReturn(createUpdateApplicationResponse());
    when(updateApplicationRequestMapper.toModel(any(ApplicationResponse.class)))
        .thenReturn(createUpdateApplicationRequest());
    // Act
    var directDebitResponse = payAppOutPort.directDebit(directDebitRequest, CLIENT_IP);

    // Assert
    assertThat(directDebitResponse, notNullValue());

    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verify(cdhClient).updateApplication(any(UpdateApplicationRequest.class), any(String.class));
  }

  @Test
  void directDebit_ShouldCallWorldLineAndCdh_WhenDirect() {
    // Arrange
    var userAccount = createAccount();
    var directDebitRequest = new DirectDebitRequest(
        APPLICATION_ID, APPLICATION_GUID, "resumeUrl", DirectDebitOption.DIRECT);

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponseForDirectDebit());
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(worldlineClient.hostedPageAppInit(anyString(),  any()))
        .thenReturn(createWLHostedPageAppInitResponseDto());
    when(cdhClient.updateApplication(any(UpdateApplicationRequest.class), any(String.class)))
        .thenReturn(createUpdateApplicationResponse());
    when(updateApplicationRequestMapper.toModel(any(ApplicationResponse.class)))
        .thenReturn(createUpdateApplicationRequest());
    // Act
    var directDebitResponse = payAppOutPort.directDebit(directDebitRequest, CLIENT_IP);

    // Assert
    assertThat(directDebitResponse, notNullValue());

    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verify(worldlineClient).hostedPageAppInit(anyString(), any());
    verify(cdhClient).updateApplication(any(UpdateApplicationRequest.class), any(String.class));
  }

  @Test
  void directDebit_WorldLineCallThrowError_WhenDirect() {
    // Arrange
    var userAccount = createAccount();
    var directDebitRequest = new DirectDebitRequest(
        APPLICATION_ID, APPLICATION_GUID, "resumeUrl", DirectDebitOption.DIRECT);

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponseForDirectDebit());
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(worldlineClient.hostedPageAppInit(anyString(), any()))
        .thenThrow(new WorldlineResponseException(ErrorCode.WORLDLINE_HOSTED_PAGE_APP_INIT_ERROR,
            "error"));
    when(cdhClient.updateApplication(any(UpdateApplicationRequest.class), any(String.class)))
        .thenReturn(createUpdateApplicationResponse());
    when(updateApplicationRequestMapper.toModel(any(ApplicationResponse.class)))
        .thenReturn(createUpdateApplicationRequest());
    // Act & Assert
    assertThrows(WorldlineResponseException.class,
        () -> payAppOutPort.directDebit(directDebitRequest, CLIENT_IP));

    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verify(worldlineClient).hostedPageAppInit(anyString(), any());
    verify(cdhClient).updateApplication(any(UpdateApplicationRequest.class), any(String.class));
  }

  @Test
  void directDebit_ShouldThrowExceptionWhenApplicationNotFound() {
    // Arrange
    var userAccount = createAccount();
    var directDebitRequest = new DirectDebitRequest(
        APPLICATION_ID, APPLICATION_GUID, "resumeUrl", DirectDebitOption.DIRECT);

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(userAccount);
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(null);

    // Act & Assert
    assertThrows(CdhNotFoundException.class,
        () -> payAppOutPort.directDebit(directDebitRequest, CLIENT_IP));

    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verifyNoMoreInteractions(cdhClient);
    verifyNoInteractions(worldlineClient);
  }

  @Test
  void updateResumeUrl_ShouldReturnOk() {
    // Arrange
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(createAccount());
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(createComplexApplicationResponse());
    when(cdhClient.updateApplication(any(UpdateApplicationRequest.class), any(String.class)))
        .thenReturn(createUpdateApplicationResponse());
    when(updateApplicationRequestMapper.toModel(any(ApplicationResponse.class)))
        .thenReturn(createUpdateApplicationRequest());

    // Act
    var response = payAppOutPort.updateResumeUrl(createUpdateResumeUrlRequest());

    // Assert
    assertThat(response, notNullValue());
    assertThat(response.status(), is(200));
    assertThat(response.message(), is("Application updated Successfully."));

    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verify(cdhClient).updateApplication(any(UpdateApplicationRequest.class), any(String.class));
  }

  @Test
  void updateResumeUrl_ShouldThrowExceptionWhenApplicationNotFound() {
    // Arrange
    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(createAccount());
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(null);

    // Act & Assert
    assertThrows(CdhNotFoundException.class,
        () -> payAppOutPort.updateResumeUrl(createUpdateResumeUrlRequest()));

    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
    verifyNoMoreInteractions(cdhClient);
  }

  @Test
  void shareApp_ShouldThrowException_WhenApplicationAlreadySharedWithParticipant() {
    // Arrange
    var shareAppRequest = ShareAppRequest.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .employeeId(EMPLOYEE_ID)
        .email(EMAIL)
        .fullName(FULL_NAME)
        .build();
    var applicationResponse = ApplicationResponse.builder()
        .participants(List.of(
            ApplicationParticipant.builder()
                .participantId(EMPLOYEE_ID)
                .build()
        ))
        .build();

    when(authenticatedUserService.getCurrentUserAccount())
        .thenReturn(Optional.of(Account.builder().email(EMAIL).companyId(COMPANY_ID).build()));
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(applicationResponse);
    when(cdhAdapterClient.getEmployees(any(), anyString()))
        .thenReturn(createEmployeesResponse());
    Cache cache = mock(Cache.class);
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(cache);

    // Act & Assert
    var exception = assertThrows(AppAlreadySharedException.class, () ->
        payAppOutPort.shareApp(shareAppRequest)
    );

    assertThat(exception.getErrorCode(), is(ErrorCode.APP_ALREADY_SHARED_WITH_PARTICIPANT.getCode()));
    assertThat(exception.getMessage(),
        is(String.format(APP_ALREADY_SHARED_MESSAGE, APPLICATION_ID, APPLICATION_GUID,
            EMPLOYEE_ID)));
  }

  @Test
  void shareApp_ShouldNotThrowException_WhenApplicationNotSharedWithParticipant() {
    // Arrange
    var shareAppRequest = ShareAppRequest.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .employeeId(EMPLOYEE_ID + 1)
        .email(EMAIL)
        .fullName(FULL_NAME)
        .build();
    var applicationResponse = ApplicationResponse.builder()
        .participants(List.of(
            ApplicationParticipant.builder()
                .participantId(EMPLOYEE_ID)
                .build()
        ))
        .build();

    when(authenticatedUserService.getCurrentUserAccount())
        .thenReturn(Optional.of(Account.builder().email(EMAIL).companyId(COMPANY_ID).build()));
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenReturn(applicationResponse);
    when(cdhClient.updateApplication(any(UpdateApplicationRequest.class),
        any(String.class))).thenReturn(createUpdateApplicationResponse());
    when(cdhAdapterClient.getEmployees(any(), anyString()))
        .thenReturn(GetEmployeesResponse.builder()
            .results(List.of(GetEmployeeResponse.builder()
                .bartEmployeeId(String.valueOf(EMPLOYEE_ID +1))
                .companyAccountId(COMPANY_ID)
                .build()))
            .build());
    Cache cache = mock(Cache.class);
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(cache);

    // Act
    var response = payAppOutPort.shareApp(shareAppRequest);

    // Assert
    assertThat(response, notNullValue());
    verify(cdhClient).fetchApplication(anyString(), anyString(), anyString());
  }

  @Test
  void shareApp_ShouldThrowException_WhenApplicationNotFound() {
    // Arrange
    var shareAppRequest = ShareAppRequest.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .employeeId(EMPLOYEE_ID)
        .email(EMAIL)
        .fullName(FULL_NAME)
        .build();

    when(authenticatedUserService.getCurrentUserAccount())
        .thenReturn(Optional.of(Account.builder().email(EMAIL).companyId(COMPANY_ID).build()));
    when(cdhClient.fetchApplication(anyString(), anyString(), anyString()))
        .thenThrow(new CdhNotFoundException(ErrorCode.CDH_APPLICATION_NOT_FOUND_ERROR, "Application not found"));
    when(cdhAdapterClient.getEmployees(any(), anyString()))
        .thenReturn(createEmployeesResponse());
    Cache cache = mock(Cache.class);
    when(cacheManager1Hour.getCache("ListApplicationsCache")).thenReturn(cache);

    // Act & Assert
    var exception = assertThrows(CdhNotFoundException.class, () ->
        payAppOutPort.shareApp(shareAppRequest)
    );

    assertThat(exception.getErrorCode(), is(ErrorCode.CDH_APPLICATION_NOT_FOUND_ERROR.getCode()));
    assertThat(exception.getMessage(), is("Application not found"));
  }

  ApplicationDetailsRequest createApplicationDetailsRequest() {
    return ApplicationDetailsRequest.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .scheme(Scheme.GB)
        .build();
  }

  @Test
  void getDdSepaFormStatus_ShouldReturnOK() {
    // Arrange
    when(worldlineProperties.getGb()).thenReturn(createPropertiesGB());
    when(worldlineClient.bankDetailsStatus(anyString(), any())).thenReturn(createWorldlineBankDetailsStatusResponseDto());

    // Act
    var getDdSepaFormStatusResponse = payAppOutPort.getDdSepaFormStatus(
        createGetDdSepaFormStatusRequest(), CLIENT_IP);

    // Assert
    assertThat(getDdSepaFormStatusResponse, notNullValue());
    assertEquals("Complete", getDdSepaFormStatusResponse.status());

    verify(worldlineClient).bankDetailsStatus(anyString(), any());
  }

  GetDdSepaFormStatusRequest createGetDdSepaFormStatusRequest() {
    return GetDdSepaFormStatusRequest.builder()
        .hostedPageGuid(HOSTED_PAGE_GUID)
        .scheme(Scheme.GB)
        .build();
  }

  WorldlineBankDetailsStatusResponseDto createWorldlineBankDetailsStatusResponseDto() {
    return WorldlineBankDetailsStatusResponseDto.builder()
        .responseCode("200")
        .data(BankDetailsStatusDataDto.builder()
            .status("Complete")
            .build())
        .errors(null)
        .build();
  }

  ApplicationDetailsResponse createApplicationDetailsResponse() {
    return ApplicationDetailsResponse.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .build();
  }

  ApplicationResponse createApplicationResponse() {
    return ApplicationResponse.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .accountName("AccountName")
        .resumeUrl("resumeUrl")
        .cardHolders(List.of(
            CardHolder.builder()
                .userGuid("userGuid")
                .employeeId(1)
                .build()))
        .build();
  }

  FetchApplicationDetailsResponseDto createFetchApplicationDetailsResponseDto() {
    return FetchApplicationDetailsResponseDto.builder()
        .data(AppDetailsDataDto.builder()
            .applicationDetails(
                AppDetailsDto.builder()
                    .status("PENDING")
                    .build())
            .companyDetails(
                uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.CompanyDetailsDto.builder()
                    .companyName("CompanyName")
                    .build())
            .build())
        .build();
  }

  WLAppInitResponseDto createWorldlineAppInitResponseDto() {
    return WLAppInitResponseDto.builder()
        .responseCode("200")
        .data(AppInitDataDto.builder()
            .applicationNumber("123")
            .applicationGUID("123")
            .build())
        .errors(null)
        .build();
  }

  StartApplicationResponse createStartApplicationResponse() {
    return StartApplicationResponse.builder()
        .status(200)
        .message("Application saved Successfully.")
        .build();
  }

  InitializeApplicationRequest createInitializeApplicationRequestDE() {
    return InitializeApplicationRequest.builder()
        .email("john.doe@email.com")
        .scheme(Scheme.DE)
        .incentiveCode("1234")
        .campaignCode("C123")
        .build();
  }

  InitializeApplicationRequest createInitializeApplicationRequestGB() {
    return InitializeApplicationRequest.builder()
        .email("john.doe@email.com")
        .scheme(Scheme.GB)
        .incentiveCode("1234")
        .campaignCode("C123")
        .build();
  }

  JwtTokenClaims createJwtTokenClaims() {
    JwtTokenClaims jwtTokenClaims = new JwtTokenClaims();
    jwtTokenClaims.setEmployeeId("1");
    jwtTokenClaims.setCompanyId("1");
    jwtTokenClaims.setEmail(EMAIL);

    return jwtTokenClaims;
  }

  PropertiesByLocation createPropertiesDE() {
    PropertiesByLocation deProperties = new PropertiesByLocation();
    deProperties.setCompanyNumber(91);
    deProperties.setCultureCode("de-DE");
    deProperties.setUsername("test");
    deProperties.setPassword("testP");
    return deProperties;
  }

  PropertiesByLocation createPropertiesGB() {
    PropertiesByLocation gbProperties = new PropertiesByLocation();
    gbProperties.setCompanyNumber(35);
    gbProperties.setCultureCode("en-GB");
    gbProperties.setUsername("test");
    gbProperties.setPassword("testP");
    return gbProperties;
  }

  ApplicationDetails createApplicationDetails() {
    return ApplicationDetails.builder()
        .status("PENDING")
        .accountName("AccountName")
        .applicationId("applicationId")
        .applicationGuid("applicationGuid")
        .resumeUrl("resumeUrl")
        .build();
  }

  WorldlineAppInitRequestDto createWorldlineAppInitRequestDto() {
    return WorldlineAppInitRequestDto.builder()
        .email("john.doe@email.com")
        .incentiveCode("1234")
        .campaignCode("C123")
        .build();
  }

  UpdateAppContactDetailsRequest createUpdateAppContactDetailsRequest() {
    return UpdateAppContactDetailsRequest.builder()
        .applicationGuid("123")
        .title("Mr")
        .foreName("John")
        .lastName("Doe")
        .position("Developer")
        .telephone("123")
        .email("john.doe@email.com")
        .scheme(Scheme.GB)
        .applicationId("123")
        .resumeUrl("resumeUrl")
        .build();
  }

  WLAppContactDetailsUpdateResponseDto createWLAppContactDetailsUpdateResponseDto() {
    return WLAppContactDetailsUpdateResponseDto.builder()
        .responseCode("200")
        .data("Success")
        .errors(null)
        .build();
  }

  ApplicationResponse createComplexApplicationResponse() {
    return ApplicationResponse.builder()
        .applicationId("123")
        .applicationNumber("123")
        .applicationGuid("1234-f123")
        .companyId(1)
        .startedDate("2025-02-28T10:33:11.064264Z")
        .scheme("GB")
        .updateDate("0001-01-01T00:00:00")
        .accountName("TemporaryAccountName")
        .stage("Incomplete Application")
        .resumeUrl(null)
        .submittedDate("0001-01-01T00:00:00")
        .activatedDate("0001-01-01T00:00:00")
        .participants(List.of(
            ApplicationParticipant.builder()
                .initiator(true)
                .participantId(1)
                .delegated(false)
                .terms(false)
                .directDebit(false)
                .email("john.doe@email.com")
                .shared("2025-02-28T10:33:11.064548Z")
                .name("Temporary Name")
                .build(),
            ApplicationParticipant.builder()
                .initiator(false)
                .participantId(123456)
                .delegated(false)
                .terms(false)
                .directDebit(false)
                .email("john.doe1@email.com")
                .shared("2025-02-28T10:33:11.064548Z")
                .name("Temporary Name1")
                .build()
        ))
        .cardHolders(null)
        .hostedPageGuid("hosted-page-guid")
        .directDebitOption("DIRECT")
        .created("2025-02-28T10:33:12.098656Z")
        .modified("0001-01-01T00:00:00")
        .build();
  }

  ApplicationResponse createComplexApplicationResponseForDirectDebit() {
    return ApplicationResponse.builder()
        .applicationId("123")
        .applicationNumber("123")
        .applicationGuid("1234-f123")
        .companyId(1)
        .startedDate("2025-02-28T10:33:11.064264Z")
        .scheme("GB")
        .updateDate("0001-01-01T00:00:00")
        .accountName("TemporaryAccountName")
        .stage("Incomplete Application")
        .resumeUrl(null)
        .submittedDate("0001-01-01T00:00:00")
        .activatedDate("0001-01-01T00:00:00")
        .participants(List.of(
            ApplicationParticipant.builder()
                .initiator(true)
                .participantId(1)
                .delegated(false)
                .terms(false)
                .directDebit(false)
                .email("john.doe@email.com")
                .shared("2025-02-28T10:33:11.064548Z")
                .name("Temporary Name")
                .build()))
        .cardHolders(null)
        .hostedPageGuid(null)
        .directDebitOption(null)
        .created("2025-02-28T10:33:12.098656Z")
        .modified("0001-01-01T00:00:00")
        .build();
  }

  UpdateApplicationResponse createUpdateApplicationResponse() {
    return UpdateApplicationResponse.builder()
        .status(200)
        .message("Application updated Successfully.")
        .build();
  }

  WLAppContactDetailsUpdateRequestDto createWLAppContactDetailsUpdateRequestDto() {
    return WLAppContactDetailsUpdateRequestDto.builder()
        .title("Mr")
        .foreName("John")
        .lastName("Doe")
        .position("Developer")
        .telephone("123")
        .email("john.doe@email.com")
        .build();
  }

  Participants createParticipants() {
    return Participants.builder()
        .initiator(true)
        .participantId(1)
        .delegated(false)
        .terms(false)
        .directdebit(false)
        .email("john.doe@email.com")
        .shared("2025-02-28T10:33:11.064548Z")
        .name("Temporary Name")
        .build();
  }

  List<CompanyDetails> createCompanyDetails() {
    return List.of(CompanyDetails.builder()
        .companyName("Company Name")
        .creditAgencyReference("123")
        .build());
  }

  WLAppCompanyDetailsLookupResponseDto createWLAppCompanyDetailsLookupResponseDto() {
    return WLAppCompanyDetailsLookupResponseDto.builder()
        .responseCode("200")
        .data(List.of(AppCompanyDetailsLookupDto.builder()
            .companyName("Company Name")
            .creditAgencyReference("123")
            .build()))
        .errors(null)
        .build();
  }

  GetAppLookupRequest createGetAppLookupRequest() {
    return GetAppLookupRequest.builder()
        .scheme(Scheme.GB)
        .lookupNames(List.of(LookupName.TITLE, LookupName.TRADING_STYLE))
        .build();
  }

  Map<String, List<String>> createGetAppLookupResponseWL() {
    return Map.of("title", List.of("Mr", "Mrs", "Miss", "Ms", "Doctor"),
        "tradingstyle", List.of("Charity", "Government", "Other"));
  }

  CompanyDetailsResponseDto createCompanyDetailsResponseDto() {

    CompanyDetailsDto companyDetailsDto = new CompanyDetailsDto();
    companyDetailsDto.setCompanyName("Company Name");

    CompanyDto companyDto = new CompanyDto();
    companyDto.setCompanyDetails(companyDetailsDto);

    CompanyDetailsResponseDto companyDetailsResponseDto = new CompanyDetailsResponseDto();
    companyDetailsResponseDto.setRequestedCompany(companyDto);

    return companyDetailsResponseDto;
  }

  WLAppCompanyDetailsUpdateRequestDto createWLAppCompanyDetailsUpdateRequestDto() {
    return WLAppCompanyDetailsUpdateRequestDto.builder()
        .companyName("Company Name")
        .estMonthlySpend("£4,000")
        .companyType("Partnership")
        .build();
  }

  WLAppCompanyDetailsUpdateResponseDto createWLAppCompanyDetailsUpdateResponseDto() {
    return WLAppCompanyDetailsUpdateResponseDto.builder()
        .responseCode("200")
        .data("Success")
        .errors(null)
        .build();
  }

  UpdateAppCompanyDetailsRequest createUpdateAppCompanyDetailsRequest() {
    return UpdateAppCompanyDetailsRequest.builder()
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .resumeUrl("http://resume.url")
        .scheme(Scheme.GB)
        .appCompanyDetails(
            AppCompanyDetails.builder()
                .companyName("Company Name")
                .estMonthlySpend("£4,000")
                .companyType("Partnership")
                .build())
        .build();
  }

  UpdateAppCompanyDetailsRequest createUpdateAppCompanyDetailsRequestForDECompany() {
    return UpdateAppCompanyDetailsRequest.builder()
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .resumeUrl("http://resume.url")
        .scheme(Scheme.DE)
        .appCompanyDetails(
            AppCompanyDetails.builder()
                .companyName("Company Name")
                .estMonthlySpend("£4,000")
                .companyType("Partnership")
                .build())
        .build();
  }

  Optional<Account> createAccount() {
    return Optional.of(Account.builder()
        .email("john.doe@email.com")
        .companyId(COMPANY_ID)
        .bartId(String.valueOf(EMPLOYEE_ID))
        .bartEmployeeId(String.valueOf(EMPLOYEE_ID))
        .build());
  }

  ShareAppRequest createShareAppRequest() {
    return ShareAppRequest.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .employeeId(EMPLOYEE_ID)
        .email("john.doe@email.com")
        .fullName("John Doe")
        .build();
  }

  List<Participants> createListParticipants() {
    ArrayList<Participants> participants = new ArrayList<>();
    participants.add(Participants.builder()
        .initiator(true)
        .participantId(2)
        .delegated(false)
        .terms(false)
        .directdebit(false)
        .email("john.doe@email.com")
        .shared("2025-02-28T10:33:11.064548Z")
        .name("Temporary Name")
        .build());
    return participants;
  }

  AddApplicationCardRequest createAddApplicationCardForExistentEmployeeRequest() {
    return AddApplicationCardRequest.builder()
        .applicationGuid(APPLICATION_GUID)
        .applicationId("applicationId")
        .employeeId(2121)
        .scheme(Scheme.GB)
        .cardDetails(AddApplicationCardDetails.builder()
            .myCard(false)
            .cardName("CardName")
            .cardLimit(1000)
            .title("Mr")
            .foreName("John")
            .lastName("Smith")
            .emailAddress("email@gmail.com")
            .build())
        .build();
  }

  CustomJwtAuthenticationToken createCustomJwtAuthenticationTokenTM() {
    var account = Account.builder()
        .accessLevel("SUPER")
        .email("email")
        .build();
    var jwt = Jwt.withTokenValue("token")
        .header("alg", "RS256")
        .header("typ", "JWT")
        .claim("sub", "user123")
        .claim("role", "USER")
        .build();
    return new CustomJwtAuthenticationToken(jwt, account);
  }


  AddApplicationCardResponse createAddApplicationCardResponse() {
    return AddApplicationCardResponse.builder()
        .cardGuid(CARD_GUID)
        .build();
  }

  WLAddApplicationCardResponseDto createWLAddApplicationCardResponse() {
    return WLAddApplicationCardResponseDto.builder()
        .responseCode("200")
        .data(WLAddApplicationCardDto.builder()
            .cardGuid(CARD_GUID)
            .build())
        .errors(null)
        .build();
  }

  WLAddApplicationCardResponseDto createWLAddApplicationCardErrorResponse() {
    return WLAddApplicationCardResponseDto.builder()
        .responseCode("400")
        .data(null)
        .errors(List.of(WLErrorDto.builder()
            .code("122")
            .message("Error message")
            .target("target")
            .build()))
        .build();
  }

  AddApplicationCardRequest createAddApplicationCardRequest(Scheme scheme) {
    return AddApplicationCardRequest.builder()
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .scheme(scheme)
        .cardDetails(AddApplicationCardDetails.builder()
            .myCard(true)
            .cardName("CardName")
            .cardLimit(1000)
            .build())
        .build();
  }



  GetAppCardsResponseDto createGetAppCardsResponseDto() {
    return GetAppCardsResponseDto.builder()
        .responseCode("200")
        .data(
            List.of(CardDetailsDto.builder()
                .cardName("Mr. John Doe")
                .myCard(false)
                .cardOwnerName("Mr John Doe")
                .creditLimit(CreditLimitDto.builder()
                    .value(100)
                    .currencyCode("826")
                    .build())
                .emailAddress("john.doe@email.com")
                .cardGuid("2e3962d6-c843-425a-b98d-8dd7bd3d42fd")
            .build()))
        .errors(null)
        .build();
  }

  GetAppCardsResponse createGetAppCardsResponse() {
    return GetAppCardsResponse.builder()
        .appCards(
            List.of(CardDetails.builder()
                .cardName("Mr. John Doe")
                .myCard(false)
                .cardOwnerName("Mr John Doe")
                .creditLimit(CreditLimit.builder()
                    .value(100)
                    .currencyCode("826")
                    .build())
                .emailAddress("john.doe@email.com")
                .cardGuid("2e3962d6-c843-425a-b98d-8dd7bd3d42fd")
                .build()))
        .build();
  }

  GetAppCardsRequest createGetAppCardsRequest() {
    return GetAppCardsRequest.builder()
        .applicationGuid(APPLICATION_GUID)
        .scheme(Scheme.GB)
        .page(1)
        .maxDisplayRows(10)
        .build();
  }

  SubmitApplicationRequest createSubmitApplicationRequest() {
    return SubmitApplicationRequest.builder()
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .hostedPageGuid("hostedPageGuid")
        .isDirectDebit(false)
        .termsAndConditionAccepted("Y")
        .registrationQuestion("registrationQuestion")
        .registrationAnswer("registrationAnswer")
        .build();
  }

  SubmitApplicationResponse createSubmitApplicationResponse() {
    return SubmitApplicationResponse.builder()
        .message(SUCCESS_MESSAGE)
        .build();
  }

  SubmitApplicationDto createSubmitApplicationDto() {
    return SubmitApplicationDto.builder()
        .responseCode("200")
        .data(SUCCESS_MESSAGE)
        .errors(null)
        .build();
  }

  SubmitApplicationDto createInvalidSubmitApplicationDto() {
    return SubmitApplicationDto.builder()
        .responseCode("422")
        .data(null)
        .errors(List.of(WLErrorDto.builder()
            .code("NotNullValidator")
            .message("RegistrationQuestion is required")
            .target("RegistrationQuestion")
            .build()))
        .build();
  }

  SubmitApplicationRequestDetailsDto createSubmitApplicationRequestDto() {
    return SubmitApplicationRequestDetailsDto.builder()
        .applicationGuid(APPLICATION_GUID)
        .hostedPageGuid("hostedPageGuid")
        .hotelBookingRole("hotelBookingRole")
        .isDirectDebit(false)
        .termsAndConditionAccepted("Y")
        .registrationQuestion("registrationQuestion")
        .registrationAnswer("registrationAnswer")
        .build();
  }

  WLHostedPageAppInitResponseDto createWLHostedPageAppInitResponseDto() {
    return WLHostedPageAppInitResponseDto.builder()
        .responseCode("200")
        .data(WLHostedPageAppInitDto.builder()
            .hostedPageGuid("hosted-page-guid")
            .build())
        .errors(null)
        .build();
  }

  UpdateResumeUrlRequest createUpdateResumeUrlRequest() {
    return new UpdateResumeUrlRequest(APPLICATION_ID, APPLICATION_GUID, "newResumeUrl");
  }

  UpdateApplicationRequest createUpdateApplicationRequest() {

    return UpdateApplicationRequest.builder()
        .applicationId(APPLICATION_ID)
        .resumeUrl("newResumeUrl")
        .participants(createListParticipants())
        .hostedPageGuid("hostedPageGuid")
        .updateDate("2025-02-28T10:33:11.064264Z")
        .build();
  }

  private GetEmployeesResponse createEmployeesResponse() {
    return GetEmployeesResponse.builder()
        .results(List.of(GetEmployeeResponse.builder()
            .bartEmployeeId(String.valueOf(EMPLOYEE_ID))
            .companyAccountId(COMPANY_ID)
            .globalCompanyId("globalCompanyId")
            .emailAddress(EMAIL)
            .build()))
        .build();
  }

}
