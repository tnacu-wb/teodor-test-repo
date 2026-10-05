package uk.co.whitbread.hotel.account.controller.v1_5;

import uk.co.whitbread.hotel.account.controller.ContractVerifierBaseTest;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.response.ResponseOptions;

import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.*;
import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;

@SuppressWarnings("rawtypes")
public class GetStaysIT extends ContractVerifierBaseTest {

	@Test
	public void validate_guestAllStaysNoStays() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer mockAuthorization2");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com/stays?business=false");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['stays']").isEmpty();
	}

	@Test
	public void validate_guestGetAllStays() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer mockAuthorization");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com/stays?business=false");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['stays']").contains("['hotelCode']").isEqualTo("LONBLA");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2017-10-29");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2017-10-29");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(105.0);
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['stays']").contains("['noOfRooms']").isEqualTo(0);
			assertThatJson(parsedJson).array("['stays']").contains("['customerReference']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['historyRecordNumber']").isEqualTo(0);
			assertThatJson(parsedJson).array("['stays']").contains("['purchaseOrder']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['prePaidAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['paymentStatus']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cancelled']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['checkInOnline']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['checkedIn']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['rateClass']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['checkInDate']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['promotionText']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cellCodeLegend']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['carDataRequired']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['bookingStatus']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['amendable']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['guestHistoryNumber']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['bookedBy']").isEqualTo("");
			assertThatJson(parsedJson).array("['stays']").contains("['bookingFee']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['outstandingAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['frequentBooking']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['rateName']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cancellationId']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['hotelName']").isEqualTo("London");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuest']").isEqualTo("MR ASD ASD");
			assertThatJson(parsedJson).array("['stays']").contains("['mpibooking']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2037-12-22");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2037-12-23");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(83.0);
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['roomType']").isEqualTo("DB");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['bookingStatus']").isEqualTo("BOOKED");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['leadGuest']").isEqualTo("MR ASD ASD");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['carDataPresent']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['noOfRooms']").isEqualTo(1);
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("BBER283883");
			assertThatJson(parsedJson).array("['stays']").contains("['rateClass']").isEqualTo("A");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuestSurname']").isEqualTo("ASD");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2017-12-29");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2017-12-30");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(102.0);
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("BBER283885");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2018-01-03");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2018-01-04");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(77.0);
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['leadGuest']").isEqualTo("MISS IOA OPRESCU");
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("BBER283897");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuest']").isEqualTo("MISS IOA OPRESCU");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuestSurname']").isEqualTo("OPRESCU");
			assertThatJson(parsedJson).array("['stays']").contains("['hotelCode']").isEqualTo("WHILON");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2018-01-04");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2018-01-05");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(45.0);
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['bookingStatus']").isEqualTo("CANCELLED");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['leadGuest']").isEqualTo("MR TEST BOOKING");
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("AOAR172316");
			assertThatJson(parsedJson).array("['stays']").contains("['cancelled']").isEqualTo(true);
			assertThatJson(parsedJson).array("['stays']").contains("['hotelName']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuest']").isEqualTo("MR TEST BOOKING");
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("AOAR172317");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2018-01-23");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2018-01-24");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(182.0);
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("BBER283904");
	}

	@Test
	public void validate_guestGetAllStaysFailureOnFuture() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer mockAuthorization");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com/stays?business=false");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['stays']").contains("['hotelCode']").isEqualTo("LONBLA");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2017-10-29");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2017-10-29");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(105.0);
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['stays']").contains("['noOfRooms']").isEqualTo(0);
			assertThatJson(parsedJson).array("['stays']").contains("['customerReference']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['historyRecordNumber']").isEqualTo(0);
			assertThatJson(parsedJson).array("['stays']").contains("['purchaseOrder']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['prePaidAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['paymentStatus']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cancelled']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['checkInOnline']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['checkedIn']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['rateClass']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['checkInDate']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['promotionText']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cellCodeLegend']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['carDataRequired']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['bookingStatus']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['amendable']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['guestHistoryNumber']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['bookedBy']").isEqualTo("");
			assertThatJson(parsedJson).array("['stays']").contains("['bookingFee']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['outstandingAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['frequentBooking']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['rateName']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cancellationId']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['hotelName']").isEqualTo("London");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuest']").isEqualTo("");
			assertThatJson(parsedJson).array("['stays']").contains("['mpibooking']").isEqualTo(false);
	}

	@Test
	public void validate_guestGetAllStaysFailureOnPast() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer mockAuthorization");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com/stays?business=false");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['stays']").contains("['hotelCode']").isEqualTo("LONBLA");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2037-12-22");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2037-12-23");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(83.0);
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['roomType']").isEqualTo("DB");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['bookingStatus']").isEqualTo("BOOKED");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['leadGuest']").isEqualTo("MR ASD ASD");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['carDataPresent']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['noOfRooms']").isEqualTo(1);
			assertThatJson(parsedJson).array("['stays']").contains("['customerReference']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['historyRecordNumber']").isEqualTo(0);
			assertThatJson(parsedJson).array("['stays']").contains("['purchaseOrder']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("BBER283883");
			assertThatJson(parsedJson).array("['stays']").contains("['prePaidAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['paymentStatus']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cancelled']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['checkInOnline']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['checkedIn']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['rateClass']").isEqualTo("A");
			assertThatJson(parsedJson).array("['stays']").contains("['checkInDate']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['promotionText']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cellCodeLegend']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['carDataRequired']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['bookingStatus']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['amendable']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['guestHistoryNumber']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['bookedBy']").isEqualTo("");
			assertThatJson(parsedJson).array("['stays']").contains("['bookingFee']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['outstandingAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['frequentBooking']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['rateName']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cancellationId']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['hotelName']").isEqualTo("London");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuest']").isEqualTo("MR ASD ASD");
			assertThatJson(parsedJson).array("['stays']").contains("['mpibooking']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2017-12-29");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2017-12-30");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(102.0);
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("BBER283885");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2018-01-03");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2018-01-04");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(77.0);
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['leadGuest']").isEqualTo("MISS IOA OPRESCU");
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("BBER283897");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuest']").isEqualTo("MISS IOA OPRESCU");
			assertThatJson(parsedJson).array("['stays']").contains("['hotelCode']").isEqualTo("WHILON");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2018-01-04");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2018-01-05");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(45.0);
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['bookingStatus']").isEqualTo("CANCELLED");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['leadGuest']").isEqualTo("MR TEST BOOKING");
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("AOAR172316");
			assertThatJson(parsedJson).array("['stays']").contains("['cancelled']").isEqualTo(true);
			assertThatJson(parsedJson).array("['stays']").contains("['hotelName']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuest']").isEqualTo("MR TEST BOOKING");
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("AOAR172317");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2018-01-23");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2018-01-24");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(182.0);
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("BBER283904");
	}

	@Test
	public void validate_guestGetCancelledStaysSuccess() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer mockAuthorization");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com/stays?business=false&typeOfBooking=CANCELLED");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['stays']").contains("['hotelCode']").isEqualTo("WHILON");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2018-01-04");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2018-01-05");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(45.0);
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['roomType']").isEqualTo("DB");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['bookingStatus']").isEqualTo("CANCELLED");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['leadGuest']").isEqualTo("MR TEST BOOKING");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['carDataPresent']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['noOfRooms']").isEqualTo(1);
			assertThatJson(parsedJson).array("['stays']").contains("['customerReference']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['historyRecordNumber']").isEqualTo(0);
			assertThatJson(parsedJson).array("['stays']").contains("['purchaseOrder']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("AOAR172316");
			assertThatJson(parsedJson).array("['stays']").contains("['prePaidAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['paymentStatus']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cancelled']").isEqualTo(true);
			assertThatJson(parsedJson).array("['stays']").contains("['checkInOnline']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['checkedIn']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['rateClass']").isEqualTo("A");
			assertThatJson(parsedJson).array("['stays']").contains("['checkInDate']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['promotionText']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cellCodeLegend']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['carDataRequired']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['bookingStatus']").isEqualTo("CANCELLED");
			assertThatJson(parsedJson).array("['stays']").contains("['amendable']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['guestHistoryNumber']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['bookedBy']").isEqualTo("");
			assertThatJson(parsedJson).array("['stays']").contains("['bookingFee']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['outstandingAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['frequentBooking']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['rateName']").isEqualTo("Room Only");
			assertThatJson(parsedJson).array("['stays']").contains("['cancellationId']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['hotelName']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuest']").isEqualTo("MR TEST BOOKING");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuestSurname']").isEqualTo("BOOKING");
			assertThatJson(parsedJson).array("['stays']").contains("['mpibooking']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("AOAR172317");
			assertThatJson(parsedJson).array("['stays']").contains("['hotelCode']").isEqualTo("LONBLA");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2018-01-23");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2018-01-24");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(182.0);
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['leadGuest']").isEqualTo("MISS IOA OPRESCU");
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("BBER283904");
			assertThatJson(parsedJson).array("['stays']").contains("['hotelName']").isEqualTo("London");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuest']").isEqualTo("MISS IOA OPRESCU");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuestSurname']").isEqualTo("OPRESCU");
	}

	@Test
	public void validate_guestGetFutureStaysSuccess() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer mockAuthorization");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com/stays?business=false&typeOfBooking=FUTURE");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['stays']").contains("['hotelCode']").isEqualTo("LONBLA");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2037-12-22");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2037-12-23");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(83.0);
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['roomType']").isEqualTo("DB");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['bookingStatus']").isEqualTo("FUTURE");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['leadGuest']").isEqualTo("MR ASD ASD");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").contains("['carDataPresent']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['noOfRooms']").isEqualTo(1);
			assertThatJson(parsedJson).array("['stays']").contains("['customerReference']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['historyRecordNumber']").isEqualTo(0);
			assertThatJson(parsedJson).array("['stays']").contains("['purchaseOrder']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isEqualTo("BBER283883");
			assertThatJson(parsedJson).array("['stays']").contains("['prePaidAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['paymentStatus']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cancelled']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['checkInOnline']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['checkedIn']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['rateClass']").isEqualTo("A");
			assertThatJson(parsedJson).array("['stays']").contains("['checkInDate']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['promotionText']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cellCodeLegend']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['carDataRequired']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['bookingStatus']").isEqualTo("FUTURE");
			assertThatJson(parsedJson).array("['stays']").contains("['amendable']").isEqualTo(true);
			assertThatJson(parsedJson).array("['stays']").contains("['guestHistoryNumber']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['bookedBy']").isEqualTo("");
			assertThatJson(parsedJson).array("['stays']").contains("['bookingFee']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['outstandingAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['frequentBooking']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['rateName']").isEqualTo("Room Only");
			assertThatJson(parsedJson).array("['stays']").contains("['cancellationId']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['hotelName']").isEqualTo("London");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuest']").isEqualTo("MR ASD ASD");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuestSurname']").isEqualTo("ASD");
			assertThatJson(parsedJson).array("['stays']").contains("['mpibooking']").isEqualTo(false);
	}

	@Test
	public void validate_guestGetPastStaysSuccess() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer mockAuthorization");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com/stays?business=false&typeOfBooking=PAST");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['stays']").contains("['hotelCode']").isEqualTo("LONBLA");
			assertThatJson(parsedJson).array("['stays']").contains("['arrivalDate']").isEqualTo("2017-10-29");
			assertThatJson(parsedJson).array("['stays']").contains("['departureDate']").isEqualTo("2017-10-29");
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['amount']").isEqualTo(105.0);
			assertThatJson(parsedJson).array("['stays']").field("['totalCost']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['stays']").array("['roomTypes']").isEmpty();
			assertThatJson(parsedJson).array("['stays']").contains("['noOfRooms']").isEqualTo(0);
			assertThatJson(parsedJson).array("['stays']").contains("['customerReference']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['historyRecordNumber']").isEqualTo(0);
			assertThatJson(parsedJson).array("['stays']").contains("['purchaseOrder']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['confirmationNumber']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['prePaidAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['paymentStatus']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cancelled']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['checkInOnline']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['checkedIn']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['rateClass']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['checkInDate']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['promotionText']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cellCodeLegend']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['carDataRequired']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['bookingStatus']").isEqualTo("PAST");
			assertThatJson(parsedJson).array("['stays']").contains("['amendable']").isEqualTo(false);
			assertThatJson(parsedJson).array("['stays']").contains("['guestHistoryNumber']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['bookedBy']").isEqualTo("");
			assertThatJson(parsedJson).array("['stays']").contains("['bookingFee']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['outstandingAmount']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['frequentBooking']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['rateName']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['cancellationId']").isNull();
			assertThatJson(parsedJson).array("['stays']").contains("['hotelName']").isEqualTo("London");
			assertThatJson(parsedJson).array("['stays']").contains("['leadGuest']").isEqualTo("");
			assertThatJson(parsedJson).array("['stays']").contains("['mpibooking']").isEqualTo(false);
	}

}
