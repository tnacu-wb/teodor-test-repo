package uk.co.whitbread.hotel.account.utils.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.bart.auth0.api.InitSession;
import uk.co.whitbread.bart.auth0.api.InitSessionRequest;
import uk.co.whitbread.bart.auth0.api.InitSessionResponse;
import uk.co.whitbread.bart.auth0.api.InitSessionResponse2;
import uk.co.whitbread.bart.booking.api.ClearSessionRequest;
import uk.co.whitbread.bart.booking.api.ClearSessionRequest2;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginRequest;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginRequestResponse;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginResponse;
import uk.co.whitbread.hotel.account.exceptions.InvalidLoginException;
import uk.co.whitbread.hotel.account.model.*;
import uk.co.whitbread.shared.auth.service.EncryptionService;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.account.service.HotelLoginServiceTest.DECRYPTION_ERROR_MESSAGE;

@ExtendWith(MockitoExtension.class)
class BartAuthTransformerTest {

    public static final String GUEST_HISTORY_NUMBER = "guest-history-number-test";
    public static final String GUEST_HISTORY_NUMBER_ENCRYPTED = "guest-history-number-encrypted-test";
    public static final String SESSION_ID = "sessionID";

    private ObjectMapper objectMapper;
    @Mock
    private EncryptionService encryptionServiceMock;

    @Spy
    private LoginRequestMapper loginRequestMapper = Mappers.getMapper(LoginRequestMapper.class);

    @InjectMocks
    private BartAuthTransformer sut;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        sut = new BartAuthTransformer(loginRequestMapper, encryptionServiceMock);
    }


    @Test
    public void shouldMapRegisteredGuestLoginRequest() throws Exception {
        //Given
        LoginRequest request = objectMapper.readValue(
                new File("src/test/resources/mapping/LoginRequest.json"), LoginRequest.class);

        RegisteredGuestLoginRequest expectedBartRequest = objectMapper.readValue(
                new File("src/test/resources/mapping/RegisteredGuestLoginRequest.json"),
                RegisteredGuestLoginRequest.class);

        //When
        RegisteredGuestLoginRequest bartRequest = sut.transform(request);

        //Then
        assertAll("Test bartRequest with expectedBartRequest equality",
            () -> assertEquals(bartRequest.getRegisteredGuestLoginDetails().getLoginMethod(), expectedBartRequest.getRegisteredGuestLoginDetails().getLoginMethod()),
            () -> assertEquals(bartRequest.getRegisteredGuestLoginDetails().getPassword(), expectedBartRequest.getRegisteredGuestLoginDetails().getPassword()),
            () -> assertEquals(bartRequest.getRegisteredGuestLoginDetails().getEmailAddress(), expectedBartRequest.getRegisteredGuestLoginDetails().getEmailAddress()),
            () -> assertEquals(bartRequest.getRegisteredGuestLoginDetails().getTelephoneNumber(), expectedBartRequest.getRegisteredGuestLoginDetails().getTelephoneNumber()),
            () -> assertEquals(bartRequest.getRegisteredGuestLoginDetails().getPin(), expectedBartRequest.getRegisteredGuestLoginDetails().getPin()));
    }

    @Test
    public void shouldMapLoginResponse() {
        //Given
        RegisteredGuestLoginRequestResponse bartResponse = new RegisteredGuestLoginRequestResponse();
        RegisteredGuestLoginResponse result = new RegisteredGuestLoginResponse();
        result.setSessionID("testSessionId");
        bartResponse.setRegisteredGuestLoginRequestResult(result);

        //When
        LoginResponse response = sut.transform(bartResponse);

        //Then
        assertEquals(response.getSessionId(),"testSessionId");
        assertTrue(response.isLoginSuccessful());
    }

    @Test
    public void shouldMapClearSessionRequest() {
        //Given
        ClearSessionRequest csrequest = new ClearSessionRequest();
        ClearSessionRequest2 innerRequest = new ClearSessionRequest2();
        innerRequest.setSessionID("sessionID");
        csrequest.setClearSessionDetails(innerRequest);

        //When
        ClearSessionRequest request = sut.transform(SESSION_ID);

        //Then
        assertEquals(request.getClearSessionDetails().getSessionID(), SESSION_ID);
    }

    @Test
    public void shouldMapSessionRequest() {
        //Given
        SessionRequest sessionRequest = new SessionRequest();
        sessionRequest.setGuestHistoryNumber(GUEST_HISTORY_NUMBER_ENCRYPTED);
        when(encryptionServiceMock.readSecuredMessage(GUEST_HISTORY_NUMBER_ENCRYPTED)).thenReturn(GUEST_HISTORY_NUMBER);

        //When
        InitSession initSession = sut.transform(sessionRequest);

        //Then
        assertThat(initSession, notNullValue());
        InitSessionRequest initSessionRequest = initSession.getInitSessionRequest();
        assertThat(initSessionRequest, notNullValue());
        assertThat(initSessionRequest.getGuestHistoryNumber(), is(equalTo(GUEST_HISTORY_NUMBER)));
    }

    @Test
    public void mapSessionRequest_shouldThrowInvalidLoginException() {
        //Given
        SessionRequest sessionRequest = new SessionRequest();
        sessionRequest.setGuestHistoryNumber(GUEST_HISTORY_NUMBER_ENCRYPTED);
        when(encryptionServiceMock.readSecuredMessage(GUEST_HISTORY_NUMBER_ENCRYPTED))
                .thenThrow(new InvalidLoginException(DECRYPTION_ERROR_MESSAGE));

        //When
        assertThrows(InvalidLoginException.class,
                () -> sut.transform(sessionRequest),
                DECRYPTION_ERROR_MESSAGE);
    }

    @Test
    public void shouldMapInitSessionResponse() {
        //Given
        InitSessionResponse initSessionResponse = new InitSessionResponse();
        InitSessionResponse2 initSessionResult = new InitSessionResponse2();
        initSessionResponse.setInitSessionResult(initSessionResult);
        initSessionResult.setSessionID(SESSION_ID);

        //When
        SessionResponse sessionResponse = sut.transform(initSessionResponse);
        assertThat(sessionResponse, notNullValue());
        assertThat(sessionResponse.getSessionId(), is(equalTo(SESSION_ID)));
    }
}
