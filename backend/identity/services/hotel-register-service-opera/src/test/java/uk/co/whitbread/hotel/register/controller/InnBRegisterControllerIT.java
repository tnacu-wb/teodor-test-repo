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
import java.util.stream.Stream;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.register.model.InnBCompanyAddress;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepOneResponse;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoRequest;
import uk.co.whitbread.hotel.register.model.InnBRegistrationStepTwoResponse;
import uk.co.whitbread.hotel.register.service.HotelRegisterService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class InnBRegisterControllerIT {

  @LocalServerPort
  int serverPort;

  @MockitoBean
  private HotelRegisterService mockHotelRegisterService;

  @BeforeEach
  void setUp() {
    RestAssured.port = serverPort;
  }

  @Test
  void shouldRegisterInnBStepOne_success() {
    // Given
    InnBRegistrationStepOneRequest request = createValidInnBRegistrationStepOneRequest();
    InnBRegistrationStepOneResponse response = InnBRegistrationStepOneResponse.builder()
        .existingCompany(false)
        .existingEmployee(false)
        .existingCompanyType(null)
        .build();

    when(mockHotelRegisterService.registerInnBStepOne(request)).thenReturn(response);

    RequestSpecification httpRequest = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(request)
        .log()
        .everything();

    // When
    Response httpResponse = httpRequest.when()
        .post("/v1/hotel-register/innbusiness/registration/step-one");

    // Then
    ValidatableResponse validatableResponse = httpResponse.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_CREATED)
        .body("existingEmployee", is(false))
        .body("existingCompany", is(false));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidInnBRegistrationStepOneRequests")
  void shouldReturnA400BadRequestWhenInvalidInputPayloadIsPassed() {
    // Given
    InnBRegistrationStepOneRequest request = createValidInnBRegistrationStepOneRequest();
    request.setEmail(null); // Invalid payload

    RequestSpecification httpRequest = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(request)
        .log()
        .everything();

    // When
    Response httpResponse = httpRequest.when()
        .post("/v1/hotel-register/innbusiness/registration/step-one");

    // Then
    ValidatableResponse validatableResponse = httpResponse.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("details", is(not(empty())));
  }

  @Test
  void shouldReturnA500InternalServerErrorWhenAnUnexpectedErrorHappens() {
    // Given
    InnBRegistrationStepOneRequest request = createValidInnBRegistrationStepOneRequest();

    when(mockHotelRegisterService.registerInnBStepOne(request)).thenThrow(new RuntimeException("Internal Exception"));

    RequestSpecification httpRequest = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(request)
        .log()
        .everything();

    // When
    Response httpResponse = httpRequest.when()
        .post("/v1/hotel-register/innbusiness/registration/step-one");

    // Then
    ValidatableResponse validatableResponse = httpResponse.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
        .body("code", is(equalTo("999")))
        .body("details", hasSize(1))
        .body("details", hasItem("Internal Exception"));
  }

  @Test
  void shouldRegisterInnBStepTwo_success() {
    // Given
    InnBRegistrationStepTwoRequest request = createValidInnBRegistrationStepTwoRequest();
    InnBRegistrationStepTwoResponse response = new InnBRegistrationStepTwoResponse("valid.email@example.com", "companyid");

    when(mockHotelRegisterService.registerInnBStepTwo(request)).thenReturn(response);

    RequestSpecification httpRequest = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(request)
        .log()
        .everything();

    // When
    Response httpResponse = httpRequest.when()
        .post("/v1/hotel-register/innbusiness/registration/step-two");

    // Then
    ValidatableResponse validatableResponse = httpResponse.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_OK)
        .body("email", is("valid.email@example.com"));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidInnBRegistrationStepTwoRequests")
  void shouldReturnA400BadRequestWhenInvalidInputPayloadIsPassedForStepTwo(InnBRegistrationStepTwoRequest request) {
    // Given
    RequestSpecification httpRequest = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(request)
        .log()
        .everything();

    // When
    Response httpResponse = httpRequest.when()
        .post("/v1/hotel-register/innbusiness/registration/step-two");

    // Then
    ValidatableResponse validatableResponse = httpResponse.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_BAD_REQUEST)
        .body("details", is(not(empty())));
  }


  @Test
  void shouldReturnA500InternalServerErrorWhenAnUnexpectedErrorHappensForStepTwo() {
    // Given
    InnBRegistrationStepTwoRequest request = createValidInnBRegistrationStepTwoRequest();

    when(mockHotelRegisterService.registerInnBStepTwo(request)).thenThrow(new RuntimeException("Internal Exception"));

    RequestSpecification httpRequest = given()
        .accept(MediaType.APPLICATION_JSON_VALUE)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(request)
        .log()
        .everything();

    // When
    Response httpResponse = httpRequest.when()
        .post("/v1/hotel-register/innbusiness/registration/step-two");

    // Then
    ValidatableResponse validatableResponse = httpResponse.then()
        .log()
        .everything();

    validatableResponse.statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
        .body("code", is(equalTo("999")))
        .body("details", hasSize(1))
        .body("details", hasItem("Internal Exception"));
  }

  private static InnBRegistrationStepOneRequest createValidInnBRegistrationStepOneRequest() {
    InnBRegistrationStepOneRequest request = new InnBRegistrationStepOneRequest();
    request.setEmail("valid.email@example.com");
    request.setCompanyName("Valid Company Name");
    request.setAddress(InnBCompanyAddress.builder()
        .line1("123 Street")
        .postCode("12345")
        .countryCode("UK")
        .build());
    request.setLanguage("en");
    return request;
  }

  private static Stream<Arguments> provideInvalidInnBRegistrationStepOneRequests() {
    return Stream.of(
        Arguments.of(createValidInnBRegistrationStepOneRequestWithInvalidEmail()), // Invalid email
        Arguments.of(createValidInnBRegistrationStepOneRequestWithInvalidCompanyName()), // Invalid company name
        Arguments.of(createValidInnBRegistrationStepOneRequestWithInvalidAddress()), // Invalid address
        Arguments.of(createValidInnBRegistrationStepOneRequestWithInvalidLanguage()) // Invalid language
    );
  }

  private static InnBRegistrationStepOneRequest createValidInnBRegistrationStepOneRequestWithInvalidEmail() {
    InnBRegistrationStepOneRequest request = createValidInnBRegistrationStepOneRequest();
    request.setEmail(null);
    return request;
  }

  private static InnBRegistrationStepOneRequest createValidInnBRegistrationStepOneRequestWithInvalidCompanyName() {
    InnBRegistrationStepOneRequest request = createValidInnBRegistrationStepOneRequest();
    request.setCompanyName(null);
    return request;
  }

  private static InnBRegistrationStepOneRequest createValidInnBRegistrationStepOneRequestWithInvalidAddress() {
    InnBRegistrationStepOneRequest request = createValidInnBRegistrationStepOneRequest();
    request.setAddress(null);
    return request;
  }

  private static InnBRegistrationStepOneRequest createValidInnBRegistrationStepOneRequestWithInvalidLanguage() {
    InnBRegistrationStepOneRequest request = createValidInnBRegistrationStepOneRequest();
    request.setLanguage(null);
    return request;
  }

  private static InnBRegistrationStepTwoRequest createValidInnBRegistrationStepTwoRequest() {
    InnBRegistrationStepTwoRequest request = new InnBRegistrationStepTwoRequest();
    request.setFirstName("ValidFirstName");
    request.setLastName("ValidLastName");
    request.setTitle("Mr");
    request.setPassword("ValidPassword123!");
    request.setActivationKey("ValidActivationKey");
    return request;
  }

  private static Stream<Arguments> provideInvalidInnBRegistrationStepTwoRequests() {
    return Stream.of(
        Arguments.of(createRequestWithNullPassword()),
        Arguments.of(createRequestWithNullActivationKey()),
        Arguments.of(createRequestWithEmptyFirstName()),
        Arguments.of(createRequestWithEmptyLastName())
    );
  }

  private static InnBRegistrationStepTwoRequest createRequestWithNullPassword() {
    InnBRegistrationStepTwoRequest request = createValidInnBRegistrationStepTwoRequest();
    request.setPassword(null);
    return request;
  }

  private static InnBRegistrationStepTwoRequest createRequestWithNullActivationKey() {
    InnBRegistrationStepTwoRequest request = createValidInnBRegistrationStepTwoRequest();
    request.setActivationKey(null);
    return request;
  }

  private static InnBRegistrationStepTwoRequest createRequestWithEmptyFirstName() {
    InnBRegistrationStepTwoRequest request = createValidInnBRegistrationStepTwoRequest();
    request.setFirstName("");
    return request;
  }

  private static InnBRegistrationStepTwoRequest createRequestWithEmptyLastName() {
    InnBRegistrationStepTwoRequest request = createValidInnBRegistrationStepTwoRequest();
    request.setLastName("");
    return request;
  }

}
