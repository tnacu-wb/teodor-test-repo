package uk.co.whitbread.payapp.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import uk.co.whitbread.payapp.domain.model.in.GetUserPreferencesRequest;
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
import uk.co.whitbread.payapp.domain.model.out.AppCompanyDetailsResponse;
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
import uk.co.whitbread.payapp.domain.model.out.GetUserPreferencesResponse;
import uk.co.whitbread.payapp.domain.model.out.InitializeApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.ShareAppResponse;
import uk.co.whitbread.payapp.domain.model.out.SubmitApplicationResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateAppCompanyDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateAppContactDetailsResponse;
import uk.co.whitbread.payapp.domain.model.out.UpdateResumeUrlResponse;
import uk.co.whitbread.payapp.domain.model.out.UserPreferenceDetails;
import uk.co.whitbread.payapp.domain.model.out.UserPreferenceSettingsItem;
import uk.co.whitbread.payapp.domain.model.out.cdh.TetheredGuidResponse;
import uk.co.whitbread.payapp.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.payapp.domain.ports.secondary.PayAppOutPort;

@ExtendWith(MockitoExtension.class)
class PayAppInPortImplTest {

  @Mock
  private PayAppOutPort payAppOutPort;

  @Mock
  private CdhOutPort cdhOutPort;

  @InjectMocks
  private PayAppInPortImpl payAppInPort;

  private static final String CLIENT_IP = "1.1.1.1";
  private static final String APPLICATION_GUID = "157e2060-6598-4079-aebd-e34671640322";
  private static final String APPLICATION_ID = "784529";
  private static final String CARD_GUID = "6bb273f9-1439-4d23-935b-b8d1535e8776";
  private static final String SUCCESS_MESSAGE = "Thank you for submitting your details.";
  private static final String HOSTED_PAGE_GUID = "7a4e96f2-e4a1-4b68-b03e-30240ac0a514";

  @Test
  void initializeApplication__ShouldReturnOk() {
    // Arrange
    when(payAppOutPort.initializeApplication(any(), any())).thenReturn(createInitializeApplicationResponse());

    // Act
    var initializeApplicationResponse = payAppInPort.initializeApplication(
        createInitializeApplicationRequest(), CLIENT_IP);

    // Assert
    assertThat(initializeApplicationResponse, notNullValue());

    verify(payAppOutPort).initializeApplication(any(), any());
  }

  @Test
  void fetchApplicationsDetails__ShouldReturnOk() {
    // Arrange
    String accountName = "accountName";
    String status = "status";
    when(payAppOutPort.fetchApplicationsDetails(any(), any())).thenReturn(createFetchApplicationsResponse(accountName, status));

    // Act
    var fetchApplicationsDetails = payAppInPort.fetchApplicationsDetails(createJwtTokenClaims(), CLIENT_IP);

    // Assert
    assertThat(fetchApplicationsDetails, notNullValue());
    assertEquals(1, fetchApplicationsDetails.getApplications().size());
    assertEquals(fetchApplicationsDetails.getApplications().get(0).getAccountName(), accountName);
    assertEquals(fetchApplicationsDetails.getApplications().get(0).getStatus(), status);
    verify(payAppOutPort).fetchApplicationsDetails(any(), any());
  }

  @Test
  void getAppLookup__ShouldReturnOk() {
    // Arrange
    var createGetAppLookupRequest = createGetAppLookupRequest();
    when(payAppOutPort.getAppLookup(createGetAppLookupRequest, CLIENT_IP)).thenReturn(createGetAppLookupResponse());

    // Act
    var getAppLookupResponse = payAppInPort.getAppLookup(createGetAppLookupRequest, CLIENT_IP);

    // Assert
    assertThat(getAppLookupResponse, notNullValue());
    verify(payAppOutPort).getAppLookup(createGetAppLookupRequest, CLIENT_IP);
  }

