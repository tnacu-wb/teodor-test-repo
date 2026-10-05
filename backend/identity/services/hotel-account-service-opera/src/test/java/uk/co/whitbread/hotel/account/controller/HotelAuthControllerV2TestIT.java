package uk.co.whitbread.hotel.account.controller;

import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordRequest;
import uk.co.whitbread.hotel.account.model.ForgottenPasswordResponse;
import uk.co.whitbread.hotel.account.service.HotelAuthServiceV2;

import static io.restassured.RestAssured.given;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class HotelAuthControllerV2TestIT {

  @LocalServerPort
  int serverPort;

  @MockitoSpyBean
  private HotelAuthServiceV2 mockHotelAuthServiceV2;

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
        post("/v2/auth/hotels/forgot-password").
        then().
        log().everything().
        statusCode(HttpStatus.SC_BAD_REQUEST).
        body("code", is(equalTo("001"))).
        body("details", hasSize(1)).
        body("details", hasItem("username must not be blank"));

    verifyNoInteractions(mockHotelAuthServiceV2);
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
        post("/v2/auth/hotels/forgot-password").
        then().
        log().everything().
        statusCode(HttpStatus.SC_BAD_REQUEST).
        body("code", is(equalTo("001"))).
        body("details", hasSize(2)).
        body("details", containsInAnyOrder("url must be a valid URL", "url must be on white list"));

    verifyNoInteractions(mockHotelAuthServiceV2);
  }

  @Test
  void forgotPassword_shouldHandleNoUrl() {
    //Given
    ForgottenPasswordRequest request = new ForgottenPasswordRequest();
    request.setUsername("userId");
    ForgottenPasswordResponse response = new ForgottenPasswordResponse(TRUE);

    doReturn(response).when(mockHotelAuthServiceV2)
        .forgotPassword(any(ForgottenPasswordRequest.class), eq(false));

    //When
    given().
        contentType(MediaType.APPLICATION_JSON_VALUE).
        body(request).
        log().everything().
        when().
        post("/v2/auth/hotels/forgot-password").
        then().
        log().everything().
        statusCode(HttpStatus.SC_OK).
        body("success", is(true));
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
        post("/v2/auth/hotels/forgot-password").
        then().
        log().everything().
        statusCode(HttpStatus.SC_BAD_REQUEST).
        body("code", is(equalTo("001"))).
        body("details", hasSize(1)).
        body("details", hasItem("url must be on white list"));
  }

  @Test
  void forgotPasswordBusiness_shouldGetOKResponse() {
    //Given
    ForgottenPasswordRequest request = createValidForgottenPasswordRequest();
    ForgottenPasswordResponse response = new ForgottenPasswordResponse(TRUE);
    boolean business = true;

    doReturn(response).when(mockHotelAuthServiceV2)
        .forgotPassword(any(ForgottenPasswordRequest.class), eq(business));

    //When
    given().
        contentType(MediaType.APPLICATION_JSON_VALUE).
        queryParam("business", business).
        body(request).
        log().everything().
        when().
        post("/v2/auth/hotels/forgot-password").
        then().
        log().everything().
        statusCode(HttpStatus.SC_OK).
        body("success", is(equalTo(true)));
  }

  private ForgottenPasswordRequest createValidForgottenPasswordRequest() {
    ForgottenPasswordRequest request = new ForgottenPasswordRequest();
    request.setUsername("userId");
    request.setUrl("http://www.premierinn.com");
    return request;
  }
}