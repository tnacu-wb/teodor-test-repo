package uk.co.whitbread.hotel.account.controller;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.account.exceptions.InvalidLoginException;
import uk.co.whitbread.hotel.account.model.LoginRequest;
import uk.co.whitbread.hotel.account.model.LoginResponse;
import uk.co.whitbread.hotel.account.model.LogoutRequest;
import uk.co.whitbread.hotel.account.model.LogoutResponse;
import uk.co.whitbread.hotel.account.model.SessionRequest;
import uk.co.whitbread.hotel.account.model.SessionResponse;
import uk.co.whitbread.hotel.account.service.HotelLoginService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class HotelLoginControllerTestIT {

    public static final String GUEST_HISTORY_NUMBER = "guest-history-number-test";
    public static final String SESSION_ID = "asdfasdfwq3432423";
    @LocalServerPort
    int serverPort;

    @MockitoBean
    private HotelLoginService mockHotelLoginService;

    @BeforeEach
    public void setUp()  {
        RestAssured.port = serverPort;
    }

    @Test
    public void login_shouldReturnOK()  {
        //Given
        LoginRequest request = new LoginRequest();
        request.setUsername("methmal@gmail.com");
        request.setPassword("Oxford34circus");

        LoginResponse response = new LoginResponse();
        response.setLoginSuccessful(true);
        response.setSessionId("asdfasdfwq3432423");

        when(mockHotelLoginService.login(any(LoginRequest.class), any(Boolean.class))).
                thenReturn(response);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                post("/auth/hotels/login?bookingChannel=PI").
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK).
                body("loginSuccessful", is(equalTo(true)));
    }

    @Test
    public void login_should_log_warning_when_InvalidExceptionIsThrown_PI()  {
        //Given
        LoginRequest request = new LoginRequest();
        request.setUsername("methmal@gmail.com");
        request.setPassword("Oxford34circus");

        LoginResponse response = new LoginResponse();
        response.setLoginSuccessful(true);
        response.setSessionId("asdfasdfwq3432423");

        when(mockHotelLoginService.login(any(LoginRequest.class), any(Boolean.class))).thenThrow(new InvalidLoginException("Login Failed"));


        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                post("/auth/hotels/login?bookingChannel=PI").
                then().
                log().everything().
                statusCode(HttpStatus.SC_UNAUTHORIZED).
                body("code", equalTo("025")).
                body("details", hasItem("Login Failed"));
    }

    @Test
    public void login_should_log_warning_when_InvalidExceptionIsThrown_BB()  {
        //Given
        LoginRequest request = new LoginRequest();
        request.setUsername("methmal@gmail.com");
        request.setPassword("Oxford34circus");

        LoginResponse response = new LoginResponse();
        response.setLoginSuccessful(true);
        response.setSessionId("asdfasdfwq3432423");

        when(mockHotelLoginService.login(any(LoginRequest.class), any(Boolean.class))).thenThrow(new InvalidLoginException("Login Failed"));


        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                post("/auth/hotels/login?bookingChannel=BB").
                then().
                log().everything().
                statusCode(HttpStatus.SC_UNAUTHORIZED).
                body("code", equalTo("025")).
                body("details", hasItem("Login Failed"));
    }


    @Test
    public void login_shouldReturnNotOK()  {
        //Given
        LoginRequest request = new LoginRequest();
        request.setUsername("methmal@gmail.com");

        LoginResponse response = new LoginResponse();

        when(mockHotelLoginService.login(any(LoginRequest.class), any(Boolean.class))).
                thenReturn(response);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                post("/auth/hotels/login?bookingChannel=PI").
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("details", hasItem("password must not be empty"));
    }

    @Test
    public void logout_shouldReturnOK() {
        //Given
        LogoutRequest request = new LogoutRequest();
        request.setSessionId("testSessionId");

        LogoutResponse response = new LogoutResponse(true);
        when(mockHotelLoginService.logout(any(LogoutRequest.class))).thenReturn(response);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                post("/auth/hotels/logout").
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK).
                body("logoutSuccessful", is(equalTo(true)));
    }

    @Test
    public void initSession_pi_shouldReturnOK()  {
        //Given
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(GUEST_HISTORY_NUMBER);

        SessionResponse response = new SessionResponse();
        response.setSessionId(SESSION_ID);

        when(mockHotelLoginService.initiateSession(request, false)).
                thenReturn(response);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                queryParam("business", false).
                post("/auth/session").
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK).
                body("sessionId", is(equalTo(SESSION_ID)));
    }

    @Test
    public void initSession_pi_shouldFailOnBadRequest()  {
        //Given
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(null);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                queryParam("business", false).
                post("/auth/session").
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void initSession_pi_shouldThrowInvalidLoginException()  {
        //Given
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(GUEST_HISTORY_NUMBER);

        when(mockHotelLoginService.initiateSession(request, false))
                .thenThrow(InvalidLoginException.class);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                queryParam("business", false).
                post("/auth/session").
                then().
                log().everything().
                statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void initSession_bb_shouldReturnOK()  {
        //Given
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(GUEST_HISTORY_NUMBER);

        SessionResponse response = new SessionResponse();
        response.setSessionId(SESSION_ID);

        when(mockHotelLoginService.initiateSession(request, true)).
                thenReturn(response);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                queryParam("business", true).
                post("/auth/session").
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK).
                body("sessionId", is(equalTo(SESSION_ID)));
    }

    @Test
    public void initSession_bb_shouldFailOnBadRequest()  {
        //Given
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(null);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                queryParam("business", true).
                post("/auth/session").
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void initSession_bb_shouldThrowInvalidLoginException()  {
        //Given
        SessionRequest request = new SessionRequest();
        request.setGuestHistoryNumber(GUEST_HISTORY_NUMBER);

        when(mockHotelLoginService.initiateSession(request, true))
                .thenThrow(InvalidLoginException.class);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                queryParam("business", true).
                post("/auth/session").
                then().
                log().everything().
                statusCode(HttpStatus.SC_UNAUTHORIZED);
    }
}