  @Test
  void getUserPreferences__ShouldReturnOk() {
    // Arrange
    String accountName = "accountName";
    String tetheredUserGuid = "tetheredUserGuid";

    when(payAppOutPort.getUserPreferences(any(), any()))
        .thenReturn(createGetUserPreferencesResponse(accountName));
    when(cdhOutPort.getTetheredGuids(any(), any(), any())).thenReturn(List.of(
        TetheredGuidResponse.builder().tetheredGuid(tetheredUserGuid).build()));

    // Act
    var userPreferences = payAppInPort
        .getUserPreferences(createGetUserPreferencesRequest(tetheredUserGuid),
            createJwtTokenClaims(), CLIENT_IP);

    // Assert
    assertThat(userPreferences, notNullValue());
    assertThat(userPreferences, hasSize(1));

    var userPreferencesResponse = userPreferences.get(0);

    var details = userPreferencesResponse.getDetails();
    assertThat(details, notNullValue());
    assertEquals(details.getAccountName(), accountName);

    var settings = userPreferencesResponse.getSettings();
    assertThat(settings, notNullValue());
    assertThat(settings, hasSize(1));

    verify(payAppOutPort).getUserPreferences(any(), any());
  }

  @Test
  void deleteApplication_ShouldReturnExpectedResponse() {
    // Arrange
    var deleteRequest = DeletePayApplicationRequest.builder().applicationId("123").applicationGuid("guid-123").scheme(Scheme.GB).build();
    var jwtTokenClaims = createJwtTokenClaims();
    var clientIp = "192.168.1.1";
    DeletePayApplicationResponse expectedResponse = DeletePayApplicationResponse.builder().status(200).message("Success").build();
    when(payAppOutPort.deleteApplication(any(), any(), any())).thenReturn(expectedResponse);

    // Act
    DeletePayApplicationResponse actualResponse = payAppInPort.deleteApplication(deleteRequest, jwtTokenClaims, clientIp);

    // Assert
    assertEquals(expectedResponse, actualResponse);
    verify(payAppOutPort).deleteApplication(deleteRequest, jwtTokenClaims, clientIp);
  }

  @Test
  void updateAppContactDetails__ShouldReturnOk() {
    // Arrange
    when(payAppOutPort.updateAppContactDetails(any(), any(), any())).thenReturn(createUpdateAppContactDetailsResponse());

    // Act
    var updateAppContactDetailsResponse = payAppInPort.updateAppContactDetails(
        createUpdateAppContactDetailsRequest(), createJwtTokenClaims(), CLIENT_IP);

    // Assert
    assertThat(updateAppContactDetailsResponse, notNullValue());
    assertEquals(200, updateAppContactDetailsResponse.getStatus());
    assertEquals("Success", updateAppContactDetailsResponse.getMessage());

    verify(payAppOutPort).updateAppContactDetails(any(), any(), any());
  }

  @Test
  void updateAppCompanyDetails__ShouldReturnOk() {
    // Arrange
    when(payAppOutPort.updateAppCompanyDetails(any(), any())).thenReturn(createUpdateAppCompanyDetailsResponse());

    // Act
    var updateAppCompanyDetailsResponse = payAppInPort.updateAppCompanyDetails(
        createUpdateAppCompanyDetailsRequest(), CLIENT_IP);

    // Assert
    assertThat(updateAppCompanyDetailsResponse, notNullValue());
    assertEquals(200, updateAppCompanyDetailsResponse.getStatus());
    assertEquals("Success", updateAppCompanyDetailsResponse.getMessage());

    verify(payAppOutPort).updateAppCompanyDetails(any(), any());
  }

  @Test
  void lookupCompanyDetails__ShouldReturnOk() {
    // Arrange
    when(payAppOutPort.lookupCompanyDetails(any(), any())).thenReturn(createAppCompanyDetailsResponse());

    // Act
    var response = payAppInPort.lookupCompanyDetails(createAppCompanyDetailsRequest(),
        CLIENT_IP);

    // Assert
    assertThat(response, notNullValue());
    assertEquals(1, response.getData().size());

    verify(payAppOutPort).lookupCompanyDetails(any(), any());
  }


