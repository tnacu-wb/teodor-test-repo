package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.payapp.ErrorCode.USER_EMAIL_MISMATCH_ERROR;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.payapp.domain.model.in.AddApplicationCardDetails;
import uk.co.whitbread.payapp.domain.model.in.AddApplicationCardRequest;
import uk.co.whitbread.payapp.domain.model.in.Address;
import uk.co.whitbread.payapp.domain.model.in.AppCompanyDetails;
import uk.co.whitbread.payapp.domain.model.in.AppCompanyDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.AppPreCheckRequest;
import uk.co.whitbread.payapp.domain.model.in.ApplicationDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.ContactInfo;
import uk.co.whitbread.payapp.domain.model.in.DeleteCardRequest;
import uk.co.whitbread.payapp.domain.model.in.DeletePayApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.DirectDebitOption;
import uk.co.whitbread.payapp.domain.model.in.DirectDebitRequest;
import uk.co.whitbread.payapp.domain.model.in.GetAppCardsRequest;
import uk.co.whitbread.payapp.domain.model.in.GetAppLookupRequest;
import uk.co.whitbread.payapp.domain.model.in.GetDdSepaFormStatusRequest;
import uk.co.whitbread.payapp.domain.model.in.GetUserPreferencesRequest;
import uk.co.whitbread.payapp.domain.model.in.InitializeApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.LookupName;
import uk.co.whitbread.payapp.domain.model.in.PartnerDetails;
import uk.co.whitbread.payapp.domain.model.in.RemoveParticipantRequest;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.domain.model.in.ShareAppRequest;
import uk.co.whitbread.payapp.domain.model.in.SubmitApplicationRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateAppCompanyDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateAppContactDetailsRequest;
import uk.co.whitbread.payapp.domain.model.in.UpdateResumeUrlRequest;
import uk.co.whitbread.payapp.domain.model.in.jwt.JwtTokenClaims;
import uk.co.whitbread.payapp.domain.model.out.AddApplicationCardResponse;
import uk.co.whitbread.payapp.domain.model.out.AppCompanyDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.AppPreCheckResponse;
import uk.co.whitbread.payapp.domain.model.out.ApplicationDetails;
import uk.co.whitbread.payapp.domain.model.out.ApplicationDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.CardDetails;
import uk.co.whitbread.payapp.domain.model.out.CompanyDetails;
import uk.co.whitbread.payapp.domain.model.out.CreditLimit;
import uk.co.whitbread.payapp.domain.model.out.DeletePayApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.DirectDebitResponse;
import uk.co.whitbread.payapp.domain.model.out.FetchApplicationsResponse;
import uk.co.whitbread.payapp.domain.model.out.GetAppCardsResponse;
import uk.co.whitbread.payapp.domain.model.out.GetDdSepaFormStatusResponse;
import uk.co.whitbread.payapp.domain.model.out.InitializeApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.ShareAppResponse;
import uk.co.whitbread.payapp.domain.model.out.SubmitApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateAppCompanyDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateAppContactDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateResumeUrlResponse;
import uk.co.whitbread.payapp.domain.ports.primary.PayAppInPort;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.exceptions.EmailMismatchException;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.CardDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out.CreditLimitDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.mapper.PayApplicationRequestDtoMapper;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.mapper.PayApplicationResponseDtoMapper;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.AddApplicationCardDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.AddApplicationCardRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.AddressDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.AppCompanyDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.AppCompanyDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.ApplicationDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.ContactInfoDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DeleteCardRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DeletePayApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.DirectDebitRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetAppCardsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetAppLookupRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetDdSepaFormStatusRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.GetUserPreferencesRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.InitializeApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.PartnerDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.RemoveParticipantRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.ShareAppRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.SubmitApplicationRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateAppCompanyDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateAppContactDetailsRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in.UpdateResumeUrlRequestDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AddApplicationCardResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AppCompanyDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.AppPreCheckResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.ApplicationDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.ApplicationDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.CompanyDetailsDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.DeletePayApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.DirectDebitResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.FetchApplicationsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.GetAppCardsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.GetDdSepaFormStatusResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.InitializeApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.ShareAppResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.SubmitApplicationResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateAppCompanyDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateAppContactDetailsResponseDto;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out.UpdateResumeUrlResponseDto;
import uk.co.whitbread.payapp.infrastructure.security.JwtUtils;
import uk.co.whitbread.payapp.infrastructure.util.Utils;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@ExtendWith(MockitoExtension.class)
class PayAppControllerTest {

  @InjectMocks
  private PayAppController payAppController;

  @Mock
  private PayApplicationRequestDtoMapper payApplicationRequestDtoMapper;

  @Mock
  private PayAppInPort payAppInPort;

  @Mock
  private PayApplicationResponseDtoMapper payApplicationResponseDtoMapper;

  @Mock
  private JwtUtils jwtUtils;

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private Utils utils;

  @Mock
  private AuthenticatedUserService authenticatedUserService;

