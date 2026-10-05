package uk.co.whitbread.hotel.account.utils.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.bart.business.auth0.api.Employee2;
import uk.co.whitbread.bart.business.auth0.api.InitSessionRequest;
import uk.co.whitbread.bart.business.auth0.api.InitSessionResponse;
import uk.co.whitbread.bart.business.auth0.api.InitSessionResponse2;
import uk.co.whitbread.hotel.account.exceptions.InvalidLoginException;
import uk.co.whitbread.hotel.account.model.LoginRequestMapper;
import uk.co.whitbread.hotel.account.model.SessionRequest;
import uk.co.whitbread.hotel.account.model.SessionResponse;
import uk.co.whitbread.shared.auth.service.EncryptionService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.account.service.HotelLoginServiceTest.DECRYPTION_ERROR_MESSAGE;

@ExtendWith(MockitoExtension.class)
public class BartBBAuthTransformerTest {

    public static final String GUEST_HISTORY_NUMBER = "guest-history-number-test";
    public static final String GUEST_HISTORY_NUMBER_ENCRYPTED = "guest-history-number-encrypted-test";
    public static final String SESSION_ID = "sessionID";
    public static final String COMPANY_ID = "125";
    public static final String EMPLOYEE_ID = "10";
    public static final String ACCESS_LEVEL = "SUPER";
    @Mock
    private EncryptionService encryptionServiceMock;
    @Mock
    private LoginRequestMapper loginRequestMapper;

    @InjectMocks
    private BartBBAuthTransformer sut;


    @Test
    public void mapSessionRequest_shouldReturnInitSession() {
        //Given
        SessionRequest sessionRequest = new SessionRequest();
        sessionRequest.setGuestHistoryNumber(GUEST_HISTORY_NUMBER_ENCRYPTED);
        when(encryptionServiceMock.readSecuredMessage(GUEST_HISTORY_NUMBER_ENCRYPTED)).thenReturn(GUEST_HISTORY_NUMBER);

        //When
        uk.co.whitbread.bart.business.auth0.api.InitSession initSession = sut.transform(sessionRequest);

        //Then
        assertThat(initSession, notNullValue());
        InitSessionRequest initSessionRequest = initSession.getRequest();
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

        assertThrows(InvalidLoginException.class,
                () -> sut.transform(sessionRequest),
                DECRYPTION_ERROR_MESSAGE);
    }

    @Test
    public void mapInitSessionResponse_shouldReturnSessionResponse() {
        //Given
        InitSessionResponse initSessionResponse = new InitSessionResponse();
        InitSessionResponse2 initSessionResult = new InitSessionResponse2();
        initSessionResponse.setInitSessionResult(initSessionResult);
        initSessionResult.setSessionID(SESSION_ID);
        initSessionResult.setCompanyID(COMPANY_ID);
        Employee2 employee = new Employee2();
        employee.setEmployeeID(EMPLOYEE_ID);
        employee.setAccessLevel(ACCESS_LEVEL);
        initSessionResult.setEmployee(employee);

        //When
        SessionResponse sessionResponse = sut.transform(initSessionResponse);
        assertThat(sessionResponse, notNullValue());
        assertThat(sessionResponse.getSessionId(), is(equalTo(SESSION_ID)));
        assertThat(sessionResponse.getCompanyId(), is(equalTo(COMPANY_ID)));
        assertThat(sessionResponse.getEmployeeId(), is(equalTo(EMPLOYEE_ID)));
        assertThat(sessionResponse.getAccessLevel(), is(equalTo(ACCESS_LEVEL)));
    }
}
