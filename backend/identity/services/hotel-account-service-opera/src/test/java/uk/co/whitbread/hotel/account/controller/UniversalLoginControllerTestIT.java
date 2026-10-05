package uk.co.whitbread.hotel.account.controller;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static uk.co.whitbread.hotel.account.helper.AuthTestHelper.configureAuth0Context;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import uk.co.whitbread.hotel.account.model.PasswordResetResponse;
import uk.co.whitbread.hotel.account.model.SendEmailRequest;
import uk.co.whitbread.hotel.account.service.UniversalLoginService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UniversalLoginControllerTestIT {

    @LocalServerPort
    int serverPort;

    @MockitoSpyBean
    private UniversalLoginService universalLoginService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private TenantRepository tenantRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = serverPort;
        configureAuth0Context(tenantRepository, jwtDecoder);
    }

    @Test
    void sendEmailToLeisureAccount_BlockedAccount_shouldReturnAcceptedResponse() {
        // Given
        String email = "test@example.com";
        SendEmailRequest request = SendEmailRequest.builder()
                .messageType("blocked_account")
                .firstName("John")
                .lastName("Doe")
                .language("en")
                .build();

        doNothing()
                .when(universalLoginService)
                .sendEmailToLeisureAccount(anyString(), any(SendEmailRequest.class));

        // When & Then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .log().everything()
                .when()
                .post("/v1/hotel-account/universal-login/leisure/accounts/" + email + "/send-email")
                .then()
                .log().everything()
                .statusCode(HttpStatus.SC_ACCEPTED);

        verify(universalLoginService).sendEmailToLeisureAccount(email, request);
    }

    @Test
    void sendEmailToLeisureAccount_AccountRegistration_shouldReturnAcceptedResponse() {
        // Given
        String email = "test@example.com";
        SendEmailRequest request = SendEmailRequest.builder()
                .messageType("account_registration")
                .firstName("John")
                .lastName("Doe")
                .language("en")
                .build();

        doNothing()
                .when(universalLoginService)
                .sendEmailToLeisureAccount(anyString(), any(SendEmailRequest.class));

        // When & Then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .log().everything()
                .when()
                .post("/v1/hotel-account/universal-login/leisure/accounts/" + email + "/send-email")
                .then()
                .log().everything()
                .statusCode(HttpStatus.SC_ACCEPTED);

        verify(universalLoginService).sendEmailToLeisureAccount(email, request);
    }

    @Test
    void sendEmailToLeisureAccount_ChangePasswordConfirmation_shouldReturnAcceptedResponse() {
        // Given
        String email = "test@example.com";
        SendEmailRequest request = SendEmailRequest.builder()
                .messageType("change_password_confirmation")
                .firstName("John")
                .lastName("Doe")
                .language("en")
                .build();

        doNothing()
                .when(universalLoginService)
                .sendEmailToLeisureAccount(anyString(), any(SendEmailRequest.class));

        // When & Then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .log().everything()
                .when()
                .post("/v1/hotel-account/universal-login/leisure/accounts/" + email + "/send-email")
                .then()
                .log().everything()
                .statusCode(HttpStatus.SC_ACCEPTED);

        verify(universalLoginService).sendEmailToLeisureAccount(email, request);
    }

    @Test
    void sendEmailToLeisureAccount_ResetEmailByCode_shouldReturnAcceptedResponse() {
        // Given
        String email = "test@example.com";
        SendEmailRequest request = SendEmailRequest.builder()
                .messageType("reset_email_by_code")
                .firstName("John")
                .lastName("Doe")
                .language("en")
                .otp("123456")
                .build();

        doNothing()
                .when(universalLoginService)
                .sendEmailToLeisureAccount(anyString(), any(SendEmailRequest.class));

        // When & Then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .log().everything()
                .when()
                .post("/v1/hotel-account/universal-login/leisure/accounts/" + email + "/send-email")
                .then()
                .log().everything()
                .statusCode(HttpStatus.SC_ACCEPTED);

        verify(universalLoginService).sendEmailToLeisureAccount(email, request);
    }

    @Test
    void sendEmailToLeisureAccount_shouldReturnBadRequestForInvalidEmail() {
        // Given
        String invalidEmail = "invalid-email";
        SendEmailRequest request = SendEmailRequest.builder()
                .messageType("reset_email_by_code")
                .firstName("John")
                .lastName("Doe")
                .language("en")
                .build();

        // When & Then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .log().everything()
                .when()
                .post("/v1/hotel-account/universal-login/leisure/accounts/" + invalidEmail + "/send-email")
                .then()
                .log().everything()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void sendEmailToLeisureAccount_shouldReturnBadRequestForMissingMessageType() {
        // Given
        String email = "test@example.com";
        SendEmailRequest request = SendEmailRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .language("en")
                .build();

        // When & Then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .log().everything()
                .when()
                .post("/v1/hotel-account/universal-login/leisure/accounts/" + email + "/send-email")
                .then()
                .log().everything()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void sendEmailToLeisureAccount_shouldReturnBadRequestForMissingFirstName() {
        // Given
        String email = "test@example.com";
        SendEmailRequest request = SendEmailRequest.builder()
                .messageType("blocked_account")
                .lastName("Doe")
                .language("en")
                .build();

        // When & Then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .log().everything()
                .when()
                .post("/v1/hotel-account/universal-login/leisure/accounts/" + email + "/send-email")
                .then()
                .log().everything()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void sendEmailToLeisureAccount_shouldReturnBadRequestForMissingLastName() {
        // Given
        String email = "test@example.com";
        SendEmailRequest request = SendEmailRequest.builder()
                .messageType("blocked_account")
                .firstName("John")
                .language("en")
                .build();

        // When & Then
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .log().everything()
                .when()
                .post("/v1/hotel-account/universal-login/leisure/accounts/" + email + "/send-email")
                .then()
                .log().everything()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void getPasswordResetUrl_shouldReturnResetUrl() {
        // Given
        String email = "test@example.com";

        doReturn(PasswordResetResponse.builder().passwordResetUrl("test").build())
            .when(universalLoginService)
            .getPasswordResetUrl(anyString());

        // When & Then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .log().everything()
            .when()
            .header("authorization", "Bearer test")
            .get("/v1/hotel-account/universal-login/leisure/accounts/" + email + "/password-reset")
            .then()
            .log().everything()
            .statusCode(HttpStatus.SC_OK);

        verify(universalLoginService).getPasswordResetUrl(email);
    }

    @Test
    void getPasswordResetUrl_shouldReturnNotFoundForInvalidEmail() {
        // Given
        String invalidEmail = "invalid-email";

        doReturn(null)
            .when(universalLoginService)
            .getPasswordResetUrl(anyString());

        // When & Then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .log().everything()
            .when()
            .header("authorization", "Bearer test")
            .get("/v1/hotel-account/universal-login/leisure/accounts/" + invalidEmail + "/password-reset")
            .then()
            .log().everything()
            .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}




