package uk.co.whitbread.hotel.register.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.captcha.service.CaptchaService;
import uk.co.whitbread.hotel.register.model.AppsContactDetail;
import uk.co.whitbread.hotel.register.model.AppsCustomer;
import uk.co.whitbread.hotel.register.service.HotelRegisterService;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class AppsHotelRegisterControllerIT {

  private static final String VALID_EMAIL = "user@test.com";
  private static final String PASSWORD = "abcXYZefg";
  private static final String DEFAULT_LANGUAGE = "en";
  private static final String FIRST_NAME = "firstName";
  private static final String LAST_NAME = "lastName";
  private static final String CAPTCHA = "string";
  private static final String PATH = "/v1/hotel-register/accounts/register";

  @LocalServerPort
  int serverPort;

  @MockitoBean
  private HotelRegisterService mockHotelRegisterService;

  @MockitoBean
  private CaptchaService mockCaptchaService;

  @BeforeEach
  void setUp() {
    RestAssured.port = serverPort;
  }

  @Test
  void shouldReturnA500InternalServerErrorWhenAnUnexpectedErrorHappen() {
    //Given
    AppsCustomer payload = buildRequest(VALID_EMAIL, PASSWORD);

    when(mockHotelRegisterService.registerAccount(payload, "en")).
        thenThrow(new RuntimeException("Internal Exception"));

    when(mockCaptchaService.isValid(payload.getCaptcha())).thenReturn(true);

    RequestSpecification request = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .header("language", DEFAULT_LANGUAGE)
        .body(payload)
        .log()
        .everything();

    // When
    Response response = request.when()
        .post(PATH);

    // Then
    ValidatableResponse validatableResponse = response.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
        .body("code", is(equalTo("999")))
        .body("details", hasSize(1))
        .body("details", hasItem("Internal Exception"));
  }

  @ParameterizedTest
  @CsvSource({"null, null, email", "email1, password1, email", "null, password2, email",
      "email3, null, password"})
  void shouldReturnA400BadRequestWhenAnInvalidInputPayloadIsPassed(String email, String password,
      String field) {
    //Given
    AppsCustomer payload = buildRequest(email, password);

    RequestSpecification request = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(payload)
        .log()
        .everything();
    when(mockCaptchaService.isValid(payload.getCaptcha())).thenReturn(true);

    // When
    Response response = request.when()
        .post(PATH);

    // Then
    ValidatableResponse validatableResponse = response.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_BAD_REQUEST)
        .body(field, is(not(empty())));
  }

  @Test
  void shouldReturnA401WhenTheCaptchaIsInvalid() {
    //Given
    AppsCustomer payload = buildRequest(VALID_EMAIL, PASSWORD);

    when(mockCaptchaService.isValid(payload.getCaptcha())).thenReturn(false);

    RequestSpecification request = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(payload)
        .log()
        .everything();

    // When
    Response response = request.when()
        .post(PATH);

    // Then
    ValidatableResponse validatableResponse = response.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_UNAUTHORIZED)
        .body("code", is(equalTo("042")))
        .body("details", hasItem("Captcha verification failed"));
  }

  private AppsCustomer buildRequest(String email, String password) {
    AppsCustomer request = new AppsCustomer();
    AppsContactDetail detail = new AppsContactDetail();
    detail.setEmail(email);
    detail.setFirstName(FIRST_NAME);
    detail.setLastName(LAST_NAME);
    request.setContactDetail(detail);
    request.setPassword(password);
    request.setCaptcha(CAPTCHA);
    return request;
  }

}