  private static final String CLIENT_IP = "1.1.1.1";
  private static final String APPLICATION_GUID = "157e2060-6598-4079-aebd-e34671640322";
  private static final String APPLICATION_ID = "784529";
  private static final String CARD_GUID = "1234";
  private static final String SUCCESS_MESSAGE = "Thank you for submitting your details.";
  private static final String HOSTED_PAGE_GUID = "7a4e96f2-e4a1-4b68-b03e-30240ac0a514";

  @Test
  void initializePayApplication__ShouldReturnOK200() {
    // Arrange
    var initializeApplicationRequestDto = createInitializeApplicationRequestDto();
    var initializeApplicationRequest = createInitializeApplicationRequest();
    var initializeApplicationResponse = createInitializeApplicationResponse();
    var initializeApplicationResponseDto = createInitializeApplicationResponseDto();

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(createAccount());
    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(initializeApplicationRequestDto)).thenReturn(
        initializeApplicationRequest);
    when(payAppInPort.initializeApplication(
        initializeApplicationRequest, CLIENT_IP)).thenReturn(
        initializeApplicationResponse);
    when(payApplicationResponseDtoMapper.toDto(initializeApplicationResponse)).thenReturn(
        initializeApplicationResponseDto);

    // Act
    var response = payAppController.initializeApplication(
        initializeApplicationRequestDto, httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("1234", response.getBody().getApplicationGUID());
    assertEquals("12", response.getBody().getApplicationId());

    verify(payApplicationRequestDtoMapper).toModel(any(InitializeApplicationRequestDto.class));
    verify(payApplicationResponseDtoMapper).toDto(any(InitializeApplicationResponse.class));
  }

