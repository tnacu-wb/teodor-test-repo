package uk.co.whitbread.hotel.account.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.bart.auth0.api.InitSession;
import uk.co.whitbread.bart.auth0.api.InitSessionRequest;
import uk.co.whitbread.bart.auth0.api.InitSessionResponse;
import uk.co.whitbread.bart.auth0.api.InitSessionResponse2;
import uk.co.whitbread.bart.booking.api.ClearSessionRequest;
import uk.co.whitbread.bart.booking.api.ClearSessionRequestResponse;
import uk.co.whitbread.bart.business.api.Employee;
import uk.co.whitbread.bart.business.api.UserLogin;
import uk.co.whitbread.bart.business.api.UserLoginResponse;
import uk.co.whitbread.bart.business.api.UserLoginResponse2;
import uk.co.whitbread.bart.exceptions.BartServiceException;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuest;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginRequest;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginRequestResponse;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginResponse;
import uk.co.whitbread.bart.security.LoginWebServiceMessageCallback;
import uk.co.whitbread.hotel.account.exceptions.InvalidLoginException;
import uk.co.whitbread.hotel.account.model.*;
import uk.co.whitbread.hotel.account.properties.BartProperties;
import uk.co.whitbread.hotel.account.service.auth0.Auth0BusinessService;
import uk.co.whitbread.hotel.account.service.auth0.Auth0LeisureService;
import uk.co.whitbread.hotel.account.utils.auth.BartAuthResponseValidator;
import uk.co.whitbread.hotel.account.utils.auth.BartAuthTransformer;
import uk.co.whitbread.hotel.account.utils.auth.BartBBAuthTransformer;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class HotelLoginServiceTest {

    public static final String MOCK_GHN_SERVICE_URL = "mockGhnServiceUrl";
    public static final String MOCK_BB_GHN_SERVICE_URL = "mockBbGhnServiceUrl";
    public static final String MOCK_BART_BOOKING_SERVICE_URL = "mockBartBookingServiceUrl";
    public static final String MOCK_SERVICE_BB_URL = "mockServiceBBUrl";
    public static final String MOCK_SERVICE_URL = "mockServiceUrl";
    public static final String ENCRYPTED_GUEST_HISTORY_NUMBER = "encrypted-guest-history-number-test";
    public static final String DECRYPTED_GUEST_HISTORY_NUMBER = "decrypted-guest-history-number-test";
    public static final String SESSION_ID = "sessionId";
    public static final String REMOTE_ADDRESS = "remote-address-test";
    public static final String REMOTE_ADDRESS_2 = "remote-address-2-test";
    public static final String REMOTE_ADDRESS_3 = "remote-address-3-test";
    public static final String DECRYPTION_ERROR_MESSAGE = "Invalid guest history number";
    @Mock
    private BartAuthTransformer mockBartAuthTransformer;
    @Mock
    private BartBBAuthTransformer mockBartBBAuthTransformer;
    @Mock
    private WebServiceTemplate mockWebServiceTemplate;
    @Mock
    private BartProperties mockBartProperties;
    @Mock
    private BartAuthResponseValidator mockBartAuthResponseValidator;
    @Mock
    private LoginRequest mockLoginRequest;
    @Mock
    private LoginResponse mockLoginResponse;
    @Mock
    private RegisteredGuestLoginRequest mockRegisteredGuestLoginRequest;
    @Mock
    private RegisteredGuestLoginRequestResponse mockRegisteredGuestLoginRequestResponse;
    @Mock
    private ClearSessionRequest mockClearSessionRequest;
    @Mock
    private ClearSessionRequestResponse mockClearSessionRequestResponse;
    @Mock
    private LogoutRequest mockLogoutRequest;
    @Mock
    private UserLogin mockUserLogin;
    @Mock
    private UserLoginResponse mockUserLoginResponse;
    @Mock
    private LoginWebServiceMessageCallback mockLoginWebServiceMessageCallback;
    @Mock
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private Auth0LeisureService auth0LeisureService;
    @Mock
    private Auth0BusinessService auth0BusinessService;

    @Spy
    @InjectMocks
    private HotelLoginService sut;

    @BeforeEach
    public void setUp() throws Exception {
        when(mockBartProperties.getPiGuestServiceUrl()).thenReturn(MOCK_SERVICE_URL);
        when(mockBartProperties.getBbClientServiceUrl()).thenReturn(MOCK_SERVICE_BB_URL);
        when(mockBartProperties.getBartBookingServiceUrl()).thenReturn(MOCK_BART_BOOKING_SERVICE_URL);
        when(mockBartProperties.getPiInitSessionAuth0ServiceUrl()).thenReturn(MOCK_GHN_SERVICE_URL);
        when(mockBartProperties.getBbInitSessionAuth0ServiceUrl()).thenReturn(MOCK_BB_GHN_SERVICE_URL);
        when(mockLogoutRequest.getSessionId()).thenReturn(SESSION_ID);


        //LOGIN
        when(mockBartAuthTransformer.transform(mockLoginRequest)).thenReturn(mockRegisteredGuestLoginRequest);
        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_URL),
                any(RegisteredGuestLoginRequest.class),
                any(WebServiceMessageCallback.class))).thenReturn(mockRegisteredGuestLoginRequestResponse);
        when(mockBartAuthResponseValidator.validate(mockRegisteredGuestLoginRequestResponse)).thenReturn(Optional.empty());

        //LOGIN BB
        when(mockBartBBAuthTransformer.transform(mockLoginRequest)).thenReturn(mockUserLogin);
        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_BB_URL),
                any(UserLogin.class),
                any(WebServiceMessageCallback.class))).thenReturn(mockUserLoginResponse);
        when(mockBartAuthResponseValidator.validate(mockUserLoginResponse)).thenReturn(Optional.empty());


        //LOGOUT
        when(mockBartAuthTransformer.transform(SESSION_ID)).thenReturn(mockClearSessionRequest);
        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_BART_BOOKING_SERVICE_URL),
                any(ClearSessionRequest.class),
                any(WebServiceMessageCallback.class)))
                .thenReturn(mockClearSessionRequestResponse);

        when(mockBartAuthResponseValidator.validate(mockClearSessionRequestResponse)).thenReturn(Optional.empty());
    }

    @Test
    public void login_shouldMakeRequest() {
        RegisteredGuestLoginRequestResponse bartResponse = createBartLoginResponse();

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_URL),
                any(RegisteredGuestLoginRequest.class),
                any(WebServiceMessageCallback.class)))
                .thenReturn(bartResponse);
        when(mockBartAuthTransformer.transform(bartResponse)).
                thenReturn(mockLoginResponse);

        //When
        LoginResponse loginResponse = sut.login(mockLoginRequest, false);

        //Then
        assertThat(loginResponse, is(not(nullValue())));
    }

    @Test
    public void login_shouldHandleSoapFaultClientException() {

        SoapFaultClientException mockSoapFaultException = mock(SoapFaultClientException.class);
        SoapFault mockSoapFault = mock(SoapFault.class);
        when(mockSoapFaultException.getSoapFault()).thenReturn(mockSoapFault);

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_URL),
                any(RegisteredGuestLoginRequest.class),
                any(WebServiceMessageCallback.class))).
                thenThrow(mockSoapFaultException);

        assertThrows(BartServiceException.class,
                () -> sut.login(mockLoginRequest, false));
    }

    @Test
    public void login_shouldHandleErrorFromBart() {

        when(mockBartAuthResponseValidator.validate(mockRegisteredGuestLoginRequestResponse)).thenReturn(Optional.of("Error"));

        assertThrows(InvalidLoginException.class,
                () -> sut.login(mockLoginRequest, false),
                "Error");
    }

    @Test
    public void login_bb_shouldMakeRequest() {
        UserLoginResponse bartBBResponse = createBartBBLoginResponse();

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_BB_URL),
                any(UserLogin.class),
                any(LoginWebServiceMessageCallback.class)))
                .thenReturn(bartBBResponse);
        when(mockBartBBAuthTransformer.transform(bartBBResponse)).
                thenReturn(mockLoginResponse);

        //When
        LoginResponse loginResponse = sut.login(mockLoginRequest, true);

        //Then
        assertThat(loginResponse, is(not(nullValue())));
        assertThat(loginResponse, is(equalTo(mockLoginResponse)));
    }

    @Test
    public void login_bb_shouldHandleSoapFaultClientException() {

        SoapFaultClientException mockSoapFaultException = mock(SoapFaultClientException.class);
        SoapFault mockSoapFault = mock(SoapFault.class);
        when(mockSoapFaultException.getSoapFault()).thenReturn(mockSoapFault);

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_BB_URL),
                any(UserLogin.class),
                any(WebServiceMessageCallback.class))).
                thenThrow(mockSoapFaultException);

        assertThrows(BartServiceException.class,
                () -> sut.login(mockLoginRequest, true));
    }

    @Test
    public void login_bb_shouldHandleErrorFromBart() {

        when(mockBartAuthResponseValidator.validate(mockUserLoginResponse)).thenReturn(Optional.of("Error"));

        assertThrows(InvalidLoginException.class,
                () -> sut.login(mockLoginRequest, true),
                "Error");
    }

    @Test
    public void logout_shouldLogout() {

        //When
        LogoutResponse logoutResponse = sut.logout(mockLogoutRequest);

        //Then
        assertThat(logoutResponse.isLogoutSuccessful(), is(equalTo(true)));
    }

    @Test
    public void logout_shouldHandleSoapFaultClientException() {

        SoapFaultClientException mockSoapFaultException = mock(SoapFaultClientException.class);
        SoapFault mockSoapFault = mock(SoapFault.class);

        when(mockSoapFaultException.getSoapFault()).thenReturn(mockSoapFault);

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_BART_BOOKING_SERVICE_URL),
                any(ClearSessionRequest.class),
                any(WebServiceMessageCallback.class)))
                .thenThrow(mockSoapFaultException);

        assertThrows(BartServiceException.class,
                () -> sut.logout(mockLogoutRequest));
    }

    @Test
    public void logout_shouldHandleErrorFromBart() {

        when(mockBartAuthResponseValidator.validate(mockClearSessionRequestResponse)).thenReturn(Optional.of("Error"));

        assertThrows(BartServiceException.class,
            () -> sut.logout(mockLogoutRequest),
            "Error");

    }

    @Test
    public void initiateSession_pi_shouldReturnSessionId() {
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(ENCRYPTED_GUEST_HISTORY_NUMBER);

        InitSession initSession = createPiInitSession();
        InitSessionResponse initSessionResponse = createPiInitSessionResponse();
        SessionResponse sessionResponse = createSessionResponse();

        when(mockWebServiceTemplate.marshalSendAndReceive(
                eq(MOCK_GHN_SERVICE_URL),
                any(InitSession.class),
                any(WebServiceMessageCallback.class)))
                .thenReturn(initSessionResponse);

        when(mockBartAuthResponseValidator.validate(initSessionResponse)).thenReturn(Optional.empty());
        when(mockBartAuthTransformer.transform(initSessionResponse)).thenReturn(sessionResponse);
        when(mockBartAuthTransformer.transform(request)).thenReturn(initSession);

        SessionResponse response = sut.initiateSession(request, false);

        assertThat(response, notNullValue());
        assertThat(response, sameInstance(sessionResponse));
        verify(mockWebServiceTemplate).marshalSendAndReceive(
                eq(MOCK_GHN_SERVICE_URL),
                eq(initSession),
                any(WebServiceMessageCallback.class));
    }

    @Test
    public void initiateSession_pi_shouldHandleBartError() {
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(ENCRYPTED_GUEST_HISTORY_NUMBER);

        InitSession initSession = createPiInitSession();
        InitSessionResponse initSessionResponse = createPiInitSessionResponse();

        when(mockWebServiceTemplate.marshalSendAndReceive(
                eq(MOCK_GHN_SERVICE_URL),
                any(InitSession.class),
                any(WebServiceMessageCallback.class)))
                .thenReturn(initSessionResponse);

        when(mockBartAuthResponseValidator.validate(initSessionResponse)).thenReturn(Optional.of("Error"));
        when(mockBartAuthTransformer.transform(request)).thenReturn(initSession);

        assertThrows(InvalidLoginException.class,
                () -> sut.initiateSession(request, false),
                "Error");
    }

    @Test
    public void initiateSession_pi_shouldHandleDecryptionError() {
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(ENCRYPTED_GUEST_HISTORY_NUMBER);

        when(mockBartAuthTransformer.transform(request)).thenThrow(new InvalidLoginException(DECRYPTION_ERROR_MESSAGE));

        assertThrows(InvalidLoginException.class,
                () -> sut.initiateSession(request, false),
                DECRYPTION_ERROR_MESSAGE);
    }

    @Test
    public void initiateSession_bb_shouldReturnSessionId() {
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(ENCRYPTED_GUEST_HISTORY_NUMBER);

        uk.co.whitbread.bart.business.auth0.api.InitSession initSession = createBBInitSession();
        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse initSessionResponse = createBbInitSessionResponse();
        SessionResponse sessionResponse = createSessionResponse();

        when(mockWebServiceTemplate.marshalSendAndReceive(
                eq(MOCK_BB_GHN_SERVICE_URL),
                any(uk.co.whitbread.bart.business.auth0.api.InitSession.class),
                any(WebServiceMessageCallback.class)))
                .thenReturn(initSessionResponse);

        when(mockBartAuthResponseValidator.validate(initSessionResponse)).thenReturn(Optional.empty());
        when(mockBartBBAuthTransformer.transform(initSessionResponse)).thenReturn(sessionResponse);
        when(mockBartBBAuthTransformer.transform(request)).thenReturn(initSession);

        SessionResponse response = sut.initiateSession(request, true);

        assertThat(response, notNullValue());
        assertThat(response, sameInstance(sessionResponse));
        verify(mockWebServiceTemplate).marshalSendAndReceive(
                eq(MOCK_BB_GHN_SERVICE_URL),
                eq(initSession),
                any(WebServiceMessageCallback.class));
    }

    @Test
    public void initiateSession_bb_shouldHandleBartError() {
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(ENCRYPTED_GUEST_HISTORY_NUMBER);

        uk.co.whitbread.bart.business.auth0.api.InitSession initSession = createBBInitSession();
        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse initSessionResponse = createBbInitSessionResponse();

        when(mockWebServiceTemplate.marshalSendAndReceive(
                eq(MOCK_BB_GHN_SERVICE_URL),
                any(uk.co.whitbread.bart.business.auth0.api.InitSession.class),
                any(WebServiceMessageCallback.class)))
                .thenReturn(initSessionResponse);

        when(mockBartAuthResponseValidator.validate(initSessionResponse)).thenReturn(Optional.of("Error"));
        when(mockBartBBAuthTransformer.transform(request)).thenReturn(initSession);

        assertThrows(InvalidLoginException.class,
                () -> sut.initiateSession(request, true),
                "Error");

    }

    @Test
    public void initiateSession_bb_shouldHandleDecryptionError() {
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(ENCRYPTED_GUEST_HISTORY_NUMBER);

        when(mockBartBBAuthTransformer.transform(request)).thenThrow(new InvalidLoginException(DECRYPTION_ERROR_MESSAGE));

        assertThrows(InvalidLoginException.class,
                () -> sut.initiateSession(request, true),
                DECRYPTION_ERROR_MESSAGE);
    }

    private RegisteredGuestLoginRequestResponse createBartLoginResponse() {
        RegisteredGuestLoginRequestResponse bartResponse = new RegisteredGuestLoginRequestResponse();
        RegisteredGuestLoginResponse registeredGuestLoginResponse = new RegisteredGuestLoginResponse();
        RegisteredGuest registeredGuest = new RegisteredGuest();
        registeredGuest.setGuestHistoryNumber(ENCRYPTED_GUEST_HISTORY_NUMBER);
        registeredGuestLoginResponse.setGuestDetails(registeredGuest);
        bartResponse.setRegisteredGuestLoginRequestResult(registeredGuestLoginResponse);
        return bartResponse;
    }

    private UserLoginResponse createBartBBLoginResponse() {
        UserLoginResponse bartBBResponse = new UserLoginResponse();
        UserLoginResponse2 response2 = new UserLoginResponse2();
        Employee employee = new Employee();
        employee.setGhNumber(ENCRYPTED_GUEST_HISTORY_NUMBER);
        response2.setEmployee(employee);
        bartBBResponse.setUserLoginResult(response2);
        return bartBBResponse;
    }

    private InitSession createPiInitSession() {
        InitSession initSession = new InitSession();
        InitSessionRequest initSessionRequest = new InitSessionRequest();
        initSessionRequest.setGuestHistoryNumber(DECRYPTED_GUEST_HISTORY_NUMBER);
        initSession.setInitSessionRequest(initSessionRequest);
        return initSession;
    }

    private uk.co.whitbread.bart.business.auth0.api.InitSession createBBInitSession() {
        uk.co.whitbread.bart.business.auth0.api.InitSession initSession = new uk.co.whitbread.bart.business.auth0.api.InitSession();
        uk.co.whitbread.bart.business.auth0.api.InitSessionRequest initSessionRequest = new uk.co.whitbread.bart.business.auth0.api.InitSessionRequest();
        initSessionRequest.setGuestHistoryNumber(DECRYPTED_GUEST_HISTORY_NUMBER);
        initSession.setRequest(initSessionRequest);
        return initSession;
    }

    private InitSessionResponse createPiInitSessionResponse() {
        InitSessionResponse initSessionResponse = new InitSessionResponse();
        InitSessionResponse2 initSessionResponse2 = new InitSessionResponse2();
        initSessionResponse2.setSessionID(SESSION_ID);
        initSessionResponse.setInitSessionResult(initSessionResponse2);
        return initSessionResponse;
    }

    private uk.co.whitbread.bart.business.auth0.api.InitSessionResponse createBbInitSessionResponse() {
        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse initSessionResponse = new uk.co.whitbread.bart.business.auth0.api.InitSessionResponse();
        uk.co.whitbread.bart.business.auth0.api.InitSessionResponse2 initSessionResponse2 = new uk.co.whitbread.bart.business.auth0.api.InitSessionResponse2();
        initSessionResponse2.setSessionID(SESSION_ID);
        initSessionResponse.setInitSessionResult(initSessionResponse2);
        return initSessionResponse;
    }

    private SessionResponse createSessionResponse() {
        SessionResponse sessionResponse = new SessionResponse();
        sessionResponse.setSessionId(SESSION_ID);
        return sessionResponse;
    }
}
