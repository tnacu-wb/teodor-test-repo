package uk.co.whitbread.hotel.account.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static uk.co.whitbread.common.exceptions.ErrorCodes.AUTH0_GENERIC_ERROR_CODE;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import uk.co.whitbread.hotel.account.model.ChangePasswordResponse;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordRequest;
import uk.co.whitbread.hotel.account.model.ResetPasswordRequest;
import uk.co.whitbread.hotel.account.model.ValidateResetKeyRequest;
import uk.co.whitbread.hotel.account.model.ValidateResetKeyResponse;
import uk.co.whitbread.hotel.account.service.HotelAuthServiceCdh;
import uk.co.whitbread.shared.auth.service.ManagementService;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HotelAuthControllerTestIT {
    private static final String LANGUAGE = "en";

    @LocalServerPort
    int serverPort;

    @MockitoSpyBean
    private HotelAuthServiceCdh mockHotelAuthServiceCdh;

    @MockitoSpyBean(name = "leisureManagementService")
    private ManagementService managementService;

    @BeforeEach
    void setUp() {
        RestAssured.port = serverPort;
    }

    @Test
    void forgotPassword_shouldHandleUserNameValidationError() {
        //Given
        ForgottenPasswordRequest request = new ForgottenPasswordRequest();
        request.setUrl("http://www.premierinn.com");


        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                post("/auth/hotels/forgot-password").
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("code", is(equalTo("001"))).
                body("details", hasSize(1)).
                body("details", hasItem("username must not be blank"));
    }

    @Test
    void forgotPassword_shouldHandleInvalidUrlValidationError() {
        //Given
        ForgottenPasswordRequest request = new ForgottenPasswordRequest();
        request.setUsername("userId");
        request.setUrl("invalidurl");


        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                post("/auth/hotels/forgot-password").
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("code", is(equalTo("001"))).
                body("details", hasSize(2)).
                body("details", containsInAnyOrder("url must be a valid URL", "url must be on white list"));
    }

    @Test
    void forgotPassword_shouldHandleValidUrlButNotWhiteListedValidationError() {
        //Given
        ForgottenPasswordRequest request = createValidForgottenPasswordRequest();
        request.setUrl("http://not.whitelisted.com");


        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(request).
                log().everything().
                when().
                post("/auth/hotels/forgot-password").
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("code", is(equalTo("001"))).
                body("details", hasSize(1)).
                body("details", hasItem("url must be on white list"));
    }

    @Test
    void forgotPassword_shouldInspectAuth0UserWithLowercaseEmailParam() {
        //Given
        String email = "First.Last@Domain.com";
        ForgottenPasswordRequest request = createValidForgottenPasswordRequest();
        request.setUrl("http://www.premierinn.com");
        request.setUsername(email);

        doCallRealMethod().when(mockHotelAuthServiceCdh).forgottenPasswordToken(request, LANGUAGE);

        //When
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            body(request).
            log().everything().
            when().
            post("/auth/hotels/forgot-password").
            then().
            log().everything();

        verify(managementService).getUser(email.toLowerCase());
    }

    @Test
    void forgotPassword_shouldFallbackToCdhService_PI() {
        //Given
        final ForgottenPasswordRequest request = createValidForgottenPasswordRequest();

        final String endpoint = "/auth/hotels/forgot-password";


        //When
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header("language", "en").
            header("country", "gb").
            queryParam("business", false).
            body(request).
            log().everything().
            when().
            post(endpoint).
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK).
            body("success", is(equalTo(true)));

        verify(mockHotelAuthServiceCdh).forgottenPasswordToken(any(ForgottenPasswordRequest.class),
            anyString());
    }

    @Test
    void forgotPassword_shouldFallbackToCdhService_BB() {
        //Given
        final ForgottenPasswordRequest request = createValidForgottenPasswordRequest();
        final String endpoint = "/auth/hotels/forgot-password";

        //When
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header("language", "en").
            header("country", "gb").
            queryParam("business", true).
            body(request).
            log().everything().
            when().
            post(endpoint).
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK).
            body("success", is(equalTo(true)));

        verify(mockHotelAuthServiceCdh).forgottenPasswordTokenBusiness(
            any(ForgottenPasswordRequest.class), anyString());
    }

    @Test
    void validateResetKey_shouldReturnSuccessForValidRequest() {
        // Given
        ValidateResetKeyRequest request = new ValidateResetKeyRequest("validResetKey");
        doReturn(new ValidateResetKeyResponse(true, "test@email.com")).when(mockHotelAuthServiceCdh)
            .validateResetKey(request);

        // When / Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .log().everything()
            .when()
            .post("/auth/hotels/validate-reset-key")
            .then()
            .log().everything()
            .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void validateResetKey_shouldReturnBadRequestForInvalidRequest() {
        // Given
        ValidateResetKeyRequest request = new ValidateResetKeyRequest("");

        // When / Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .log().everything()
            .when()
            .post("/auth/hotels/validate-reset-key")
            .then()
            .log().everything()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("code", equalTo("001"))
            .body("details", hasSize(1))
            .body("details[0]", equalTo("resetKey must not be blank"));
    }

    @Test
    void validateResetKey_shouldHandleServerError() {
        // Given
        ValidateResetKeyRequest request = new ValidateResetKeyRequest("triggerServerError");

        // When / Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
            .log().everything()
            .when()
            .post("/auth/hotels/validate-reset-key")
            .then()
            .log().everything()
            .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
            .body("code", equalTo(AUTH0_GENERIC_ERROR_CODE.getCode()));
    }

    @Test
    void resetPassword_shouldSucceed() {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setCustomerId("customer123");
        request.setNewPassword("dasdas!");

        ChangePasswordResponse mockResponse = new ChangePasswordResponse(true);
        doReturn(mockResponse).when(mockHotelAuthServiceCdh)
            .resetPassword("customer123", "dasdas!", "valid-token");

        // When / Then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("password-token", "valid-token")
            .header("country", "gb")
            .header("language", "en")
            .body(request)
            .log().everything()
        .when()
            .put("/auth/hotels/forgot-password?business=false")
        .then()
            .log().everything()
            .statusCode(HttpStatus.SC_OK)
            .body("passwordChanged", is(true));
    }

    @Test
    void resetPassword_shouldFailValidation() {
        // Given: missing newPassword and customerId
        ResetPasswordRequest request = new ResetPasswordRequest();

        // When / Then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("password-token", "valid-token")
            .body(request)
            .log().everything()
        .when()
            .put("/auth/hotels/forgot-password")
        .then()
            .log().everything()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("code", is("001"))
            .body("details", hasItem("customerId must not be empty"));
    }

    @Test
    void resetPassword_shouldFailOnMissingPasswordToken() {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setCustomerId("customer123");
        request.setNewPassword("dsadas");

        // When / Then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .log().everything()
        .when()
            .put("/auth/hotels/forgot-password")
        .then()
            .log().everything()
            .statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("code", is("013"))
            .body("details", hasItem("Required request header 'password-token' for method parameter type String is not present"));
    }

    @Test
    void resetPassword_shouldSucceedForBusiness() {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setCustomerId("customer123");
        request.setNewPassword("dasdasd!");

        ChangePasswordResponse mockResponse = new ChangePasswordResponse(true);
        doReturn(mockResponse).when(mockHotelAuthServiceCdh)
            .resetPasswordBusiness("customer123", "dasdasd!", "valid-token");

        // When / Then
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header("password-token", "valid-token")
            .header("country", "gb")
            .header("language", "en")
            .body(request)
            .log().everything()
            .when()
            .put("/auth/hotels/forgot-password?business=true")
            .then()
            .log().everything()
            .statusCode(HttpStatus.SC_OK)
            .body("passwordChanged", is(true));
    }


    private ForgottenPasswordRequest createValidForgottenPasswordRequest(){
        ForgottenPasswordRequest request = new ForgottenPasswordRequest();
        request.setUsername("userId");
        request.setUrl("http://www.premierinn.com");
        return request;
    }
}