  @Test
  void applicationDetails__ShouldReturnOK() {
    // Arrange
    when(payAppOutPort.applicationDetails(any(), any(), any()))
        .thenReturn(createApplicationDetailsResponse());

    // Act
    var applicationDetailsResponse = payAppInPort.applicationDetails(
        createApplicationDetailsRequest(), createJwtTokenClaims(), CLIENT_IP);

    // Assert
    assertThat(applicationDetailsResponse, notNullValue());
    assertEquals(APPLICATION_ID, applicationDetailsResponse.applicationId());
    assertEquals(APPLICATION_GUID, applicationDetailsResponse.applicationGuid());

    verify(payAppOutPort).applicationDetails(any(), any(), any());
  }

  @Test
  void shareApp__ShouldReturnOK() {
    // Arrange
    when(payAppOutPort.shareApp(any())).thenReturn(createShareAppResponse());

    // Act
    var shareAppResponse = payAppInPort.shareApp(createShareAppRequest());

    // Assert
    assertThat(shareAppResponse, notNullValue());
    assertEquals(200, shareAppResponse.getStatus());
    assertEquals("Success", shareAppResponse.getMessage());

    verify(payAppOutPort).shareApp(any());
  }

  @Test
  void removeParticipant__ShouldCallOutPort() {
    // Arrange
    var removeParticipantRequest = new RemoveParticipantRequest(
        "app-id", "app-guid", 123);

    // Act
    payAppInPort.removeParticipant(removeParticipantRequest);

    // Assert
    verify(payAppOutPort).removeParticipant(removeParticipantRequest);
  }

  @Test
  void deleteApplicationCard_ShouldCallOutPort() {
    // Arrange
    var deleteRequest = new DeleteCardRequest("app-guid", "app-id", "card-guid");
    var jwtTokenClaims = createJwtTokenClaims();
    var clientIp = CLIENT_IP;

    // Act
    payAppInPort.deleteApplicationCard(deleteRequest, jwtTokenClaims, clientIp);

    // Assert
    verify(payAppOutPort).deleteApplicationCard(deleteRequest, jwtTokenClaims, clientIp);
  }

  @Test
  void getApplicationCards__ShouldReturnOK() {
    // Arrange
    when(payAppOutPort.getApplicationCards(any(), any())).thenReturn(createGetAppCardsResponse());

    // Act
    var getApplicationCardsResponse = payAppInPort.getApplicationCards(creatGetAppCardsRequest(), CLIENT_IP);

    // Assert
    assertThat(getApplicationCardsResponse, notNullValue());
    assertThat(getApplicationCardsResponse.getAppCards(), notNullValue());
    verify(payAppOutPort).getApplicationCards(any(), any());
  }

  @Test
  void addApplicationCard__ShouldReturnOk(){
    // Arrange
    when(payAppOutPort.addApplicationCard(any(), any()))
        .thenReturn(createAddApplicationCardResponse());

    // Act
    var addApplicationCardResponse = payAppInPort.addApplicationCard(
        createAddApplicationCardRequest(), CLIENT_IP);

    // Assert
    assertThat(addApplicationCardResponse, notNullValue());
    assertEquals(CARD_GUID, addApplicationCardResponse.getCardGuid());

    verify(payAppOutPort).addApplicationCard(any(), any());
  }

  @Test
  void submitApplication__ShouldReturnOk() {
    // Arrange
    when(payAppOutPort.submitApplication(any(), any()))
        .thenReturn(createSubmitApplicationResponse());

    // Act
    var submitApplicationResponse = payAppInPort.submitApplication(
        createSubmitApplicationRequest(), CLIENT_IP);

    // Assert
    assertThat(submitApplicationResponse, notNullValue());
    assertEquals(SUCCESS_MESSAGE, submitApplicationResponse.getMessage());

    verify(payAppOutPort).submitApplication(any(), any());
  }

  @Test
  void appPreCheck_ShouldCallOutPort() {
    // Arrange
    var appPreCheckRequest = new AppPreCheckRequest("test@example.com", Scheme.GB);

    // Act
    payAppInPort.appPreCheck(appPreCheckRequest, CLIENT_IP);

    // Assert
    verify(payAppOutPort).appPreCheck(appPreCheckRequest, CLIENT_IP);
  }

