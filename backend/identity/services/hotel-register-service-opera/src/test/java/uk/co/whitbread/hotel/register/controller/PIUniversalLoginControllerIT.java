package uk.co.whitbread.hotel.register.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.register.model.RegisterAccountResponse;
import uk.co.whitbread.hotel.register.service.PIUniversalLoginService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class PIUniversalLoginControllerIT {

  @LocalServerPort
  int serverPort;

  @MockitoBean
  private PIUniversalLoginService mockUniversalLoginService;

  @BeforeEach
  void setUp() {
    RestAssured.port = serverPort;
  }

  @Test
  void shouldRegisterAccount_success() {
    // Given
    String email = "test@example.com";
    String payload = "{ \"email\": \"" + email + "\", \"firstName\": \"John\", \"lastName\": \"Doe\" }";
    RegisterAccountResponse mockResponse = new RegisterAccountResponse();
    when(mockUniversalLoginService.registerAccount(org.mockito.ArgumentMatchers.any())).thenReturn(mockResponse);

    // When
    Response httpResponse = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(payload)
        .log().everything()
        .post("/v1/hotel-register/universal-login/leisure/accounts");

    // Then
    httpResponse.then()
        .log().everything()
        .statusCode(HttpStatus.SC_CREATED);
  }

  @Test
  void shouldReturn400WhenInvalidPayload() {
    // Given
    String payload = "{ \"email\": \"\", \"firstName\": \"\", \"lastName\": \"\" }";

    // When
    Response httpResponse = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(payload)
        .log().everything()
        .post("/v1/hotel-register/universal-login/leisure/accounts");

    // Then
    httpResponse.then()
        .log().everything()
        .statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("title", is("Universal Login: Validation Failed"))
        .body("type", is("https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/4077617174/Digital+error+codes"))
        .body("details", hasItems("Email must not be blank", "Firstname must not be blank", "Lastname must not be blank"));
  }

  @Test
  void shouldReturn500WhenServiceThrowsException() {
    // Given
    String payload = "{ \"email\": \"test@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\" }";
    when(mockUniversalLoginService.registerAccount(org.mockito.ArgumentMatchers.any()))
        .thenThrow(new RuntimeException("Internal Exception"));

    // When
    Response httpResponse = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(payload)
        .log().everything()
        .post("/v1/hotel-register/universal-login/leisure/accounts");

    // Then
    httpResponse.then()
        .log().everything()
        .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
        .body("title", is("Universal Login: Internal Server Error"))
        .body("type", is("https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/4077617174/Digital+error+codes"))
        .body("detail", is("Internal Exception"));
  }

}
