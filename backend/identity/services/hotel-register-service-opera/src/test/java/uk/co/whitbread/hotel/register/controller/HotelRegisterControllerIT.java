package uk.co.whitbread.hotel.register.controller;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.captcha.service.CaptchaService;
import uk.co.whitbread.hotel.register.model.ContactDetail;
import uk.co.whitbread.hotel.register.model.Customer;
import uk.co.whitbread.hotel.register.service.HotelRegisterService;

import java.util.List;

import static io.github.benas.randombeans.api.EnhancedRandom.random;

import static io.restassured.RestAssured.given;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class HotelRegisterControllerIT {

  private static final String VALID_EMAIL = "user@test.com";
  private static final String VALID_TELEPHONE = "07701234567";
  public static final String PASSWORD = "abcXYZefg";

  public static final String DEFAULT_LANGUAGE = "en";

  @LocalServerPort
  int serverPort;

  @MockitoBean
  private HotelRegisterService mockHotelRegisterService;

  @MockitoBean
  private CaptchaService mockCaptchaService;

  @BeforeEach
  public void setUp() {
    RestAssured.port = serverPort;
  }

  @Test
  void shouldReturnA500InternalServerErrorWhenAnUnexpectedErrorHappen() {
    //Given
    Customer payload = random(Customer.class);
    payload.setPassword(PASSWORD);

    addValidContactDetails(payload);

    when(mockHotelRegisterService.createCustomer(payload, "en", false)).
        thenThrow(new RuntimeException("Internal Exception"));

    when(mockCaptchaService.isValid(payload.getCaptcha())).thenReturn(true);

    RequestSpecification request = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .header("language", DEFAULT_LANGUAGE)
        .body(payload)
        .queryParam("business", false)
        .log()
        .everything();

    // When
    Response response = request.when()
        .post("/customers/hotels");

    // Then
    ValidatableResponse validatableResponse = response.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
        .body("code", is(equalTo("999")))
        .body("details", hasSize(1))
        .body("details", hasItem("Internal Exception"));
  }

  @Test
  void shouldReturnA400BadRequestWhenAnInvalidInputPayloadIsPassed() {
    //Given
    Customer payload = random(Customer.class);
    payload.setPassword(null);
    payload.getBookingPreference().getRoomRequirements().setAdults(2);
    payload.getBookingPreference().getRoomRequirements().setChildren(1);

    RequestSpecification request = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(payload)
        .queryParam("business", false)
        .log()
        .everything();

    // When
    Response response = request.when()
        .post("/customers/hotels");

    // Then
    ValidatableResponse validatableResponse = response.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("details", is(not(empty())));
  }

  @Test
  void shouldReturnA401WhenTheCaptchaIsInvalid() {
    //Given
    Customer payload = random(Customer.class);

    addValidContactDetails(payload);

    when(mockCaptchaService.isValid(payload.getCaptcha())).thenReturn(false);

    RequestSpecification request = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(payload)
        .queryParam("business", false)
        .log()
        .everything();

    // When
    Response response = request.when()
        .post("/customers/hotels");

    // Then
    ValidatableResponse validatableResponse = response.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_UNAUTHORIZED)
        .body("code", is(equalTo("042")))
        .body("details", hasItem("Captcha verification failed"));
  }

  private void addValidContactDetails(Customer request) {

    request.getContactDetail().setEmail(VALID_EMAIL);
    request.getContactDetail().setTelephone(VALID_TELEPHONE);
    request.getContactDetail().setMobile(VALID_TELEPHONE);

    request.getBookingPreference().getRoomRequirements().setAdults(2);
    request.getBookingPreference().getRoomRequirements().setChildren(1);

    List<ContactDetail> additionalGuests = request.getAdditionalGuests()
        .stream()
        .map(t -> {
          t.setTelephone(VALID_TELEPHONE);
          t.setEmail(VALID_EMAIL);
          t.setMobile(VALID_TELEPHONE);
          return t;
        }).collect(toList());

    request.setAdditionalGuests(additionalGuests);
  }

}