  @Test
  void directDebit__ShouldCallOutPort() {
    // Arrange
    var directDebitRequest = new DirectDebitRequest(
        APPLICATION_ID, APPLICATION_GUID, "resumeUrl", DirectDebitOption.DIRECT);

    when(payAppOutPort.directDebit(any(), any()))
        .thenReturn(createDirectDebitResponse());
    // Act
    var directDebitResponse = payAppInPort.directDebit(
        directDebitRequest, CLIENT_IP);

    // Assert
    assertThat(directDebitResponse, notNullValue());

    verify(payAppOutPort).directDebit(any(), any());
  }

  @Test
  void updateResumeUrl__ShouldCallOutPort() {
    // Arrange
    var updateResumeUrlRequest = new UpdateResumeUrlRequest(
        APPLICATION_ID, APPLICATION_GUID, "resumeUrl");
    when(payAppOutPort.updateResumeUrl(any())).thenReturn(createUpdateResumeUrlResponse());

    // Act
    var updateResumeUrlResponse = payAppInPort.updateResumeUrl(updateResumeUrlRequest);

    // Assert
    assertThat(updateResumeUrlResponse, notNullValue());

    verify(payAppOutPort).updateResumeUrl(any());
  }

  @Test
  void getDdSepaFormStatus__ShouldCallOutPort() {
    // Arrange
    var getDdSepaFormStatusRequest = new GetDdSepaFormStatusRequest(HOSTED_PAGE_GUID, Scheme.GB);
    when(payAppOutPort.getDdSepaFormStatus(any(), any())).thenReturn(createGetDdSepaFormStatusResponse());

    // Act
    var getDdSepaFormStatusResponse = payAppInPort.getDdSepaFormStatus(getDdSepaFormStatusRequest, CLIENT_IP);

    // Assert
    assertThat(getDdSepaFormStatusResponse, notNullValue());

    verify(payAppOutPort).getDdSepaFormStatus(any(), any());
  }

  GetDdSepaFormStatusResponse createGetDdSepaFormStatusResponse() {
    return GetDdSepaFormStatusResponse.builder()
        .status("Completed")
        .build();
  }

  AddApplicationCardRequest createAddApplicationCardRequest() {
      return AddApplicationCardRequest.builder()
          .applicationGuid(APPLICATION_GUID)
          .applicationId(APPLICATION_ID)
          .scheme(Scheme.GB)
          .cardDetails(AddApplicationCardDetails.builder()
              .myCard(true)
              .cardLimit(30)
              .cardName("John Doe")
              .build())
          .build();
  }

  AddApplicationCardResponse createAddApplicationCardResponse() {
    return AddApplicationCardResponse.builder()
        .cardGuid(CARD_GUID)
        .build();
  }

  ApplicationDetailsRequest createApplicationDetailsRequest() {
    return ApplicationDetailsRequest.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .scheme(Scheme.GB)
        .build();
  }

  ApplicationDetailsResponse createApplicationDetailsResponse() {
    return ApplicationDetailsResponse.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .scheme("Standard")
        .build();
  }

  AppCompanyDetailsRequest createAppCompanyDetailsRequest() {
    return AppCompanyDetailsRequest.builder()
        .companyRegistrationNumber("dummy-company-registration-number")
        .scheme(Scheme.GB)
        .build();
  }

  AppCompanyDetailsResponse createAppCompanyDetailsResponse() {
    var appCompanyDetailsResponse = new AppCompanyDetailsResponse();
    appCompanyDetailsResponse.setData(List.of(CompanyDetails.builder()
            .companyName("dummy-company-name")
            .creditAgencyReference("dummy-credit-agency-reference")
        .build()));
    return appCompanyDetailsResponse;
  }

  JwtTokenClaims createJwtTokenClaims() {
    JwtTokenClaims jwtTokenClaims = new JwtTokenClaims();
    jwtTokenClaims.setEmployeeId("1");
    jwtTokenClaims.setCompanyId("1");

    return jwtTokenClaims;
  }

