package uk.co.whitbread.piba.registration.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.piba.registration.util.TestUtil.PRIMARY_SCHEME_CUSTOMER_ID;
import static uk.co.whitbread.piba.registration.util.TestUtil.REGISTRATION_CODE_DE;
import static uk.co.whitbread.piba.registration.util.TestUtil.REGISTRATION_CODE_GB;
import static uk.co.whitbread.piba.registration.util.TestUtil.REGISTRATION_ROLE;
import static uk.co.whitbread.piba.registration.util.TestUtil.TETHERED_USER_GUID;

import java.util.concurrent.Executor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import uk.co.whitbread.piba.registration.client.CdhClient;
import uk.co.whitbread.piba.registration.client.PibaAccountServiceClient;
import uk.co.whitbread.piba.registration.converter.WorldlineRegistrationTransformer;
import uk.co.whitbread.piba.registration.exception.InValidTokenException;
import uk.co.whitbread.piba.registration.exception.PibaRegistrationException;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitRequest;
import uk.co.whitbread.piba.registration.model.RegistrationSubmitResp;
import uk.co.whitbread.piba.registration.model.Scheme;
import uk.co.whitbread.piba.registration.model.TetheredUserAccountOverview;
import uk.co.whitbread.piba.registration.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.registration.model.TetheredUserRequest;
import uk.co.whitbread.piba.registration.properties.CdhProperties;
import uk.co.whitbread.piba.registration.util.TestUtil;
import uk.co.whitbread.piba.registration.util.WorldlineUtils;
import uk.co.whitbread.piba.registration.validation.WorldLineRegistrationResponseValidator;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.CardHolder;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmit;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitResponse;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationSubmitResponseType;
import worldline.mst.bsm.api.b2b.pi.data.TetherDetailsType;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PibaRegistrationSubmitServiceTest {

    private static final String PIBA_REGISTRATIION_SERVICE_URL = "piba registratiion service url";
    private static final String COMPANY_ID = "50";
    private static final String EMPLOYEE_ID = "1";
    private static final String USER_EMAIL = "test@test.com";
    private static final String APP_GUID = "123-456";
    private static final String CARD_GUID = "123-2222-3493hh32";
    private static final Integer CARD_EMPLOYEE_ID = 12;

    @InjectMocks
    @Spy
    private PibaRegistrationService objectUnderTest;

    @Mock
    private WebServiceTemplate mockWorldlineWebServiceTemplate;

    @Mock
    private WorldlineRegistrationTransformer mockWorldlineRegistrationTransformer;

    @Mock
    private WorldLineProperties mockWorldLineProperties;

    @Mock
    private WorldLineRegistrationResponseValidator mockWorldLineRegistrationResponseValidator;

    @Mock
    private WorldLineWebServiceMessageCallback mockWorldLineWebServiceMessageCallback;

    @Mock
    private RegistrationSubmitResponse mockRegistrationSubmitResponse;

    @Mock
    private RegistrationSubmit mockRegistrationSubmit;

    @Mock
    private RegistrationSubmitRequest mockRegistrationSubmitRequest;

    @Mock
    private WorldLineProperties.Piba mockPibaProperties;

    @Mock
    private WorldLineProperties.Piba.Service mockPibaServiceProperties;

    @Mock
    private TokenService mockAuthTokenService;

    @Mock
    private PibaGuidServiceClient mockPibaGuidServiceClient;

    @Mock
    private RegistrationSubmitResp mockRegistrationSubmitResp;

    @Mock
    private RegistrationSubmit registrationSubmit;

    @Mock
    private RegistrationSubmitResponseType mockRegistrationSubmitResponseType;

    @Mock
    private TetherDetailsType mockTetherDetailsType;

    @Mock
    private CdhProperties mockCdhProperties;

    @Mock
    private PibaAccountServiceClient pibaAccountServiceClient;

    @Mock
    private CdhClient cdhClient;

    @Mock
    private Executor pibaRegExecutor;

    @Mock
    private WorldlineUtils worldlineUtils;

    private final TestUtil testUtil = new TestUtil();

    @BeforeEach
    void setup(){
        when(mockWorldLineProperties.getPiba()).thenReturn(mockPibaProperties);
        when(mockPibaProperties.getService()).thenReturn(mockPibaServiceProperties);
        when(mockPibaServiceProperties.getUrl()).thenReturn(PIBA_REGISTRATIION_SERVICE_URL);
        when(worldlineUtils.serializeObject(any())).thenReturn("mockedSerializedObject");
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitResponse(mockRegistrationSubmitResponse))
                .thenReturn(mockRegistrationSubmitResp);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest))
                .thenReturn(registrationSubmit);
        when(mockCdhProperties.isEnableBbDataFetch()).thenReturn(false);
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(pibaRegExecutor).execute(any(Runnable.class));
    }

    @Test
    void submitReturnsSuccessfulResponse() {
        submitMocks();
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(
            createEmployeeDetails());
        RegistrationSubmitResp registrationSubmitRespActual = objectUnderTest.submit(
            mockRegistrationSubmitRequest, "Bearer Token random");
        submitAsserts(registrationSubmitRespActual);
    }

    @Test
    void submitCdhEnabledReturnsSuccessfulResponse() {
        submitMocks();
        when(mockCdhProperties.isEnableBbDataFetch()).thenReturn(true);
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(any())).thenReturn(
            createCdhEmployeeDetails());
        RegistrationSubmitResp registrationSubmitRespActual = objectUnderTest.submit(
            mockRegistrationSubmitRequest, "Bearer Token random");
        submitAsserts(registrationSubmitRespActual);
    }

    @Test
    void submitReturnsSuccessfulResponseDeCode(){
        RegistrationSubmitResp registrationSubmitResp = testUtil.buildRegistrationSubmitResp();

        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest)).thenReturn(mockRegistrationSubmit);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitResponse(mockRegistrationSubmitResponse)).thenReturn(registrationSubmitResp);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(
            createEmployeeDetails());
        when(mockRegistrationSubmitRequest.getRegistrationCode()).thenReturn(REGISTRATION_CODE_DE);
        when(mockWorldlineRegistrationTransformer.extractScheme(REGISTRATION_CODE_DE)).thenReturn(Scheme.DE);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegistrationSubmitResponse);

        doNothing().when(mockWorldLineRegistrationResponseValidator).validate(mockRegistrationSubmitResponse);

        when(mockRegistrationSubmitResponse.getResponse()).thenReturn(mockRegistrationSubmitResponseType);
        when(mockRegistrationSubmitResponseType.getTetherDetails()).thenReturn(mockTetherDetailsType);

        RegistrationSubmitResp registrationSubmitRespActual = objectUnderTest.submit(mockRegistrationSubmitRequest,"Bearer Token random");

        assertEquals(PRIMARY_SCHEME_CUSTOMER_ID,registrationSubmitRespActual.getRegistrationCodeInfo().getPrimarySchemeCustomerId());
        assertEquals(REGISTRATION_ROLE,registrationSubmitRespActual.getRegistrationCodeInfo().getRegistrationRole());
        assertEquals(TETHERED_USER_GUID,registrationSubmitRespActual.getTetherDetails().getTetheredUserGuid());
        verify(objectUnderTest).saveTetherInformation(any(),any(), eq(Scheme.DE));

    }

    @Test
    void submitReturnsSuccessfulResponseGBCode(){
        RegistrationSubmitResp registrationSubmitResp = testUtil.buildRegistrationSubmitResp();

        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest)).thenReturn(mockRegistrationSubmit);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitResponse(mockRegistrationSubmitResponse)).thenReturn(registrationSubmitResp);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(
            createEmployeeDetails());
        when(mockRegistrationSubmitRequest.getRegistrationCode()).thenReturn(REGISTRATION_CODE_GB);
        when(mockWorldlineRegistrationTransformer.extractScheme(REGISTRATION_CODE_GB)).thenReturn(Scheme.GB);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegistrationSubmitResponse);

        doNothing().when(mockWorldLineRegistrationResponseValidator).validate(mockRegistrationSubmitResponse);

        when(mockRegistrationSubmitResponse.getResponse()).thenReturn(mockRegistrationSubmitResponseType);
        when(mockRegistrationSubmitResponseType.getTetherDetails()).thenReturn(mockTetherDetailsType);

        RegistrationSubmitResp registrationSubmitRespActual = objectUnderTest.submit(mockRegistrationSubmitRequest,"Bearer Token random");

        assertEquals(PRIMARY_SCHEME_CUSTOMER_ID,registrationSubmitRespActual.getRegistrationCodeInfo().getPrimarySchemeCustomerId());
        assertEquals(REGISTRATION_ROLE,registrationSubmitRespActual.getRegistrationCodeInfo().getRegistrationRole());
        assertEquals(TETHERED_USER_GUID,registrationSubmitRespActual.getTetherDetails().getTetheredUserGuid());
        verify(objectUnderTest).saveTetherInformation(any(),any(), eq(Scheme.GB));
    }

    @Test
    void submitReturnsErrorResponse(){
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest)).thenReturn(mockRegistrationSubmit);
        doThrow(new PibaRegistrationException("Error")).when(mockWorldLineRegistrationResponseValidator).validate(mockRegistrationSubmitResponse);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(
            createEmployeeDetails());
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
                mockWorldLineProperties.getPiba().getService().getUrl(),
                mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest),
                mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegistrationSubmitResponse);

        //Then
        assertThatThrownBy(() -> objectUnderTest.submit(mockRegistrationSubmitRequest,"Bearer Token random"))
                .isInstanceOf(PibaRegistrationException.class)
                .hasMessageContaining("Error");
    }

    @Test
    void submit_shouldHandleErrorFromAuthService() {

        // When
        doThrow(new InValidTokenException("Error")).when(objectUnderTest).getEmployeeDetails(any());
        //Then
        assertThatThrownBy(() -> objectUnderTest.submit(mockRegistrationSubmitRequest,"Bearer Token"))
                .isInstanceOf(InValidTokenException.class)
                .hasMessageContaining("Error");
    }

    @Test
    void submit_withInnBusinessFlagTrue_shouldProcessForInnBusinessAndSaveUserAndCardGuid() {
        RegistrationSubmitResp registrationSubmitResp = testUtil.buildRegistrationSubmitResp();
        var wlResponse = createRegistrationSubmitResponse();
        ArgumentCaptor<TetheredUserRequest> tetheredUserRequestCaptor = ArgumentCaptor.forClass(TetheredUserRequest.class);

        when(mockRegistrationSubmitRequest.isInnBusiness()).thenReturn(true);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest)).thenReturn(mockRegistrationSubmit);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(createEmployeeDetails());
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(any())).thenReturn(createCdhEmployeeDetails());
        when(mockRegistrationSubmitRequest.getRegistrationCode()).thenReturn(REGISTRATION_CODE_GB);
        when(mockWorldlineRegistrationTransformer.extractScheme(REGISTRATION_CODE_GB)).thenReturn(Scheme.GB);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest),
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(wlResponse);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitResponse(wlResponse)).thenReturn(registrationSubmitResp);

        doNothing().when(mockWorldLineRegistrationResponseValidator).validate(wlResponse);

        ApplicationResponse applicationResponse = createFetchApplicationResponse();
        applicationResponse.setStage("Accepted");
        when(cdhClient.fetchApplication(any(), any(), any())).thenReturn(applicationResponse);
        when(cdhClient.getTetheredGuids(any(), any(), any())).thenReturn(List.of());
        doNothing().when(cdhClient).updateAppStatus(any(), any(), any());
        String token = "Bearer Token random";

        // Act
        objectUnderTest.submit(mockRegistrationSubmitRequest, token);

        // Assert
        verify(pibaAccountServiceClient, times(2)).registerTetheredUser(eq(token), tetheredUserRequestCaptor.capture());
        List<TetheredUserRequest> capturedRequests = tetheredUserRequestCaptor.getAllValues();
        assertEquals(2, capturedRequests.size());

        TetheredUserRequest firstRequest = capturedRequests.get(0);
        assertEquals(Scheme.GB, firstRequest.getScheme());
        assertEquals(COMPANY_ID, firstRequest.getCompanyId());
        assertEquals(EMPLOYEE_ID, firstRequest.getTetheredGuids().get(0).getEmployeeId());
        assertEquals(TETHERED_USER_GUID, firstRequest.getTetheredGuids().get(0).getTetheredGuid());

        TetheredUserRequest secondRequest = capturedRequests.get(1);
        assertEquals(Scheme.GB, secondRequest.getScheme());
        assertEquals(COMPANY_ID, secondRequest.getCompanyId());
        assertEquals(String.valueOf(CARD_EMPLOYEE_ID), secondRequest.getTetheredGuids().get(0).getEmployeeId());
        assertEquals(CARD_GUID, secondRequest.getTetheredGuids().get(0).getTetheredGuid());

        verify(cdhClient).fetchApplication(null, APP_GUID, USER_EMAIL);
        verify(cdhClient).updateAppStatus(applicationResponse.getApplicationId(), "Completed", USER_EMAIL);
    }

    @Test
    void submit_withInnBusinessFlagTrue_shouldProcessForInnBusinessAndSaveUserButNotSaveCardGuid() {
        RegistrationSubmitResp registrationSubmitResp = testUtil.buildRegistrationSubmitResp();
        var wlResponse = createRegistrationSubmitResponse();
        ArgumentCaptor<TetheredUserRequest> tetheredUserRequestCaptor = ArgumentCaptor.forClass(TetheredUserRequest.class);

        when(mockRegistrationSubmitRequest.isInnBusiness()).thenReturn(true);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest)).thenReturn(mockRegistrationSubmit);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(createEmployeeDetails());
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(any())).thenReturn(createCdhEmployeeDetails());
        when(mockRegistrationSubmitRequest.getRegistrationCode()).thenReturn(REGISTRATION_CODE_GB);
        when(mockWorldlineRegistrationTransformer.extractScheme(REGISTRATION_CODE_GB)).thenReturn(Scheme.GB);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest),
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(wlResponse);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitResponse(wlResponse)).thenReturn(registrationSubmitResp);

        doNothing().when(mockWorldLineRegistrationResponseValidator).validate(wlResponse);

        ApplicationResponse applicationResponse = createFetchApplicationResponse();
        applicationResponse.setStage("Outstanding");
        when(cdhClient.fetchApplication(any(), any(), any())).thenReturn(applicationResponse);
        when(cdhClient.getTetheredGuids(any(), any(), any()))
            .thenReturn(List.of(PibaTetheredGuidResponse.builder().tetheredGuid(CARD_GUID).build())); // Card already existing in CDH
        doNothing().when(cdhClient).updateAppStatus(any(), any(), any());
        String token = "Bearer Token random";

        // Act
        objectUnderTest.submit(mockRegistrationSubmitRequest, token);

        // Assert
        verify(pibaAccountServiceClient, times(1)).registerTetheredUser(eq(token), tetheredUserRequestCaptor.capture());
        List<TetheredUserRequest> capturedRequests = tetheredUserRequestCaptor.getAllValues();
        assertEquals(1, capturedRequests.size());

        TetheredUserRequest firstRequest = capturedRequests.get(0);
        assertEquals(Scheme.GB, firstRequest.getScheme());
        assertEquals(COMPANY_ID, firstRequest.getCompanyId());
        assertEquals(EMPLOYEE_ID, firstRequest.getTetheredGuids().get(0).getEmployeeId());
        assertEquals(TETHERED_USER_GUID, firstRequest.getTetheredGuids().get(0).getTetheredGuid());
        // No second request for card holder as it already exists in CDH

        verify(cdhClient).fetchApplication(null, APP_GUID, USER_EMAIL);
        verify(cdhClient).updateAppStatus(applicationResponse.getApplicationId(), "Completed", USER_EMAIL);
    }

    @Test
    void submit_withInnBusinessFlagTrue_shouldNotUpdateStatusToCompletedIfStatusIsApproved() {
        RegistrationSubmitResp registrationSubmitResp = testUtil.buildRegistrationSubmitResp();
        var wlResponse = createRegistrationSubmitResponse();
        ArgumentCaptor<TetheredUserRequest> tetheredUserRequestCaptor = ArgumentCaptor.forClass(TetheredUserRequest.class);

        when(mockRegistrationSubmitRequest.isInnBusiness()).thenReturn(true);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest)).thenReturn(mockRegistrationSubmit);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(createEmployeeDetails());
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(any())).thenReturn(createCdhEmployeeDetails());
        when(mockRegistrationSubmitRequest.getRegistrationCode()).thenReturn(REGISTRATION_CODE_GB);
        when(mockWorldlineRegistrationTransformer.extractScheme(REGISTRATION_CODE_GB)).thenReturn(Scheme.GB);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest),
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(wlResponse);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitResponse(wlResponse)).thenReturn(registrationSubmitResp);

        doNothing().when(mockWorldLineRegistrationResponseValidator).validate(wlResponse);

        ApplicationResponse applicationResponse = createFetchApplicationResponse();
        applicationResponse.setStage("Approved");
        when(cdhClient.fetchApplication(any(), any(), any())).thenReturn(applicationResponse);
        when(cdhClient.getTetheredGuids(any(), any(), any()))
            .thenReturn(List.of(PibaTetheredGuidResponse.builder().tetheredGuid(CARD_GUID).build())); // Card already existing in CDH
        doNothing().when(cdhClient).updateAppStatus(any(), any(), any());
        String token = "Bearer Token random";

        // Act
        objectUnderTest.submit(mockRegistrationSubmitRequest, token);

        // Assert
        verify(pibaAccountServiceClient, times(1)).registerTetheredUser(eq(token), tetheredUserRequestCaptor.capture());
        List<TetheredUserRequest> capturedRequests = tetheredUserRequestCaptor.getAllValues();
        assertEquals(1, capturedRequests.size());

        TetheredUserRequest firstRequest = capturedRequests.get(0);
        assertEquals(Scheme.GB, firstRequest.getScheme());
        assertEquals(COMPANY_ID, firstRequest.getCompanyId());
        assertEquals(EMPLOYEE_ID, firstRequest.getTetheredGuids().get(0).getEmployeeId());
        assertEquals(TETHERED_USER_GUID, firstRequest.getTetheredGuids().get(0).getTetheredGuid());
        // No second request for card holder as it already exists in CDH

        verify(cdhClient).fetchApplication(null, APP_GUID, USER_EMAIL);
        verify(cdhClient, times(0)).updateAppStatus(applicationResponse.getApplicationId(), "Completed", USER_EMAIL);
    }

    @Test
    void submit_shouldReturnAccountNumberSuccessfully() {
        // Arrange
        String expectedAccountNumber = "123456789";
        TetheredUserDetailsResponse tetheredUserDetailsResponse = new TetheredUserDetailsResponse();
        TetheredUserAccountOverview tetheredUserAccountOverview = new TetheredUserAccountOverview();
        tetheredUserAccountOverview.setAccountNumber(expectedAccountNumber);
        tetheredUserDetailsResponse.setCustomerAccountOverview(tetheredUserAccountOverview);
        RegistrationSubmitResp registrationSubmitResp = testUtil.buildRegistrationSubmitResp();
        var wlResponse = createRegistrationSubmitResponse();
        when(mockRegistrationSubmitRequest.isInnBusiness()).thenReturn(true);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest)).thenReturn(mockRegistrationSubmit);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any())).thenReturn(createEmployeeDetails());
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(any())).thenReturn(createCdhEmployeeDetails());
        when(mockRegistrationSubmitRequest.getRegistrationCode()).thenReturn(REGISTRATION_CODE_GB);
        when(mockWorldlineRegistrationTransformer.extractScheme(REGISTRATION_CODE_GB)).thenReturn(Scheme.GB);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest),
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(wlResponse);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitResponse(
            wlResponse)).thenReturn(registrationSubmitResp);

        doNothing().when(mockWorldLineRegistrationResponseValidator).validate(wlResponse);

        ApplicationResponse applicationResponse = createFetchApplicationResponse();
        when(cdhClient.fetchApplication(any(), any(), any())).thenReturn(applicationResponse);
        when(cdhClient.getTetheredGuids(any(), any(), any()))
            .thenReturn(List.of(PibaTetheredGuidResponse.builder().tetheredGuid(CARD_GUID)
                .build())); // Card already existing in CDH
        when(pibaAccountServiceClient.getTetheredUserDetailsWithScheme(anyString(), anyString(),
            anyString())).thenReturn(tetheredUserDetailsResponse);

        // Act
        RegistrationSubmitResp actualResponse = objectUnderTest.submit(
            mockRegistrationSubmitRequest, "Bearer Token random");

        // Assert
        assertEquals(expectedAccountNumber, actualResponse.getTetherDetails().getAccountNumber());
    }

    @Test
    void submit_shouldNotSetAccountNumberWhenNull() {
        // Arrange
        String expectedAccountNumber = "123456789";
        TetheredUserAccountOverview tetheredUserAccountOverview = new TetheredUserAccountOverview();
        tetheredUserAccountOverview.setAccountNumber(expectedAccountNumber);
        RegistrationSubmitResp registrationSubmitResp = testUtil.buildRegistrationSubmitResp();
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest))
            .thenReturn(mockRegistrationSubmit);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitResponse(mockRegistrationSubmitResponse))
            .thenReturn(registrationSubmitResp);
        when(mockAuthTokenService.retrieveEmployeeDetailsAndVerifyToken(any()))
            .thenReturn(createEmployeeDetails());
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(mockRegistrationSubmitRequest),
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegistrationSubmitResponse);
        doNothing().when(mockWorldLineRegistrationResponseValidator).validate(mockRegistrationSubmitResponse);
        when(mockRegistrationSubmitResponse.getResponse()).thenReturn(mockRegistrationSubmitResponseType);
        when(mockRegistrationSubmitResponseType.getTetherDetails()).thenReturn(mockTetherDetailsType);
        when(pibaAccountServiceClient.getTetheredUserDetails(anyString(), eq(null))).thenReturn(null);

        // Act
        RegistrationSubmitResp actualResponse = objectUnderTest.submit(mockRegistrationSubmitRequest, "Bearer Token random");

        // Assert
        assertNull(actualResponse.getTetherDetails().getAccountNumber());
    }

    private ApplicationResponse createFetchApplicationResponse() {
        ApplicationResponse applicationResponse = new ApplicationResponse();
        applicationResponse.setApplicationGuid(APP_GUID);
        applicationResponse.setCardHolders(List.of(CardHolder.builder().userGuid(CARD_GUID)
            .employeeId(CARD_EMPLOYEE_ID).build()));
        applicationResponse.setStage("Initialized");
        return applicationResponse;
    }

    private EmployeeDetails createEmployeeDetails(){
        return new EmployeeDetails(COMPANY_ID, EMPLOYEE_ID);
    }

    private CdhEmployeeDetails createCdhEmployeeDetails(){
        return CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID)
            .userEmail(USER_EMAIL)
            .build();
    }

    private void submitMocks() {
        RegistrationSubmitResp registrationSubmitResp = testUtil.buildRegistrationSubmitResp();
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(
            mockRegistrationSubmitRequest)).thenReturn(mockRegistrationSubmit);
        when(mockWorldlineRegistrationTransformer.toRegistrationSubmitResponse(
            mockRegistrationSubmitResponse)).thenReturn(registrationSubmitResp);
        when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
            mockWorldLineProperties.getPiba().getService().getUrl(),
            mockWorldlineRegistrationTransformer.toRegistrationSubmitSoapRequest(
                mockRegistrationSubmitRequest),
            mockWorldLineWebServiceMessageCallback
        )).thenReturn(mockRegistrationSubmitResponse);
        doNothing().when(mockWorldLineRegistrationResponseValidator)
            .validate(mockRegistrationSubmitResponse);
        doNothing().when(objectUnderTest).saveTetherInformation(any(), any(), any());
        when(mockRegistrationSubmitResponse.getResponse()).thenReturn(
            mockRegistrationSubmitResponseType);
        when(mockRegistrationSubmitResponseType.getTetherDetails()).thenReturn(
            mockTetherDetailsType);
    }

    private void submitAsserts(RegistrationSubmitResp response) {
        assertEquals(PRIMARY_SCHEME_CUSTOMER_ID,
            response.getRegistrationCodeInfo().getPrimarySchemeCustomerId());
        assertEquals(REGISTRATION_ROLE,
            response.getRegistrationCodeInfo().getRegistrationRole());
        assertEquals(TETHERED_USER_GUID,
            response.getTetherDetails().getTetheredUserGuid());
    }

    private RegistrationSubmitResponse createRegistrationSubmitResponse() {
        TetherDetailsType tetherDetails = new TetherDetailsType();
        tetherDetails.setTetheredUserGuid(TETHERED_USER_GUID);

        RegistrationSubmitResponseType responseType = new RegistrationSubmitResponseType();
        responseType.setTetherDetails(tetherDetails);
        responseType.setApplicationGuid(APP_GUID);

        RegistrationSubmitResponse response = new RegistrationSubmitResponse();
        response.setResponse(responseType);
        return response;
    }
}