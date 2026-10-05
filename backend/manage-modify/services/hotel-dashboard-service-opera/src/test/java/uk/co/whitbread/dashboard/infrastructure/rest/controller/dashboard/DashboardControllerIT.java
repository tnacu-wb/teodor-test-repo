package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.wiremock.spring.ConfigureWireMock;
import uk.co.whitbread.dashboard.DashboardApplication;

@DirtiesContext
@SpringBootTest(classes = { DashboardApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ConfigureWireMock(name = "wiremockServer")
@Disabled
class DashboardControllerIT {

    @LocalServerPort
    int serverPort;

    static final String ORIGIN = "origin.header.for.test";
    
    @BeforeEach
    void setUp() {
        RestAssured.port = serverPort;
    }

    @Test
    void shouldRetrieveFrequentBookings() {
        //Given
        final String confirmationNumber = "CONF123";
        final String arrivalDate  = "2016-11-20";
        final boolean recentSearches = false;
        final String surname = "smith";
        final String auth = "Bearer VALID_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("origin", ORIGIN)
                .queryParam("hasRecentSearches", recentSearches)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("FREQUENT_BOOKINGS")))
                .body("[0].content.frequentBookings[0].hotelCode", is(equalTo("LONHOL")))
                .body("[0].content.frequentBookings[1].hotelCode", is(equalTo("LONBLA")))
                .body("[0].content.frequentBookings[2].hotelCode", is(equalTo("KINPTI")));
    }

    @Test
    void shouldRetrieveBBFrequentBookings() {
        //Given
        final String confirmationNumber = "CONF123";
        final String arrivalDate  = "2016-11-20";
        final boolean recentSearches = false;
        final String surname = "smith";
        final String auth = "Bearer VALID_BB_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .header("bookingChannel", "CBT")
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("origin", ORIGIN)
                .queryParam("hasRecentSearches", recentSearches)
                .queryParam("business", true)
                .queryParam("companyId","13")
                .queryParam("employeeId","1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("FREQUENT_BOOKINGS")))
                .body("[0].content.frequentBookings[0].hotelCode", is(equalTo("LONHOL")))
                .body("[0].content.frequentBookings[1].hotelCode", is(equalTo("LONBLA")))
                .body("[0].content.frequentBookings[2].hotelCode", is(equalTo("KINPTI")));
    }

    @Test
    void shouldRetrieveBBFrequentBookingsUsingSessionId() {
        //Given
        final String confirmationNumber = "CONF123";
        final String arrivalDate  = "2016-11-20";
        final boolean recentSearches = false;
        final String surname = "smith";
        final String sessionId = "VALID_SESSION_ID";

        final RequestSpecification request = given()
                .header("bookingChannel", "CBT")
                .header("session-id", sessionId)
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("origin", ORIGIN)
                .queryParam("hasRecentSearches", recentSearches)
                .queryParam("business", true)
                .queryParam("companyId","13")
                .queryParam("employeeId","1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("FREQUENT_BOOKINGS")))
                .body("[0].content.frequentBookings[0].hotelCode", is(equalTo("LONHOL")))
                .body("[0].content.frequentBookings[1].hotelCode", is(equalTo("LONBLA")))
                .body("[0].content.frequentBookings[2].hotelCode", is(equalTo("KINPTI")));
    }

    @Test
    void shouldReturnPastSearchesFlag() {
        //Given
        final String confirmationNumber = "CONF123";
        final String arrivalDate  = "2016-10-20";
        final boolean recentSearches = false;
        final String surname = "Johnny";
        final String auth = "Bearer ANOTHER_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("hasRecentSearches", recentSearches)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("PAST_SEARCHES")));
    }

    @Test
    void shouldReturnRecentSearchesFlag() {
        //Given
        final String confirmationNumber = "CONF123";
        final String arrivalDate  = "2016-10-20";
        final boolean recentSearches = true;
        final String surname = "Johnny";
        final String auth = "Bearer ANOTHER_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("hasRecentSearches", recentSearches)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("RECENT_SEARCHES")));
    }

    @Test
    void shouldRetrieveUpcomingBooking() {
        //Given
        final String confirmationNumber = "CQBR885046";
        final String arrivalDate  = LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        final boolean recentSearches = false;
        final String surname = "Martin";
        final String auth = "Bearer VALID_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("origin", ORIGIN)
                .queryParam("hasRecentSearches", recentSearches)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("UPCOMING_BOOKING")))
                .body("[0].content.hotelCode", is(equalTo("KINPTI")))
                .body("[0].content.arrivalDate", is(equalTo(arrivalDate)))
                .body("[0].content.actions[0].type", is(equalTo("UPSELLS")))
                .body("[0].content.actions[1].type", is(equalTo("DIRECTIONS")))
                .body("[0].content.actions[2].type", is(equalTo("BOOKING_DETAILS")));
    }

    @Test
    void shouldRetrieveUpcomingBookingActionsInGermanWhenLanguageIsGerman() {
        //Given
        final String confirmationNumber = "CQBR885046";
        final String arrivalDate  = LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        final boolean recentSearches = false;
        final String surname = "Martin";
        final String auth = "Bearer VALID_TOKEN";
        final String language = "de";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("origin", ORIGIN)
                .queryParam("hasRecentSearches", recentSearches)
                .queryParam("language", language)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("UPCOMING_BOOKING")))
                .body("[0].content.actions[0].title", is(equalTo("Mahlzeiten oder Extras hinzufügen")))
                .body("[0].content.actions[1].title", is(equalTo("Hotelanfahrt anzeigen")))
                .body("[0].content.actions[2].title", is(equalTo("Buchungsdetails anzeigen")));
    }

    @Test
    void shouldRetrieveUpcomingBookingActionsInEnglishWhenLanguageIsEnglish() {
        //Given
        final String confirmationNumber = "CQBR885046";
        final String arrivalDate  = LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        final boolean recentSearches = false;
        final String surname = "Martin";
        final String auth = "Bearer VALID_TOKEN";
        final String language = "en";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("origin", ORIGIN)
                .queryParam("hasRecentSearches", recentSearches)
//                .queryParam("language", language)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("UPCOMING_BOOKING")))
                .body("[0].content.actions[0].title", is(equalTo("Add meals or extras")))
                .body("[0].content.actions[1].title", is(equalTo("Show hotel directions")))
                .body("[0].content.actions[2].title", is(equalTo("View booking details")));
    }

    @Test
    void shouldReceivePastSearchesWhenHotelReservationIsInvalid() {
        //Given
        final String confirmationNumber = "INCOR885046";
        final String arrivalDate  = LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        final boolean recentSearches = false;
        final String surname = "Wrongy";
        final String auth = "Bearer ANOTHER_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("hasRecentSearches", recentSearches)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("PAST_SEARCHES")));
    }

    @Test
    void shouldReceiveValidationErrorWhenSurnameIsEmpty() {
        //Given
        final String confirmationNumber = "INCOR885046";
        final String arrivalDate  = LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        final boolean recentSearches = false;
        final String surname = "Wrongy";
        final String auth = "Bearer VALID_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", "")
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("hasRecentSearches", recentSearches)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", is(equalTo("001")))
                .body("details[0]", is(equalTo("retrieveDashboardRequest all or none fields must be populated: [confirmationNumber, surname, arrivalDate]")));
    }

    @Test
    void shouldReceiveValidationErrorWhenSurnameAndArrivalDateAreEmpty() {
        //Given
        final String confirmationNumber = "INCOR885046";
        final String arrivalDate  = LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        final boolean recentSearches = false;
        final String surname = "Wrongy";
        final String auth = "Bearer VALID_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", "")
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("hasRecentSearches", recentSearches)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.BAD_REQUEST.value())
                .body("code", is(equalTo("001")))
                .body("details[0]", is(equalTo("retrieveDashboardRequest all or none fields must be populated: [confirmationNumber, surname, arrivalDate]")));
    }


    @Test
    void shouldReceiveRecentSearchesWhenAllReservationFieldsAreEmpty() {
        //Given
        final String auth = "Bearer VALID_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("RECENT_SEARCHES")));
    }

    @Test
    void shouldReceiveErrorFromHotelInfo() {
        //Given
        final String confirmationNumber = "BLOBLO885046";
        final String arrivalDate  = LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        final boolean recentSearches = false;
        final String surname = "Martin";
        final String auth = "Bearer VALID_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("origin", ORIGIN)
                .queryParam("hasRecentSearches", recentSearches)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.NOT_FOUND.value())
                .body("code", is(equalTo("100")))
                .body("details[0]", is(equalTo("Unable to get info for bloblo. Reason: 404 Not Found")));
    }

    @Test
    void shouldRetrieveUpcomingBookingFromDashBoardV1() {
        //Given
        final String confirmationNumber = "CQBR885046";
        final String arrivalDate  = LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        final boolean recentSearches = false;
        final String surname = "Martin";
        final String auth = "Bearer VALID_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .queryParam("origin", ORIGIN)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("UPCOMING_BOOKING")))
                .body("[0].content.hotelCode", is(equalTo("KINPTI")))
                .body("[0].content.arrivalDate", is(equalTo(arrivalDate)))
                .body("[0].content.actions[0].type", is(equalTo("UPSELLS")))
                .body("[0].content.actions[1].type", is(equalTo("DIRECTIONS")))
                .body("[0].content.actions[2].type", is(equalTo("BOOKING_DETAILS")));
    }

    @Test
    void shouldRetrieveRecentSearchesFromDashBoardV1() {
        //Given
        final String confirmationNumber = "CQBR885046";
        final String arrivalDate  = LocalDate.now().plusDays(100).format(DateTimeFormatter.ISO_LOCAL_DATE);
        final boolean recentSearches = false;
        final String surname = "Martin";
        final String auth = "Bearer VALID_TOKEN";

        final RequestSpecification request = given()
                .header("Authorization", auth)
                .queryParam("surname", surname)
                .queryParam("arrivalDate", arrivalDate)
                .queryParam("confirmationNumber", confirmationNumber)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log()
                .everything();

        // When
        final Response response = request.when()
                .get("/dashboard");

        // Then
        final ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.OK.value())
                .body("[0].type", is(equalTo("RECENT_SEARCHES")));
    }
}