  InitializeApplicationResponse createInitializeApplicationResponse() {
    return InitializeApplicationResponse.builder()
        .applicationGUID("1")
        .applicationId("1")
        .build();
  }

  InitializeApplicationRequest createInitializeApplicationRequest() {
    return InitializeApplicationRequest.builder()
        .email("john.doe@email.com")
        .campaignCode("123")
        .incentiveCode("123")
        .scheme(Scheme.GB)
        .build();
  }

  List<GetUserPreferencesResponse> createGetUserPreferencesResponse(String accountName) {
    return List.of(GetUserPreferencesResponse.builder()
        .details(UserPreferenceDetails.builder().accountName(accountName).build())
        .settings(List.of(UserPreferenceSettingsItem.builder().build()))
        .build());
  }

  GetUserPreferencesRequest createGetUserPreferencesRequest(String tetheredUserGuid) {
    return GetUserPreferencesRequest.builder()
        .tetheredUserGuids(List.of(tetheredUserGuid))
        .build();
  }

  FetchApplicationsResponse createFetchApplicationsResponse(String accountName, String status) {
    return FetchApplicationsResponse.builder()
        .applications(List.of(ApplicationDetails.builder().accountName(accountName).status(status).build()))
        .build();
  }

  UpdateAppContactDetailsResponse createUpdateAppContactDetailsResponse() {
    return UpdateAppContactDetailsResponse.builder()
        .status(200)
        .message("Success")
        .build();
  }

  UpdateAppContactDetailsRequest createUpdateAppContactDetailsRequest() {
    return UpdateAppContactDetailsRequest.builder()
        .applicationGuid("1")
        .title("Mr")
        .foreName("John")
        .lastName("Doe")
        .position("Developer")
        .telephone("+123")
        .email("john.doe@email.com")
        .scheme(Scheme.GB)
        .applicationId("1")
        .resumeUrl("http://resume.url")
        .build();
  }

  Map<String, List<String>> createGetAppLookupResponse() {
    return Map.of("title", List.of("Mr", "Mrs", "Miss", "Ms", "Doctor"),
        "tradingstyle", List.of("Charity", "Government", "Other"));
  }

  GetAppLookupRequest createGetAppLookupRequest() {
    return GetAppLookupRequest.builder()
        .lookupNames(List.of(LookupName.TITLE, LookupName.TRADING_STYLE))
        .scheme(Scheme.GB)
        .build();
  }

  UpdateAppCompanyDetailsResponse createUpdateAppCompanyDetailsResponse() {
    return UpdateAppCompanyDetailsResponse.builder()
        .status(200)
        .message("Success")
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

  ShareAppResponse createShareAppResponse() {
    return ShareAppResponse.builder()
        .status(200)
        .message("Success")
        .build();
  }

  ShareAppRequest createShareAppRequest() {
    return ShareAppRequest.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .employeeId(123)
        .email("john.doe@email.com")
        .fullName("John Doe")
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

  GetAppCardsRequest creatGetAppCardsRequest() {
    return GetAppCardsRequest.builder()
        .applicationGuid(APPLICATION_GUID)
        .scheme(Scheme.GB)
        .page(1)
        .maxDisplayRows(10)
        .build();
  }

  SubmitApplicationRequest createSubmitApplicationRequest() {
    return SubmitApplicationRequest.builder()
        .applicationId(APPLICATION_ID)
        .applicationGuid(APPLICATION_GUID)
        .scheme(Scheme.GB)
        .hostedPageGuid("hosted-page-guid")
        .registrationAnswer("registration-answer")
        .registrationQuestion("registration-question")
        .termsAndConditionAccepted("Y")
        .build();
  }

  SubmitApplicationResponse createSubmitApplicationResponse() {
    return SubmitApplicationResponse.builder()
        .message("Thank you for submitting your details.")
        .build();
  }

  DirectDebitResponse createDirectDebitResponse() {
    return DirectDebitResponse.builder()
        .hostedPageGuid("hosted-page-guid")
        .build();
  }

  UpdateResumeUrlResponse createUpdateResumeUrlResponse() {
    return new UpdateResumeUrlResponse(200, SUCCESS_MESSAGE);
  }

}
