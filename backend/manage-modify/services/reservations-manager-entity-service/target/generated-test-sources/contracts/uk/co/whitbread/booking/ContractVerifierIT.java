package uk.co.whitbread.booking;

import uk.co.whitbread.booking.ContractVerifierBaseTest;
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
public class ContractVerifierIT extends ContractVerifierBaseTest {

	@Test
	public void validate_cancel_booking_not_found() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("WB-Authorization", "Bearer test==")
					.body("{\"hotelId\":\"DASDAD\",\"bookingReference\":\"bookingReference\",\"arrivalDate\":\"2023-03-10\",\"sourceSystem\":\"BART\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/bookings/cancel");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
	}

	@Test
	public void validate_get_booking_information_Success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("hotelId","DUBAIR")
					.queryParam("bookingReference","AWQ9273512")
					.queryParam("arrival","2020-02-05")
					.queryParam("country","gb")
					.queryParam("language","en")
					.queryParam("surname","ln")
					.queryParam("channel","PI")
					.queryParam("subchannel","WEB")
					.queryParam("token","token")
					.get("/v1/bookings/information");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['reservationDetails']").field("['bookingReference']").isEqualTo("AWQ9273512");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['basketReference']").isEqualTo("AWQ-2383fc27-7bcd-4b90-827c-07e75d0c297e");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['hotelCode']").isEqualTo("DUBAIR");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['arrivalDate']").isEqualTo("2020-02-05");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['bookingStatus']").isEqualTo("PAST");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['departureDate']").isEqualTo("2020-02-07");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['nights']").isEqualTo(2);
			assertThatJson(parsedJson).field("['reservationDetails']").field("['noOfRooms']").isEqualTo(1);
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").field("['roomCost']").field("['amount']").isEqualTo(180);
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").field("['roomCost']").field("['currency']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").contains("['roomType']").isEqualTo("PPLDBL");
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").contains("['adults']").isEqualTo(1);
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").contains("['children']").isEqualTo(0);
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").contains("['cot']").isEqualTo(false);
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").field("['guest']").field("['title']").isEqualTo("Mr");
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").field("['guest']").field("['firstName']").isEqualTo("fn");
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").field("['guest']").field("['lastName']").isEqualTo("ln");
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").array("['kidsMeal']").isEmpty();
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").array("['adultsMeal']").isEmpty();
			assertThatJson(parsedJson).field("['reservationDetails']").array("['rooms']").array("['extrasItems']").isEmpty();
			assertThatJson(parsedJson).field("['reservationDetails']").field("['rateType']").isEqualTo("FLEXRATE");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['totalCost']").field("['amount']").isEqualTo(180);
			assertThatJson(parsedJson).field("['reservationDetails']").field("['totalCost']").field("['currency']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['newTotal']").field("['amount']").isEqualTo(180);
			assertThatJson(parsedJson).field("['reservationDetails']").field("['newTotal']").field("['currency']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['outstandingAmount']").field("['amount']").isEqualTo(180);
			assertThatJson(parsedJson).field("['reservationDetails']").field("['outstandingAmount']").field("['currency']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['previousTotal']").field("['amount']").isEqualTo(0);
			assertThatJson(parsedJson).field("['reservationDetails']").field("['previousTotal']").field("['currency']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['refund']").field("['amount']").isEqualTo(0);
			assertThatJson(parsedJson).field("['reservationDetails']").field("['refund']").field("['currency']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['hotelHasCityTaxForLeisure']").isEqualTo(false);
			assertThatJson(parsedJson).field("['reservationDetails']").field("['prepaidAmount']").field("['amount']").isEqualTo(0);
			assertThatJson(parsedJson).field("['reservationDetails']").field("['prepaidAmount']").field("['currency']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['paymentOption']").isEqualTo("PAY_ON_ARRIVAL");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['sourceSystem']").isEqualTo("OPERA");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['cancellationInfoResponse']").field("['amendable']").isEqualTo(false);
			assertThatJson(parsedJson).field("['reservationDetails']").field("['cancellationInfoResponse']").field("['cancelable']").isEqualTo(false);
			assertThatJson(parsedJson).field("['reservationDetails']").field("['rateDescription']").isEqualTo("<p>Flex: Amend or cancel up to 1pm on arrival day</p>\n");
			assertThatJson(parsedJson).field("['reservationDetails']").field("['payment']").field("['cardType']").isEqualTo("Va");
			assertThatJson(parsedJson).field("['checkInTime']").isEqualTo("15:00:00");
			assertThatJson(parsedJson).field("['checkOutTime']").isEqualTo("12:00:00");
	}

	@Test
	public void validate_get_reservations_Success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("WB-Authorization", "Bearer test==");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("business","false")
					.queryParam("companyId","companyId")
					.queryParam("employeeId","employeeId")
					.queryParam("filterValue","filterValue")
					.queryParam("includeCheckInBookings","false")
					.queryParam("continuationToken","continuationToken")
					.queryParam("channe;","pi")
					.queryParam("subchannel","WEB")
					.get("/v1/bookings/history");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['pageIndex']").isEqualTo(1);
			assertThatJson(parsedJson).field("['pageSize']").isEqualTo(1);
			assertThatJson(parsedJson).field("['totalSize']").isEqualTo(3);
			assertThatJson(parsedJson).field("['continuationToken']").isEqualTo("continuationToken");
			assertThatJson(parsedJson).field("['totals']").field("['upcoming']").isEqualTo(1);
			assertThatJson(parsedJson).field("['totals']").field("['cancelled']").isEqualTo(3);
			assertThatJson(parsedJson).field("['totals']").field("['checkedIn']").isEqualTo(5);
			assertThatJson(parsedJson).field("['totals']").field("['past']").isEqualTo(6);
			assertThatJson(parsedJson).array("['bookings']").contains("['hotelCode']").isEqualTo("DUNCRO");
			assertThatJson(parsedJson).array("['bookings']").contains("['arrivalDate']").isEqualTo("2023-01-06");
			assertThatJson(parsedJson).array("['bookings']").contains("['departureDate']").isEqualTo("2023-02-06");
			assertThatJson(parsedJson).array("['bookings']").field("['totalCost']").field("['amount']").isEqualTo("20");
			assertThatJson(parsedJson).array("['bookings']").field("['totalCost']").field("['currency']").isEqualTo("$");
			assertThatJson(parsedJson).array("['bookings']").field("['cityTax']").field("['amount']").isEqualTo("20");
			assertThatJson(parsedJson).array("['bookings']").field("['cityTax']").field("['currency']").isEqualTo("$");
			assertThatJson(parsedJson).array("['bookings']").array("['roomTypes']").contains("['roomType']").isEqualTo("roomType");
			assertThatJson(parsedJson).array("['bookings']").array("['roomTypes']").contains("['carDataPresent']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").array("['roomTypes']").field("['personDetails']").field("['title']").isEqualTo("title");
			assertThatJson(parsedJson).array("['bookings']").array("['roomTypes']").field("['personDetails']").field("['firstName']").isEqualTo("firstName");
			assertThatJson(parsedJson).array("['bookings']").array("['roomTypes']").field("['personDetails']").field("['lastName']").isEqualTo("lastName");
			assertThatJson(parsedJson).array("['bookings']").contains("['noOfRooms']").isEqualTo(1);
			assertThatJson(parsedJson).array("['bookings']").contains("['customerReference']").isEqualTo("customerReference");
			assertThatJson(parsedJson).array("['bookings']").contains("['historyRecordNumber']").isEqualTo(2);
			assertThatJson(parsedJson).array("['bookings']").contains("['purchaseOrder']").isEqualTo("purchaseOrder");
			assertThatJson(parsedJson).array("['bookings']").contains("['bookingReference']").isEqualTo("confirmationNumber");
			assertThatJson(parsedJson).array("['bookings']").field("['prePaidAmount']").field("['amount']").isEqualTo("20");
			assertThatJson(parsedJson).array("['bookings']").field("['prePaidAmount']").field("['currency']").isEqualTo("$");
			assertThatJson(parsedJson).array("['bookings']").contains("['paymentStatus']").isEqualTo("paymentStatus");
			assertThatJson(parsedJson).array("['bookings']").contains("['cancelled']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").contains("['checkInOnline']").isEqualTo(true);
			assertThatJson(parsedJson).array("['bookings']").contains("['checkedIn']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").contains("['rateClass']").isEqualTo("rateClass");
			assertThatJson(parsedJson).array("['bookings']").contains("['checkInDate']").isEqualTo("checkInDate");
			assertThatJson(parsedJson).array("['bookings']").contains("['promotionText']").isEqualTo("promotionText");
			assertThatJson(parsedJson).array("['bookings']").contains("['cellCodeLegend']").isEqualTo("cellCodeLegend");
			assertThatJson(parsedJson).array("['bookings']").contains("['carDataRequired']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").contains("['amendable']").isEqualTo(true);
			assertThatJson(parsedJson).array("['bookings']").contains("['guestHistoryNumber']").isEqualTo("guestHistoryNumber");
			assertThatJson(parsedJson).array("['bookings']").contains("['bookedBy']").isEqualTo("bookedBy");
			assertThatJson(parsedJson).array("['bookings']").field("['bookingFee']").field("['amount']").isEqualTo("20");
			assertThatJson(parsedJson).array("['bookings']").field("['bookingFee']").field("['currency']").isEqualTo("$");
			assertThatJson(parsedJson).array("['bookings']").field("['outstandingAmount']").field("['amount']").isEqualTo("20");
			assertThatJson(parsedJson).array("['bookings']").field("['outstandingAmount']").field("['currency']").isEqualTo("$");
			assertThatJson(parsedJson).array("['bookings']").contains("['frequentBooking']").isEqualTo("frequentBooking");
			assertThatJson(parsedJson).array("['bookings']").contains("['mpibooking']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").contains("['rateName']").isEqualTo("rateName");
			assertThatJson(parsedJson).array("['bookings']").contains("['cancelable']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").contains("['cancellationId']").isEqualTo("cancellationId");
			assertThatJson(parsedJson).array("['bookings']").contains("['hotelName']").isEqualTo("hotelName");
			assertThatJson(parsedJson).array("['bookings']").contains("['leadGuest']").isEqualTo("leadGuest");
			assertThatJson(parsedJson).array("['bookings']").contains("['leadGuestSurname']").isEqualTo("leadGuestSurname");
	}

	@Test
	public void validate_post_booking_history_Success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("WB-Authorization", "Bearer test==")
					.body("{\"business\":false,\"companyId\":\"companyId\",\"employeeId\":\"employeeId\",\"filterValue\":\"filterValue\",\"includeCheckInBookings\":false,\"continuationToken\":\"continuationToken\",\"channel\":\"pi\",\"subchannel\":\"WEB\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/bookings/history");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['pageIndex']").isEqualTo(1);
			assertThatJson(parsedJson).field("['pageSize']").isEqualTo(1);
			assertThatJson(parsedJson).field("['totalSize']").isEqualTo(3);
			assertThatJson(parsedJson).field("['continuationToken']").isEqualTo("continuationToken");
			assertThatJson(parsedJson).field("['totals']").field("['upcoming']").isEqualTo(1);
			assertThatJson(parsedJson).field("['totals']").field("['cancelled']").isEqualTo(3);
			assertThatJson(parsedJson).field("['totals']").field("['checkedIn']").isEqualTo(5);
			assertThatJson(parsedJson).field("['totals']").field("['past']").isEqualTo(6);
			assertThatJson(parsedJson).array("['bookings']").contains("['hotelCode']").isEqualTo("DUNCRO");
			assertThatJson(parsedJson).array("['bookings']").contains("['arrivalDate']").isEqualTo("2023-01-06");
			assertThatJson(parsedJson).array("['bookings']").contains("['departureDate']").isEqualTo("2023-02-06");
			assertThatJson(parsedJson).array("['bookings']").field("['totalCost']").field("['amount']").isEqualTo("20");
			assertThatJson(parsedJson).array("['bookings']").field("['totalCost']").field("['currency']").isEqualTo("$");
			assertThatJson(parsedJson).array("['bookings']").field("['cityTax']").field("['amount']").isEqualTo("20");
			assertThatJson(parsedJson).array("['bookings']").field("['cityTax']").field("['currency']").isEqualTo("$");
			assertThatJson(parsedJson).array("['bookings']").array("['roomTypes']").contains("['roomType']").isEqualTo("roomType");
			assertThatJson(parsedJson).array("['bookings']").array("['roomTypes']").contains("['carDataPresent']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").array("['roomTypes']").field("['personDetails']").field("['title']").isEqualTo("title");
			assertThatJson(parsedJson).array("['bookings']").array("['roomTypes']").field("['personDetails']").field("['firstName']").isEqualTo("firstName");
			assertThatJson(parsedJson).array("['bookings']").array("['roomTypes']").field("['personDetails']").field("['lastName']").isEqualTo("lastName");
			assertThatJson(parsedJson).array("['bookings']").contains("['noOfRooms']").isEqualTo(1);
			assertThatJson(parsedJson).array("['bookings']").contains("['customerReference']").isEqualTo("customerReference");
			assertThatJson(parsedJson).array("['bookings']").contains("['historyRecordNumber']").isEqualTo(2);
			assertThatJson(parsedJson).array("['bookings']").contains("['purchaseOrder']").isEqualTo("purchaseOrder");
			assertThatJson(parsedJson).array("['bookings']").contains("['bookingReference']").isEqualTo("confirmationNumber");
			assertThatJson(parsedJson).array("['bookings']").field("['prePaidAmount']").field("['amount']").isEqualTo("20");
			assertThatJson(parsedJson).array("['bookings']").field("['prePaidAmount']").field("['currency']").isEqualTo("$");
			assertThatJson(parsedJson).array("['bookings']").contains("['paymentStatus']").isEqualTo("paymentStatus");
			assertThatJson(parsedJson).array("['bookings']").contains("['cancelled']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").contains("['checkInOnline']").isEqualTo(true);
			assertThatJson(parsedJson).array("['bookings']").contains("['checkedIn']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").contains("['rateClass']").isEqualTo("rateClass");
			assertThatJson(parsedJson).array("['bookings']").contains("['checkInDate']").isEqualTo("checkInDate");
			assertThatJson(parsedJson).array("['bookings']").contains("['promotionText']").isEqualTo("promotionText");
			assertThatJson(parsedJson).array("['bookings']").contains("['cellCodeLegend']").isEqualTo("cellCodeLegend");
			assertThatJson(parsedJson).array("['bookings']").contains("['carDataRequired']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").contains("['amendable']").isEqualTo(true);
			assertThatJson(parsedJson).array("['bookings']").contains("['guestHistoryNumber']").isEqualTo("guestHistoryNumber");
			assertThatJson(parsedJson).array("['bookings']").contains("['bookedBy']").isEqualTo("bookedBy");
			assertThatJson(parsedJson).array("['bookings']").field("['bookingFee']").field("['amount']").isEqualTo("20");
			assertThatJson(parsedJson).array("['bookings']").field("['bookingFee']").field("['currency']").isEqualTo("$");
			assertThatJson(parsedJson).array("['bookings']").field("['outstandingAmount']").field("['amount']").isEqualTo("20");
			assertThatJson(parsedJson).array("['bookings']").field("['outstandingAmount']").field("['currency']").isEqualTo("$");
			assertThatJson(parsedJson).array("['bookings']").contains("['frequentBooking']").isEqualTo("frequentBooking");
			assertThatJson(parsedJson).array("['bookings']").contains("['mpibooking']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").contains("['rateName']").isEqualTo("rateName");
			assertThatJson(parsedJson).array("['bookings']").contains("['cancelable']").isEqualTo(false);
			assertThatJson(parsedJson).array("['bookings']").contains("['cancellationId']").isEqualTo("cancellationId");
			assertThatJson(parsedJson).array("['bookings']").contains("['hotelName']").isEqualTo("hotelName");
			assertThatJson(parsedJson).array("['bookings']").contains("['leadGuest']").isEqualTo("leadGuest");
			assertThatJson(parsedJson).array("['bookings']").contains("['leadGuestSurname']").isEqualTo("leadGuestSurname");
	}

	@Test
	public void validate_send_booking_confirmation_email_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("WB-Authorization", "Bearer dummy-token")
					.body("{\"email\":\"test@mail.com\",\"hotelId\":\"LONMON\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/bookings/confirmation");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
	}

	@Test
	public void validate_send_booking_invoice_email_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("WB-Authorization", "Bearer dummy-token")
					.body("{\"email\":\"test@mail.com\",\"hotelId\":\"LONMON\",\"invoiceRecordNumber\":\"15\",\"bookingReference\":\"bookingReference\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/bookings/invoice");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
	}

	@Test
	public void validate_send_reservation_confirmationOrInvoice_email_Success_200() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("WB-Authorization", "Bearer dummy-token")
					.body("{\"email\":\"test@mail.com\",\"hotelId\":\"DUNCRO\",\"bookingReference\":\"AKU8375015\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/bookings/confirmation");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
	}

	@Test
	public void validate_send_reservation_confirmation_email_Success_200() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("WB-Authorization", "Bearer dummy-token")
					.body("{\"email\":\"test@mail.com\",\"hotelId\":\"DUNCRO\",\"bookingReference\":\"AKU8375015\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/bookings/confirmation");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
	}

}