  @Test
  void initializeApplication__DifferentEmailShouldThrowException() {
    // Arrange
    var expectedMessage = "Email from request body was different from email in authorization token";
    var initializeApplicationRequestDtoWithOtherEmail = createInitializeApplicationRequestDtoWithOtherEmail();

    when(authenticatedUserService.getCurrentUserAccount()).thenReturn(Optional.empty());

    // Act
    var actual = assertThrows(EmailMismatchException.class,
        () -> payAppController.initializeApplication(
            initializeApplicationRequestDtoWithOtherEmail, httpServletRequest));

    // Assert
    assertThat(actual, is(notNullValue()));
    assertThat(actual.getGlobalErrTextTemplate(), is(USER_EMAIL_MISMATCH_ERROR.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(USER_EMAIL_MISMATCH_ERROR.getCode()));
  }

  @Test
  void fetchPayApplications__ShouldReturnOK200() {
    // Arrange
    var fetchApplicationsResponse = createFetchApplicationsResponse();
    var fetchApplicationsResponseDto = createFetchApplicationsResponseDto();
    var tokenClaims = createJwtTokenClaims();

    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(jwtUtils.parseToken()).thenReturn(tokenClaims);
    when(
        payAppInPort.fetchApplicationsDetails(tokenClaims, CLIENT_IP)).thenReturn(
        fetchApplicationsResponse);
    when(payApplicationResponseDtoMapper.toDto(fetchApplicationsResponse)).thenReturn(
        fetchApplicationsResponseDto);

    // Act
    var response = payAppController.fetchPayApplications(httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().getApplications().size());
    assertEquals("accountName", response.getBody().getApplications().get(0).getAccountName());
    assertEquals("status", response.getBody().getApplications().get(0).getStatus());
    assertEquals("appId", response.getBody().getApplications().get(0).getApplicationId());
    assertEquals("appGuid", response.getBody().getApplications().get(0).getApplicationGuid());
    assertEquals("resumeUrl", response.getBody().getApplications().get(0).getResumeUrl());

    verify(payApplicationResponseDtoMapper).toDto(fetchApplicationsResponse);
  }

  @Test
  void getAppLookup__ShouldReturnOK200() {
    // Arrange
    var getAppLookupRequestDto = createGetAppLookupRequestDto();
    var getAppLookupRequest = createGetAppLookupRequest();

    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(getAppLookupRequestDto)).thenReturn(
        getAppLookupRequest);

    // Act
    var response = payAppController.getAppLookup(getAppLookupRequestDto, httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));
    verify(payApplicationRequestDtoMapper).toModel(getAppLookupRequestDto);
    verify(payAppInPort).getAppLookup(getAppLookupRequest, CLIENT_IP);
  }

  @Test
  void getUserPreferences__ShouldReturnOK200() {
    // Arrange
    var getUserPreferencesRequestDto = GetUserPreferencesRequestDto.builder()
        .tetheredUserGuids(List.of("tetheredUserGui")).build();
    var getUserPreferencesRequest = GetUserPreferencesRequest.builder()
        .tetheredUserGuids(List.of("tetheredUserGuid")).build();
    var tokenClaims = createJwtTokenClaims();

    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(getUserPreferencesRequestDto))
        .thenReturn(getUserPreferencesRequest);
    when(jwtUtils.parseToken()).thenReturn(tokenClaims);

    // Act
    var response = payAppController
        .getUserPreferences(getUserPreferencesRequestDto, httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));
    verify(payApplicationRequestDtoMapper).toModel(getUserPreferencesRequestDto);
    verify(payAppInPort).getUserPreferences(getUserPreferencesRequest, tokenClaims, CLIENT_IP);
  }

  @Test
  void deleteApplication_ShouldReturnOK200() {
    // Arrange
    String applicationId = "1122";
    String applicationGuid = "123456";
    var requestDto = DeletePayApplicationRequestDto.builder().applicationId(applicationId)
        .applicationGuid(applicationGuid).scheme(Scheme.GB).build();
    var request = DeletePayApplicationRequest.builder().applicationId(applicationId)
        .applicationGuid(applicationGuid).scheme(Scheme.GB).build();
    var tokenClaims = createJwtTokenClaims();
    String clientIp = "192.168.1.1";
    var deleteApplicationResponse = DeletePayApplicationResponse.builder().build();
    var deleteApplicationResponseDto = DeletePayApplicationResponseDto.builder().build();

    when(jwtUtils.parseToken()).thenReturn(tokenClaims);
    when(payApplicationRequestDtoMapper.toModel(
        any(DeletePayApplicationRequestDto.class))).thenReturn(request);
    when(utils.getClientIp(httpServletRequest)).thenReturn(clientIp);
    when(payAppInPort.deleteApplication(request, tokenClaims, clientIp)).thenReturn(
        deleteApplicationResponse);
    when(payApplicationResponseDtoMapper.toDto(deleteApplicationResponse)).thenReturn(
        deleteApplicationResponseDto);

    // Act
    ResponseEntity<DeletePayApplicationResponseDto> response = payAppController.deleteApplication(
        requestDto, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(payAppInPort).deleteApplication(request, tokenClaims, clientIp);
  }

  @Test
  void updateAppContactDetails__ShouldReturnOK200() {
    // Arrange
    var updateAppContactDetailsRequestDto = createUpdateAppContactDetailsRequestDto();
    var updateAppContactDetailsRequest = createUpdateAppContactDetailsRequest();
    var updateAppContactDetailsResponse = createUpdateAppContactDetailsResponse();
    var updateAppContactDetailsResponseDto = createUpdateAppContactDetailsResponseDto();
    var tokenClaims = createJwtTokenClaims();

    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(jwtUtils.parseToken()).thenReturn(tokenClaims);
    when(payApplicationRequestDtoMapper.toModel(updateAppContactDetailsRequestDto)).thenReturn(
        updateAppContactDetailsRequest);
    when(payAppInPort.updateAppContactDetails(updateAppContactDetailsRequest, tokenClaims,
        CLIENT_IP)).thenReturn(
        updateAppContactDetailsResponse);
    when(payApplicationResponseDtoMapper.toDto(updateAppContactDetailsResponse)).thenReturn(
        updateAppContactDetailsResponseDto);

    // Act
    var response = payAppController.updateAppContactDetails(
        updateAppContactDetailsRequestDto, httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(200, response.getBody().getStatus());
    assertEquals("Success", response.getBody().getMessage());

    verify(payApplicationRequestDtoMapper).toModel(updateAppContactDetailsRequestDto);
    verify(payApplicationResponseDtoMapper).toDto(updateAppContactDetailsResponse);
  }

  @Test
  void updateAppCompanyDetails__ShouldReturnOK200() {
    // Arrange
    var updateAppCompanyDetailsRequestDto = createUpdateAppCompanyDetailsRequestDto();
    var updateAppCompanyDetailsRequest = createUpdateAppCompanyDetailsRequest();
    var updateAppCompanyDetailsResponse = createUpdateAppCompanyDetailsResponse();
    var updateAppCompanyDetailsResponseDto = createUpdateAppCompanyDetailsResponseDto();

    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(updateAppCompanyDetailsRequestDto)).thenReturn(
        updateAppCompanyDetailsRequest);
    when(
        payAppInPort.updateAppCompanyDetails(updateAppCompanyDetailsRequest, CLIENT_IP)).thenReturn(
        updateAppCompanyDetailsResponse);
    when(payApplicationResponseDtoMapper.toDto(updateAppCompanyDetailsResponse)).thenReturn(
        updateAppCompanyDetailsResponseDto);
    // Act
    var response = payAppController.updateAppCompanyDetails(
        updateAppCompanyDetailsRequestDto, httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(200, response.getBody().getStatus());
    assertEquals("Success", response.getBody().getMessage());

    verify(payApplicationRequestDtoMapper).toModel(updateAppCompanyDetailsRequestDto);
    verify(payApplicationResponseDtoMapper).toDto(updateAppCompanyDetailsResponse);
  }

  @Test
  void lookupCompanyDetails__ShouldReturnOK200() {
    // Arrange
    var appCompanyDetailsRequestDto = createAppCompanyDetailsRequestDto();
    var appCompanyDetailsRequest = createCompanyDetailsRequest();
    var appCompanyDetailsResponse = createAppCompanyDetailsResponse();
    var createAppCompanyDetailsResponseDto = createAppCompanyDetailsResponseDto();

    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(appCompanyDetailsRequestDto)).thenReturn(
        appCompanyDetailsRequest);

    when(payAppInPort.lookupCompanyDetails(appCompanyDetailsRequest, CLIENT_IP)).thenReturn(
        appCompanyDetailsResponse);
    when(payApplicationResponseDtoMapper.toDto(appCompanyDetailsResponse)).thenReturn(
        createAppCompanyDetailsResponseDto);

    // Act
    var response = payAppController.lookupCompanyDetails(
        appCompanyDetailsRequestDto, httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().getData().size());

    verify(payApplicationRequestDtoMapper).toModel(appCompanyDetailsRequestDto);
    verify(payApplicationResponseDtoMapper).toDto(appCompanyDetailsResponse);
    verify(payAppInPort).lookupCompanyDetails(appCompanyDetailsRequest, CLIENT_IP);
  }

  @Test
  void applicationDetails__ShouldReturnOk200() {
    // Arrange
    when(jwtUtils.parseToken()).thenReturn(createJwtTokenClaims());
    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(any(ApplicationDetailsRequestDto.class))).
        thenReturn(createApplicationDetailsRequest());
    when(payAppInPort.applicationDetails(createApplicationDetailsRequest(), createJwtTokenClaims(),
        CLIENT_IP)).
        thenReturn(createApplicationDetailsResponse());
    when(payApplicationResponseDtoMapper.toDto(any(ApplicationDetailsResponse.class))).thenReturn(
        createApplicationDetailsResponseDto());

    // Act
    var response = payAppController.getApplicationDetails(createApplicationDetailsRequestDto(),
        httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(APPLICATION_GUID, response.getBody().applicationGuid());
    assertEquals(APPLICATION_ID, response.getBody().applicationId());

    verify(payApplicationRequestDtoMapper).toModel(any(ApplicationDetailsRequestDto.class));
    verify(payApplicationResponseDtoMapper).toDto(any(ApplicationDetailsResponse.class));
    verify(payAppInPort).applicationDetails(createApplicationDetailsRequest(),
        createJwtTokenClaims(), CLIENT_IP);
  }

  @Test
  void deleteApplicationCard_ShouldReturnNoContent204() {
    // Arrange
    var deleteCardRequestDto = new DeleteCardRequestDto("app-guid", "app-id", "card-guid");
    var deleteCardRequest = new DeleteCardRequest("app-guid", "app-id", "card-guid");
    var tokenClaims = createJwtTokenClaims();
    var clientIp = CLIENT_IP;

    when(jwtUtils.parseToken()).thenReturn(tokenClaims);
    when(utils.getClientIp(httpServletRequest)).thenReturn(clientIp);
    when(payApplicationRequestDtoMapper.toModel(deleteCardRequestDto)).thenReturn(
        deleteCardRequest);

    // Act
    var response = payAppController.deleteApplicationCard(deleteCardRequestDto, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(payAppInPort).deleteApplicationCard(deleteCardRequest, tokenClaims, clientIp);
  }

  @Test
  void shareApp__ShouldReturnOk200() {
    // Arrange
    var shareAppRequestDto = createShareAppRequestDto();
    var shareAppRequest = createShareAppRequest();
    var shareAppResponse = createShareAppResponse();
    var shareAppResponseDto = createShareAppResponseDto();

    when(payApplicationRequestDtoMapper.toModel(shareAppRequestDto)).thenReturn(shareAppRequest);
    when(payAppInPort.shareApp(shareAppRequest)).thenReturn(shareAppResponse);
    when(payApplicationResponseDtoMapper.toDto(shareAppResponse)).thenReturn(shareAppResponseDto);

    // Act
    var response = payAppController.shareApp(shareAppRequestDto);

    // Assert
    assertThat(response, is(notNullValue()));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(200, response.getBody().getStatus());
    assertEquals("Success", response.getBody().getMessage());

    verify(payApplicationRequestDtoMapper).toModel(shareAppRequestDto);
    verify(payAppInPort).shareApp(shareAppRequest);
    verify(payApplicationResponseDtoMapper).toDto(shareAppResponse);

  }

  @Test
  void removeParticipant_ShouldReturnNoContent204() {
    // Arrange
    var removeParticipantRequestDto = new RemoveParticipantRequestDto("app-id", "app-guid", 1);
    var removeParticipantRequest = new RemoveParticipantRequest("app-id", "app-guid", 1);

    when(payApplicationRequestDtoMapper.toModel(removeParticipantRequestDto)).thenReturn(
        removeParticipantRequest);

    // Act
    var response = payAppController.removeParticipant(removeParticipantRequestDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(payAppInPort).removeParticipant(removeParticipantRequest);
  }

  @Test
  void getApplicationCards__ShouldReturnOk200() {
    // Arrange
    var getAppCardsRequestDto = createGetAppCardsRequestDto();
    var getAppCardsRequest = createGetAppCardsRequest();
    var getAppCardsResponse = createGetAppCardsResponse();
    var getAppCardsResponseDto = createGetAppCardsResponseDto();

    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(getAppCardsRequestDto)).thenReturn(
        getAppCardsRequest);
    when(payAppInPort.getApplicationCards(getAppCardsRequest, CLIENT_IP)).thenReturn(getAppCardsResponse);
    when(payApplicationResponseDtoMapper.toDto(getAppCardsResponse)).thenReturn(
        getAppCardsResponseDto);

    // Act
    var response = payAppController.getApplicationCards(getAppCardsRequestDto, httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));
    assertThat(response.getBody().getAppCards().get(0).getCardGuid(), is(notNullValue()));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().getAppCards().size());

    verify(payApplicationRequestDtoMapper).toModel(getAppCardsRequestDto);
    verify(payAppInPort).getApplicationCards(getAppCardsRequest, CLIENT_IP);
    verify(payApplicationResponseDtoMapper).toDto(getAppCardsResponse);
  }

  @Test
  void addApplicationCard__AsTravelManagerShouldReturnOk200() {
    // Arrange
    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(any(AddApplicationCardRequestDto.class))).thenReturn(
        createAddApplicationCardRequest());
    when(payAppInPort.addApplicationCard(createAddApplicationCardRequest(), CLIENT_IP)).thenReturn(
        createAddApplicationCardResponse());
    when(payApplicationResponseDtoMapper.toDto(any(AddApplicationCardResponse.class))).thenReturn(
        createAddApplicationCardResponseDto());
    // Act
    var response = payAppController.addApplicationCard(
        createAddApplicationCardRequestDto(), httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(CARD_GUID, response.getBody().getCardGuid());

    verify(payApplicationRequestDtoMapper).toModel(any(AddApplicationCardRequestDto.class));
    verify(payApplicationResponseDtoMapper).toDto(any(AddApplicationCardResponse.class));
    verify(payAppInPort).addApplicationCard(createAddApplicationCardRequest(), CLIENT_IP);

  }

  @Test
  void addApplicationCard__AsBookerShouldReturnOk200() {
    // Arrange
    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(any(AddApplicationCardRequestDto.class))).thenReturn(
        createAddApplicationCardRequest());
    when(payAppInPort.addApplicationCard(createAddApplicationCardRequest(), CLIENT_IP)).thenReturn(
        createAddApplicationCardResponse());
    when(payApplicationResponseDtoMapper.toDto(any(AddApplicationCardResponse.class))).thenReturn(
        createAddApplicationCardResponseDto());
    // Act
    var response = payAppController.addApplicationCard(
        createAddApplicationCardRequestDto(), httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(CARD_GUID, response.getBody().getCardGuid());

    verify(payApplicationRequestDtoMapper).toModel(any(AddApplicationCardRequestDto.class));
    verify(payApplicationResponseDtoMapper).toDto(any(AddApplicationCardResponse.class));
    verify(payAppInPort).addApplicationCard(createAddApplicationCardRequest(), CLIENT_IP);

  }

  @Test
  void submitApplication__ShouldReturnOk200(){
    // Arrange
    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(any(SubmitApplicationRequestDto.class)))
        .thenReturn(createSubmitApplicationRequest());
    when(payAppInPort.submitApplication(createSubmitApplicationRequest(), CLIENT_IP))
        .thenReturn(createSubmitApplicationResponse());
    when(payApplicationResponseDtoMapper.toDto(any(SubmitApplicationResponse.class)))
        .thenReturn(createSubmitApplicationResponseDto());

    // Act
    var response = payAppController.submitApplication(
        createSubmitApplicationRequestDto(), httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));
  }

  @Test
  void appPreCheck_ShouldReturnOK200() {
    // Arrange
    String email = "test@example.com";
    Boolean isTetheredUser = true;
    String applicationGuid = "123456";
    var appPreCheckRequest = new AppPreCheckRequest(email, Scheme.GB);
    var jwtTokenClaims = JwtTokenClaims.builder().email(email).build();
    var appPreCheckResponse = new AppPreCheckResponse(isTetheredUser, applicationGuid);
    var appPreCheckResponseDto = new AppPreCheckResponseDto(isTetheredUser, applicationGuid);

    when(jwtUtils.parseToken()).thenReturn(jwtTokenClaims);
    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(Scheme.GB, email)).thenReturn(appPreCheckRequest);
    when(payAppInPort.appPreCheck(appPreCheckRequest, CLIENT_IP)).thenReturn(appPreCheckResponse);
    when(payApplicationResponseDtoMapper.toDto(appPreCheckResponse)).thenReturn(appPreCheckResponseDto);

    // Act
    ResponseEntity<AppPreCheckResponseDto> response = payAppController.appPreCheck(Scheme.GB, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(jwtUtils).parseToken();
    verify(payApplicationRequestDtoMapper).toModel(Scheme.GB, email);
    verify(payAppInPort).appPreCheck(appPreCheckRequest, CLIENT_IP);
    verify(payApplicationResponseDtoMapper).toDto(appPreCheckResponse);
    assertSame(response.getBody(), appPreCheckResponseDto);
  }

  @Test
  void directDebit__ShouldReturnOk200() {
    // Arrange
    var directDebitRequestDto = new DirectDebitRequestDto(APPLICATION_ID, APPLICATION_GUID, "resumeUrl", DirectDebitOption.DIRECT);
    var directDebitRequest = new DirectDebitRequest(APPLICATION_ID, APPLICATION_GUID, "resumeUrl", DirectDebitOption.DIRECT);
    var directDebitResponse = new DirectDebitResponse("hostedPageGuid");
    var directDebitResponseDto = new DirectDebitResponseDto("hostedPageGuid");

    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(directDebitRequestDto)).thenReturn(directDebitRequest);
    when(payAppInPort.directDebit(directDebitRequest, CLIENT_IP)).thenReturn(directDebitResponse);
    when(payApplicationResponseDtoMapper.toDto(directDebitResponse)).thenReturn(directDebitResponseDto);

    // Act
    var response = payAppController.directDebit(directDebitRequestDto, httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody().hostedPageGuid(), is("hostedPageGuid"));

    verify(payApplicationRequestDtoMapper).toModel(directDebitRequestDto);
    verify(payAppInPort).directDebit(directDebitRequest, CLIENT_IP);
    verify(payApplicationResponseDtoMapper).toDto(directDebitResponse);

  }

  @Test
  void updateResumeUrl_ShouldReturnOk200() {
    // Arrange
    when(payApplicationRequestDtoMapper.toModel(any(UpdateResumeUrlRequestDto.class)))
        .thenReturn(createUpdateResumeUrlRequest());
    when(payAppInPort.updateResumeUrl(createUpdateResumeUrlRequest()))
        .thenReturn(createUpdateResumeUrlResponse());
    when(payApplicationResponseDtoMapper.toDto(any(UpdateResumeUrlResponse.class)))
        .thenReturn(createUpdateResumeUrlResponseDto());

    // Act
    var response = payAppController.updateResumeUrl(
        createUpdateResumeUrlRequestDto(), httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(200, response.getBody().status());
    assertEquals(SUCCESS_MESSAGE, response.getBody().message());

    verify(payApplicationRequestDtoMapper).toModel(any(UpdateResumeUrlRequestDto.class));
    verify(payAppInPort).updateResumeUrl(createUpdateResumeUrlRequest());
    verify(payApplicationResponseDtoMapper).toDto(any(UpdateResumeUrlResponse.class));
  }

  @Test
  void getDdSepaFormStatus__ShouldReturnOk200() {
    // Arrange
    var getDdSepaFormStatusRequestDto = new GetDdSepaFormStatusRequestDto(HOSTED_PAGE_GUID, Scheme.GB);
    var getDdSepaFormStatusRequest = new GetDdSepaFormStatusRequest(HOSTED_PAGE_GUID, Scheme.GB);
    var getDdSepaFormStatusResponse = new GetDdSepaFormStatusResponse("Complete");
    var getDdSepaFormStatusResponseDto = new GetDdSepaFormStatusResponseDto("Complete");

    when(utils.getClientIp(httpServletRequest)).thenReturn(CLIENT_IP);
    when(payApplicationRequestDtoMapper.toModel(getDdSepaFormStatusRequestDto)).thenReturn(getDdSepaFormStatusRequest);
    when(payAppInPort.getDdSepaFormStatus(getDdSepaFormStatusRequest, CLIENT_IP)).thenReturn(getDdSepaFormStatusResponse);
    when(payApplicationResponseDtoMapper.toDto(getDdSepaFormStatusResponse)).thenReturn(getDdSepaFormStatusResponseDto);

    // Act
    var response = payAppController.getDdSepaFormStatus(getDdSepaFormStatusRequestDto, httpServletRequest);

    // Assert
    assertThat(response, is(notNullValue()));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody().status(), is("Complete"));

    verify(payApplicationRequestDtoMapper).toModel(getDdSepaFormStatusRequestDto);
    verify(payAppInPort).getDdSepaFormStatus(getDdSepaFormStatusRequest, CLIENT_IP);
    verify(payApplicationResponseDtoMapper).toDto(getDdSepaFormStatusResponse);
  }

  AddApplicationCardRequestDto createAddApplicationCardRequestDto() {
    return AddApplicationCardRequestDto.builder()
        .scheme(Scheme.GB)
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .cardDetails(AddApplicationCardDetailsDto.builder()
            .myCard(true)
            .cardName("John Doe")
            .cardLimit(1000)
            .build())
        .build();
  }

  AddApplicationCardResponseDto createAddApplicationCardResponseDto() {
    return AddApplicationCardResponseDto.builder()
        .cardGuid(CARD_GUID)
        .build();
  }

  AddApplicationCardResponse createAddApplicationCardResponse() {
    return AddApplicationCardResponse.builder()
        .cardGuid(CARD_GUID)
        .build();
  }

  AddApplicationCardRequest createAddApplicationCardRequest() {
    return AddApplicationCardRequest.builder()
        .scheme(Scheme.GB)
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .cardDetails(AddApplicationCardDetails.builder()
            .myCard(true)
            .cardName("John Doe")
            .cardLimit(1000)
            .build())
        .build();
  }

  ApplicationDetailsRequestDto createApplicationDetailsRequestDto() {
    return ApplicationDetailsRequestDto.builder()
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .scheme(Scheme.GB)
        .build();
  }

  ApplicationDetailsResponseDto createApplicationDetailsResponseDto() {
    return ApplicationDetailsResponseDto.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .build();
  }

  ApplicationDetailsResponse createApplicationDetailsResponse() {
    return ApplicationDetailsResponse.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .build();
  }

  ApplicationDetailsRequest createApplicationDetailsRequest() {
    return ApplicationDetailsRequest.builder()
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .scheme(Scheme.GB)
        .build();
  }

  AppCompanyDetailsResponse createAppCompanyDetailsResponse() {
    return AppCompanyDetailsResponse.builder()
        .data(getCompanyDetails())
        .build();
  }

  static List<CompanyDetails> getCompanyDetails() {
    return List.of(CompanyDetails.builder()
        .companyName("companyName")
        .creditAgencyReference("creditAgencyReference")
        .build());
  }

  static List<CompanyDetailsDto> getCompanyDetailsDto() {
    return List.of(CompanyDetailsDto.builder()
        .companyName("companyName")
        .creditAgencyReference("creditAgencyReference")
        .build());
  }

  AppCompanyDetailsResponseDto createAppCompanyDetailsResponseDto() {
    return AppCompanyDetailsResponseDto.builder()
        .data(getCompanyDetailsDto())
        .build();
  }

  AppCompanyDetailsRequest createCompanyDetailsRequest() {
    return AppCompanyDetailsRequest.builder()
        .scheme(Scheme.GB)
        .companyRegistrationNumber("dummy-company-registration-number")
        .build();
  }

  AppCompanyDetailsRequestDto createAppCompanyDetailsRequestDto() {
    return AppCompanyDetailsRequestDto.builder()
        .companyRegistrationNumber("dummy-company-registration-number")
        .scheme(Scheme.GB)
        .build();
  }

  JwtTokenClaims createJwtTokenClaims() {
    return JwtTokenClaims.builder()
        .email("john.doe@email.com")
        .companyId("1")
        .employeeId("1")
        .build();
  }

  InitializeApplicationRequestDto createInitializeApplicationRequestDto() {
    return InitializeApplicationRequestDto.builder()
        .email("john.doe@email.com")
        .scheme(Scheme.GB)
        .incentiveCode("123")
        .campaignCode("C123")
        .build();
  }

  InitializeApplicationRequestDto createInitializeApplicationRequestDtoWithOtherEmail() {
    return InitializeApplicationRequestDto.builder()
        .email("other.name@email.com")
        .scheme(Scheme.GB)
        .incentiveCode("123")
        .campaignCode("C123")
        .build();
  }

  InitializeApplicationRequest createInitializeApplicationRequest() {
    return InitializeApplicationRequest.builder()
        .email("john.doe@email.com")
        .incentiveCode("123")
        .campaignCode("C123")
        .build();
  }

  InitializeApplicationResponse createInitializeApplicationResponse() {
    return InitializeApplicationResponse.builder()
        .applicationGUID("1234")
        .applicationId("12")
        .build();
  }

  InitializeApplicationResponseDto createInitializeApplicationResponseDto() {
    return InitializeApplicationResponseDto.builder()
        .applicationGUID("1234")
        .applicationId("12")
        .build();
  }

  FetchApplicationsResponse createFetchApplicationsResponse() {
    return FetchApplicationsResponse.builder()
        .applications(List.of(
            new ApplicationDetails("status", "accountName", "appId", "appGuid", "resumeUrl",
                "scheme")))
        .build();
  }

  FetchApplicationsResponseDto createFetchApplicationsResponseDto() {
    return FetchApplicationsResponseDto.builder()
        .applications(List.of(
            new ApplicationDetailsDto("status", "accountName", "appId", "appGuid", "resumeUrl",
                "scheme")))
        .build();
  }

  UpdateAppContactDetailsRequestDto createUpdateAppContactDetailsRequestDto() {
    return UpdateAppContactDetailsRequestDto.builder()
        .applicationGuid("123")
        .title("Mr")
        .foreName("John")
        .lastName("Doe")
        .position("Developer")
        .telephone("123")
        .email("john.doe@email.com")
        .scheme(Scheme.GB)
        .applicationId("123")
        .resumeUrl("http://resume.com")
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
        .resumeUrl("http://resume.com")
        .build();
  }

  UpdateAppContactDetailsResponse createUpdateAppContactDetailsResponse() {
    return UpdateAppContactDetailsResponse.builder()
        .status(200)
        .message("Success")
        .build();
  }

  UpdateAppContactDetailsResponseDto createUpdateAppContactDetailsResponseDto() {
    return UpdateAppContactDetailsResponseDto.builder()
        .status(200)
        .message("Success")
        .build();
  }

  GetAppLookupRequestDto createGetAppLookupRequestDto() {
    return GetAppLookupRequestDto.builder()
        .scheme(Scheme.GB)
        .lookupNames(List.of(LookupName.TITLE, LookupName.TRADING_STYLE))
        .build();
  }

  GetAppLookupRequest createGetAppLookupRequest() {
    return GetAppLookupRequest.builder()
        .scheme(Scheme.GB)
        .lookupNames(List.of(LookupName.TITLE, LookupName.TRADING_STYLE))
        .build();
  }

  UpdateAppCompanyDetailsRequestDto createUpdateAppCompanyDetailsRequestDto() {
    return UpdateAppCompanyDetailsRequestDto.builder()
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .resumeUrl("http://resume.com")
        .appCompanyDetailsDto(AppCompanyDetailsDto.builder()
            .companyName("Company Name")
            .estMonthlySpend("£4,000")
            .companyType("Partnership")
            .partnerDetails(PartnerDetailsDto.builder()
                .title("Mr")
                .foreName("John")
                .lastName("Doe")
                .dateOfBirth("01-01-1990")
                .numberOfPartners(2)
                .build())
            .registrationAddress(AddressDto.builder()
                .addressLine1("addressLine1")
                .addressLine2("addressLine2")
                .build())
            .correspondenceContactInfo(ContactInfoDto.builder()
                .title("Mr")
                .foreName("John")
                .lastName("Doe")
                .email("johndoe@email.com")
                .build())
            .build())
        .scheme(Scheme.GB)
        .build();
  }

  UpdateAppCompanyDetailsRequest createUpdateAppCompanyDetailsRequest() {
    return UpdateAppCompanyDetailsRequest.builder()
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .resumeUrl("http://resume.com")
        .appCompanyDetails(AppCompanyDetails.builder()
            .companyName("Company Name")
            .estMonthlySpend("£4,000")
            .companyType("Partnership")
            .partnerDetails(PartnerDetails.builder()
                .title("Mr")
                .foreName("John")
                .lastName("Doe")
                .dateOfBirth("01-01-1990")
                .numberOfPartners(2)
                .build())
            .registrationAddress(Address.builder()
                .addressLine1("addressLine1")
                .addressLine2("addressLine2")
                .build())
            .correspondenceContactInfo(ContactInfo.builder()
                .title("Mr")
                .foreName("John")
                .lastName("Doe")
                .email("johndoe@email.com")
                .build())
            .build())
        .scheme(Scheme.GB)
        .build();
  }

  UpdateAppCompanyDetailsResponse createUpdateAppCompanyDetailsResponse() {
    return UpdateAppCompanyDetailsResponse.builder()
        .status(200)
        .message("Success")
        .build();
  }

  UpdateAppCompanyDetailsResponseDto createUpdateAppCompanyDetailsResponseDto() {
    return UpdateAppCompanyDetailsResponseDto.builder()
        .status(200)
        .message("Success")
        .build();
  }

  Optional<Account> createAccount() {
    return Optional.of(Account.builder()
        .email("john.doe@email.com")
        .accessLevel("SUPER")
        .build());
  }

  ShareAppRequestDto createShareAppRequestDto() {
    return ShareAppRequestDto.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .email("john.doe@email.com")
        .fullName("John Doe")
        .build();
  }

  ShareAppRequest createShareAppRequest() {
    return ShareAppRequest.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .email("john.doe@email.com")
        .fullName("John Doe")
        .build();
  }

  ShareAppResponse createShareAppResponse() {
    return ShareAppResponse.builder()
        .status(200)
        .message("Success")
        .build();
  }

  ShareAppResponseDto createShareAppResponseDto() {
    return ShareAppResponseDto.builder()
        .status(200)
        .message("Success")
        .build();
  }

  GetAppCardsRequestDto createGetAppCardsRequestDto() {
    return GetAppCardsRequestDto.builder()
        .applicationGuid(APPLICATION_GUID)
        .scheme(Scheme.GB)
        .page(1)
        .maxDisplayRows(10)
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

  GetAppCardsResponseDto createGetAppCardsResponseDto() {
    return GetAppCardsResponseDto.builder()
        .appCards(
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
        .build();
  }

  SubmitApplicationRequestDto createSubmitApplicationRequestDto() {
    return SubmitApplicationRequestDto.builder()
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .termsAndConditionAccepted("Y")
        .registrationQuestion("registrationQuestion")
        .registrationAnswer("registrationAnswer")
        .scheme(Scheme.GB)
        .build();
  }

  SubmitApplicationResponseDto createSubmitApplicationResponseDto() {
    return SubmitApplicationResponseDto.builder()
        .message(SUCCESS_MESSAGE)
        .build();
  }

  SubmitApplicationResponse createSubmitApplicationResponse() {
    return SubmitApplicationResponse.builder()
        .message(SUCCESS_MESSAGE)
        .build();
  }

  SubmitApplicationRequest createSubmitApplicationRequest() {
    return SubmitApplicationRequest.builder()
        .applicationGuid(APPLICATION_GUID)
        .applicationId(APPLICATION_ID)
        .termsAndConditionAccepted("Y")
        .registrationQuestion("registrationQuestion")
        .registrationAnswer("registrationAnswer")
        .scheme(Scheme.GB)
        .build();
  }

  UpdateResumeUrlRequestDto createUpdateResumeUrlRequestDto() {
    return new UpdateResumeUrlRequestDto(APPLICATION_ID, APPLICATION_GUID, "newResumeUrl");
  }

  UpdateResumeUrlResponseDto createUpdateResumeUrlResponseDto() {
    return new UpdateResumeUrlResponseDto(200, SUCCESS_MESSAGE);
  }

  UpdateResumeUrlResponse createUpdateResumeUrlResponse() {
    return new UpdateResumeUrlResponse(200, SUCCESS_MESSAGE);
  }

  UpdateResumeUrlRequest createUpdateResumeUrlRequest() {
    return new UpdateResumeUrlRequest(APPLICATION_ID, APPLICATION_GUID, "newResumeUrl");
  }

}
