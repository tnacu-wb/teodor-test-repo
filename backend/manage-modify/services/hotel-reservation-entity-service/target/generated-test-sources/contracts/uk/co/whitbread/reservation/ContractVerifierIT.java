package uk.co.whitbread.reservation;

import uk.co.whitbread.reservation.ContractVerifierBaseTest;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Disabled;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.response.ResponseOptions;

import static org.springframework.cloud.contract.verifier.assertion.SpringCloudContractAssertions.assertThat;
import static org.springframework.cloud.contract.verifier.util.ContractVerifierUtil.*;
import static com.toomuchcoding.jsonassert.JsonAssertion.assertThatJson;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;

@SuppressWarnings("rawtypes")
public class ContractVerifierIT extends ContractVerifierBaseTest {

	@Test
	@Disabled
	public void validate_amendLogic_CCUI_CNP() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"originalBookingRef\":\"GAN-674ab8b3-a59b-49af-97f0-f789d99bdb0a\",\"tempBookingRef\":\"GAN-53219109-9ee9-404e-8843-77be149f8e12\",\"token\":\"token\",\"bookingChannel\":{\"channel\":\"CCUI\",\"subchannel\":\"WEB\",\"language\":\"EN\"},\"paymentOptionSelected\":\"PAY_ON_ARRIVAL\",\"environment\":\"https://www.dit.premierinn.digital\",\"emailAddress\":\"abc@test.com\",\"ccuiExtraItems\":{\"businessItems\":{\"purchaseOrderNumber\":\"Abc1234556\",\"customReferenceNumber\":\"Cust543\",\"businessAllowances\":[{\"allowance\":\"meal deal\",\"budget\":30,\"isAuthorised\":true}],\"businessNotes\":\"Bla bla bla\"},\"accountCompanyItems\":{\"companyNumber\":\"33547\",\"charges\":\"one two\",\"companyId\":\"company33\"},\"nonguaranteedItems\":{\"typeOfCaller\":\"typeOfCaller\"},\"sendMail\":true,\"cardPresent\":false,\"addressCompanyName\":\"street company dfg\"},\"paymentRequest\":{\"payment\":{\"type\":\"a\",\"subType\":\"b\"}},\"paymentOption\":\"RESERVE_WITHOUT_CARD\",\"subPaymentType\":\"\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/amend/confirmAmendLogic");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['payment']").field("['status']").isEqualTo("NOT_REQUIRED");
			assertThatJson(parsedJson).field("['payment']").field("['paymentRequiredDetails']").isNull();
	}

	@Test
	public void validate_amendPaymentPage_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("originalBookingRef","AQN-6af52126-37b6-434f-8ab4-e8be7b7d52d0")
					.queryParam("tempBookingRef","AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e8")
					.queryParam("token","1234567890")
					.queryParam("bookingChannel.channel","CCUI")
					.queryParam("bookingChannel.subchannel","WEB")
					.queryParam("bookingChannel.language","EN")
					.queryParam("country","gb")
					.get("/v1/reservations/amend/paymentOptions");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['discount']").isEqualTo(0);
			assertThatJson(parsedJson).field("['paymentOption']").field("['payNow']").isEqualTo(false);
			assertThatJson(parsedJson).field("['paymentOption']").field("['payOnArrival']").isEqualTo(true);
			assertThatJson(parsedJson).field("['paymentType']").isEqualTo("PIBA");
			assertThatJson(parsedJson).field("['cardPresent']").field("['display']").isEqualTo(true);
			assertThatJson(parsedJson).field("['cardPresent']").field("['value']").isEqualTo(false);
			assertThatJson(parsedJson).field("['eckoh']").field("['display']").isEqualTo(true);
			assertThatJson(parsedJson).field("['eckoh']").field("['enabled']").isEqualTo(false);
			assertThatJson(parsedJson).field("['cardHolderName']").field("['display']").isEqualTo(true);
			assertThatJson(parsedJson).field("['cardHolderName']").field("['name']").isEqualTo("Cristi");
			assertThatJson(parsedJson).field("['cardHolderName']").field("['surname']").isEqualTo("Christian");
			assertThatJson(parsedJson).field("['billingAddress']").field("['display']").isEqualTo(true);
			assertThatJson(parsedJson).field("['emailPreference']").field("['display']").isEqualTo(true);
			assertThatJson(parsedJson).field("['emailPreference']").field("['send']").isEqualTo(true);
			assertThatJson(parsedJson).field("['emailPreference']").field("['emailAddress']").isEqualTo("ourtestaccount@mailinator.com");
			assertThatJson(parsedJson).field("['allowances']").field("['display']").isEqualTo(true);
			assertThatJson(parsedJson).field("['allowances']").array("['values']").contains("['allowance']").isEqualTo("BFADCT");
			assertThatJson(parsedJson).field("['allowances']").array("['values']").contains("['budget']").isNull();
			assertThatJson(parsedJson).field("['allowances']").array("['values']").contains("['allowance']").isEqualTo("mealDeal");
			assertThatJson(parsedJson).field("['allowances']").array("['values']").contains("['allowance']").isEqualTo("accommodation");
			assertThatJson(parsedJson).field("['purchaseOrder']").field("['display']").isEqualTo(true);
			assertThatJson(parsedJson).field("['purchaseOrder']").field("['value']").isNull();
			assertThatJson(parsedJson).field("['companyRef']").field("['display']").isEqualTo(true);
			assertThatJson(parsedJson).field("['companyRef']").field("['value']").isEqualTo("biciclete srl");
			assertThatJson(parsedJson).field("['a2cDetails']").field("['display']").isEqualTo(false);
			assertThatJson(parsedJson).field("['a2cDetails']").field("['number']").isNull();
			assertThatJson(parsedJson).field("['a2cDetails']").field("['name']").isNull();
			assertThatJson(parsedJson).field("['a2cDetails']").field("['address']").isNull();
			assertThatJson(parsedJson).field("['a2cDetails']").field("['postcode']").isNull();
			assertThatJson(parsedJson).field("['preAuthCharges']").field("['display']").isEqualTo(false);
			assertThatJson(parsedJson).field("['preAuthCharges']").field("['charges']").isNull();
			assertThatJson(parsedJson).field("['hotelCode']").isEqualTo("LONEUS");
			assertThatJson(parsedJson).field("['hotelName']").isEqualTo("London Euston");
			assertThatJson(parsedJson).field("['brand']").isEqualTo("PI");
	}

	@Test
	@Disabled
	public void validate_amend_add_new_room_successResponse() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"tempBookingRef\":\"AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7\",\"roomOccupancy\":{\"adultsNumber\":1,\"childrenNumber\":0,\"cotRequired\":false},\"leadGuest\":{\"title\":\"mrs\",\"firstName\":\"cat\",\"lastName\":\"dog\",\"emailAddress\":\"dd@bb.com\"},\"roomType\":\"DB\",\"bookingChannel\":{\"channel\":\"PI\",\"subchannel\":\"WEB\",\"language\":\"EN\"}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/reservations/amend/addNewRoom");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['tempBookingRef']").isEqualTo("AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7");
	}

	@Test
	@Disabled
	public void validate_amend_confirmation_prices_successResponse() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"tempBookingRef\":\"AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7\",\"originalBookingRef\":\"AKU-276afd23-b1fd-4cbc-96f1-d0a1f8100000\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/reservations/amend/amendConfirmationPrices");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['previousTotal']").isEqualTo(1058.94);
			assertThatJson(parsedJson).field("['newTotalCost']").isEqualTo(2057.94);
			assertThatJson(parsedJson).field("['outstandingBalance']").isEqualTo(1058.94);
	}

	@Test
	@Disabled
	public void validate_amend_edit_room_500Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"tempBookingRef\":\"AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7\",\"reservationId\":\"9966557\",\"roomOccupancy\":{\"adultsNumber\":1,\"childrenNumber\":0,\"cotRequired\":false},\"leadGuest\":{\"title\":\"mrs\",\"firstName\":\"cat\",\"lastName\":\"dog\",\"emailAddress\":\"dd@bb.com\"},\"roomType\":\"DB\",\"bookingChannel\":{\"channel\":\"PI\",\"subchannel\":\"WEB\",\"language\":\"EN\"}}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/amend/editRoom");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo(500);
			assertThatJson(parsedJson).field("['debugMessage']").isEqualTo("Couldn't edit your room");
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("internal.server.exception");
	}

	@Test
	@Disabled
	public void validate_amend_edit_room_successResponse() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"tempBookingRef\":\"AKU-40d698a2-b56a-481f-89a7-754148c5865e\",\"reservationId\":\"12345\",\"roomOccupancy\":{\"adultsNumber\":1,\"childrenNumber\":0,\"cotRequired\":false},\"leadGuest\":{\"title\":\"mrs\",\"firstName\":\"cat\",\"lastName\":\"dog\",\"emailAddress\":\"dd@bb.com\"},\"roomType\":\"DB\",\"bookingChannel\":{\"channel\":\"PI\",\"subchannel\":\"WEB\",\"language\":\"EN\"}}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/amend/editRoom");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['tempBookingRef']").isEqualTo("AKU-40d698a2-b56a-481f-89a7-754148c5865e");
	}

	@Test
	public void validate_amend_stay_dates_400Response_invalid_date() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"tempBookingRef\":\"AKU-40d698a2-b56a-481f-89a7-754148c5865e\",\"newStartDate\":\"2099-02-20\",\"newEndDate\":\"2099-02-20\",\"bookingChannel\":{\"channel\":\"PI\",\"subchannel\":\"WEB\",\"language\":\"EN\"}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/reservations/amendStayDates");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
	}

	@Test
	@Disabled
	public void validate_amend_stay_dates_successResponse() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"tempBookingRef\":\"AKU-40d698a2-b56a-481f-89a7-754148c5865e\",\"newStartDate\":\"2030-02-20\",\"newEndDate\":\"2030-02-24\",\"bookingChannel\":{\"channel\":\"PI\",\"subchannel\":\"WEB\",\"language\":\"EN\"}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/reservations/amendStayDates");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['tempBasket']").isEqualTo("AKU-40d698a2-b56a-481f-89a7-754148c5865e");
	}

	@Test
	@Disabled
	public void validate_amend_summary_successResponse() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"tempBookingRef\":\"AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7\",\"originalBookingRef\":\"AKU-276afd23-b1fd-4cbc-96f1-d0a1f8100000\",\"token\":\"4111111111111111\",\"channel\":\"PI\",\"subchannel\":\"WEB\",\"language\":\"EN\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/reservations/amend/summary");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['charitable']").isEqualTo(0.0);
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['previousTotal']").isEqualTo(1058.94);
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['balancePaid']").isEqualTo(0.0);
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['payOnArrival']").isEqualTo(2057.94);
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['refund']").isEqualTo(0.0);
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['nonRefundable']").isEqualTo(0.0);
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['totalCost']").isEqualTo(2057.94);
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['balanceAuthorised']").isEqualTo(0.0);
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['paymentOption']").field("['payOnArrival']").isEqualTo(true);
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['paymentOption']").field("['payNow']").isEqualTo(false);
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['paymentCardDetails']").field("['cardNumberMasked']").isEqualTo("XXXXXXXXXXXX1100");
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['paymentCardDetails']").field("['token']").isEqualTo("5479321898918651100");
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['paymentCardDetails']").field("['expirationDate']").isEqualTo("2026-05-31");
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['paymentCardDetails']").field("['cardType']").isEqualTo("Mc");
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['paymentCardDetails']").field("['cardHolderName']").isEqualTo("Testerson");
			assertThatJson(parsedJson).field("['data']").field("['amendSummary']").field("['paymentCardDetails']").field("['cardNumberLast4Digits']").isEqualTo("1100");
	}

	@Test
	public void validate_attach_ReservationProfile_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"hotelId\":\"HOTELTEST\",\"profileId\":\"1234\",\"reservationIds\":[\"1234\"]}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/reservations/profiles");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
	}

	@Test
	public void validate_cancel_onHold_Reservation_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"hotelId\":\"MANOLD\",\"basketReference\":\"TestOnHold\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/cancellations/on-hold");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['basketReference']").isEqualTo("TestOnHold");
	}

	@Test
	public void validate_confirm_Reservation_BadRequest_400Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"reservationId\":\"\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/reservations/confirm");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo(401);
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("validation.error.form");
			assertThatJson(parsedJson).array("['details']").contains("['elementId']").isEqualTo("reservationId");
			assertThatJson(parsedJson).array("['details']").contains("['errTextTemplate']").isEqualTo("must not be empty");
			assertThatJson(parsedJson).array("['details']").contains("['elementId']").isEqualTo("paymentOption");
			assertThatJson(parsedJson).array("['details']").contains("['errTextTemplate']").isEqualTo("must not be null");
			assertThatJson(parsedJson).array("['details']").contains("['elementId']").isEqualTo("hotelId");
	}

	@Test
	public void validate_confirm_Reservation_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"paymentOption\":\"PAY_ON_ARRIVAL\",\"reservationId\":\"36116\",\"hotelId\":\"TKINPT\",\"operaPaymentMethod\":\"DAT\",\"paymentCard\":{\"cardType\":\"AT\",\"token\":\"4111111111111111\",\"expirationDate\":\"2025-03-31\",\"cardHolderName\":\"Charlie\",\"cardNumberLast4Digits\":\"1234\"}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/reservations/confirm");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['reservationIdList']").contains("['id']").isEqualTo("36116");
			assertThatJson(parsedJson).array("['reservationIdList']").contains("['type']").isEqualTo("Reservation");
			assertThatJson(parsedJson).array("['reservationIdList']").contains("['id']").isEqualTo("468853");
			assertThatJson(parsedJson).array("['reservationIdList']").contains("['type']").isEqualTo("Confirmation");
			assertThatJson(parsedJson).field("['roomStay']").field("['arrivalDate']").isEqualTo("2022-07-06");
			assertThatJson(parsedJson).field("['roomStay']").field("['departureDate']").isEqualTo("2022-07-08");
			assertThatJson(parsedJson).field("['reservationGuest']").field("['givenName']").isEqualTo("Tester");
			assertThatJson(parsedJson).field("['reservationGuest']").field("['surName']").isEqualTo("Testerson");
			assertThatJson(parsedJson).field("['reservationStatus']").isEqualTo("RESERVED");

		// and:
			assertThat(parsedJson.read("$.hotelId", String.class)).matches("(?:^|\\W)TKINPT(?:$|\\W)");
	}

	@Test
	@Disabled
	public void validate_confirm_Reservation_WrongCardType_500Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"paymentOption\":\"PAY_ON_ARRIVAL\",\"hotelId\":\"LONEUS\",\"reservationId\":\"34865\",\"operaPaymentMethod\":\"DAA\",\"paymentCard\":{\"cardType\":\"AA\",\"token\":\"4111111111111111\",\"expirationDate\":\"2025-03-31\",\"cardHolderName\":\"Charlie\",\"cardNumberLast4Digits\":\"1234\"}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/reservations/confirm");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errorCode']").isEqualTo(900);
			assertThatJson(parsedJson).field("['debugMessage']").isEqualTo("An error was returned by OHIP Adapter!");
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("internal.server.exception");
	}

	@Test
	public void validate_create_ReservationGuest_Success_201Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"basketReference\":\"AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7\",\"hotelId\":\"TestId\",\"reasonForStay\":\"LEI\",\"booker\":{\"title\":\"Mrs\",\"firstName\":\"John\",\"lastName\":\"Carry\",\"emailAddress\":\"john.carry@email.com\",\"acceptFutureMailing\":true,\"mobile\":\"+3905678754\",\"landline\":\"+3905678754\",\"address\":{\"addressType\":\"BUSINESS\",\"postalCode\":\"WC2N 5DU\",\"addressLine1\":\"4 Brockley Avenue\",\"addressLine2\":\"addressline2\",\"addressLine3\":\"addressline3\",\"addressLine4\":\"addressline4\",\"countryCode\":\"UK\",\"cityName\":\"London\",\"companyName\":\"companyName\"}},\"stayingGuests\":[{\"sameAsBooker\":\"false\",\"stayingGuestDetails\":{\"title\":\"Mrs\",\"firstName\":\"Debbie\",\"lastName\":\"Doe\",\"address\":{\"addressType\":\"HOME\",\"postalCode\":\"EC1A 1BB\",\"addressLine1\":\"4 Brockley Avenue\",\"addressLine2\":\"London District 2 \",\"addressLine3\":\"Greater London - sub district 2 \",\"addressLine4\":\"Greater London - street 22\",\"countryCode\":\"GB\",\"cityName\":\"London\"},\"additionalDetails\":{\"dob\":\"1996-07-15\",\"nationality\":\"Briton\",\"passportNumber\":\"ABCD5679\"}}}],\"sendEmailConfirmation\":\"true\",\"sendEmailInvoice\":\"true\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/reservations/guests");

		// then:
			assertThat(response.statusCode()).isEqualTo(201);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['basketReference']").isEqualTo("AKU-276afd23-b1fd-4cbc-96f1-d0a1f814d4e7");
	}

	@Test
	public void validate_findBooking_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("resNo","ABCD123456")
					.queryParam("lastName","Caesar")
					.queryParam("arrivalDate","2023-12-30")
					.queryParam("country","gb")
					.queryParam("language","en")
					.queryParam("channel","PI")
					.queryParam("subchannel","MOBILE")
					.get("/v1/reservations/find");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['sourcePms']").isEqualTo("Opera");
			assertThatJson(parsedJson).field("['cookieName']").isEqualTo("pi.single-booking");
			assertThatJson(parsedJson).field("['ref']").isEqualTo("ABCD123456");
			assertThatJson(parsedJson).field("['redirectBase']").isEqualTo("/gb/en/account/dashboard");
			assertThatJson(parsedJson).field("['minutesTillExpiry']").isEqualTo("30");
			assertThatJson(parsedJson).field("['basketReference']").isEqualTo("TST-16a014c5-d8a4-4418-8d15-2537660f08e9");
	}

	@Test
	public void validate_get_MarketingPreferences_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("hotelId","MANOLD")
					.queryParam("reservationId","853004")
					.get("/v1/reservations/marketingPreferences");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['optIn']").isEqualTo(true);
			assertThatJson(parsedJson).field("['customer']").field("['title']").isEqualTo("Mr");
			assertThatJson(parsedJson).field("['customer']").field("['firstName']").isEqualTo("test");
			assertThatJson(parsedJson).field("['customer']").field("['lastName']").isEqualTo("james");
			assertThatJson(parsedJson).field("['customer']").field("['country']").isEqualTo("GB");
			assertThatJson(parsedJson).field("['customer']").field("['language']").isNull();
			assertThatJson(parsedJson).field("['contactValue']").isEqualTo("test@mail.com");
	}

	@Test
	public void validate_get_ReservationsPackages_ByBasketReference_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("hotelId","HOTELTEST")
					.queryParam("basketReferenceId","TestPackages123")
					.get("/v1/reservations/ancillaries");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['roomsSelections']").array("['packagesSelection']").contains("['id']").isEqualTo("MTEST");
			assertThatJson(parsedJson).array("['roomsSelections']").array("['packagesSelection']").contains("['noOfSelections']").isEqualTo(1);
	}

	@Test
	public void validate_get_Reservations_ByBasketReference_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("hotelId","TESTHOTEL")
					.queryParam("basketReference","TestId1234567")
					.queryParam("limit","20")
					.queryParam("offset","0")
					.get("/v1/reservations");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").array("['reservationIdList']").contains("['id']").isEqualTo("17655");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").array("['reservationIdList']").contains("['type']").isEqualTo("Reservation");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").array("['reservationIdList']").contains("['id']").isEqualTo("40977");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").array("['reservationIdList']").contains("['type']").isEqualTo("Confirmation");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").array("['externalReferences']").contains("['id']").isEqualTo("123");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").array("['externalReferences']").contains("['idContext']").isEqualTo("WB_BASKET");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['arrivalDate']").isEqualTo("2022-04-11");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['departureDate']").isEqualTo("2022-04-12");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['adultCount']").isEqualTo(1);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['childCount']").isEqualTo(0);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['roomClass']").isEqualTo("ST");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['roomType']").isEqualTo("DOUBLE");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['numberOfRooms']").isEqualTo(1);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['ratePlanCode']").isEqualTo("FLEXRATE");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['rateAmount']").field("['amount']").isEqualTo(200);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['rateAmount']").field("['currencyCode']").isEqualTo("USD");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['rateSuppressed']").isEqualTo(false);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['bookingChannelCode']").isEqualTo("OHIP");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['fixedRate']").isEqualTo(true);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['marketCode']").isEqualTo("LEISURE");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['sourceCode']").isEqualTo("PHONE");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['roomTypeCharged']").isEqualTo("DOUBLE");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['roomNumberLocked']").isEqualTo(false);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['pseudoRoom']").isEqualTo(false);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['reservationGuest']").field("['givenName']").isEqualTo("Jane");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['reservationGuest']").field("['surname']").isEqualTo("Smith");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['reservationGuest']").field("['language']").isEqualTo("E");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['reservationGuest']").field("['guestRestricted']").isEqualTo(false);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['reservationGuest']").field("['id']").isEqualTo("31783");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['reservationGuest']").field("['type']").isEqualTo("Profile");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").contains("['hotelId']").isEqualTo("HOTELTEST");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").contains("['hotelName']").isEqualTo("DIGI Euston");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").contains("['roomStayReservation']").isEqualTo(true);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").array("['reservationIdList']").contains("['id']").isEqualTo("17400");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").array("['reservationIdList']").contains("['id']").isEqualTo("40673");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").array("['externalReferences']").contains("['idContext']").isEqualTo("Basket");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['arrivalDate']").isEqualTo("2022-08-01");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['departureDate']").isEqualTo("2022-08-02");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['rateAmount']").field("['amount']").isEqualTo(120);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['rateAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['fixedRate']").isEqualTo(false);
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['roomStay']").field("['marketCode']").isEqualTo("BUSINESS");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['reservationGuest']").field("['givenName']").isEqualTo("Sarah");
			assertThatJson(parsedJson).field("['reservations']").array("['reservationInfo']").field("['reservationGuest']").field("['id']").isEqualTo("31987");
			assertThatJson(parsedJson).field("['reservations']").field("['totalPages']").isEqualTo(1);
			assertThatJson(parsedJson).field("['reservations']").field("['offset']").isEqualTo(20);
			assertThatJson(parsedJson).field("['reservations']").field("['limit']").isEqualTo(20);
			assertThatJson(parsedJson).field("['reservations']").field("['hasMore']").isEqualTo(false);
			assertThatJson(parsedJson).field("['reservations']").field("['totalResults']").isEqualTo(2);
	}

	@Test
	public void validate_get_Reservations_JustByBasketReference_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("priceBreakdownNeeded","false")
					.get("/v1/reservations/basket/TestId1234567");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['reservationByIdList']").array("['reservationGuestList']").contains("['givenName']").isEqualTo("Sarah");
			assertThatJson(parsedJson).array("['reservationByIdList']").array("['reservationGuestList']").contains("['surName']").isEqualTo("Smith");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['adultsNumber']").isEqualTo(1);
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['childrenNumber']").isEqualTo(0);
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['roomPrice']").isEqualTo(5);
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['roomType']").isEqualTo("TWINRM");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['ratePlanCode']").isEqualTo("FLEXRATE");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['arrivalDate']").isEqualTo("2022-08-20");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['departureDate']").isEqualTo("2022-08-21");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['checkInTime']").isEqualTo("12:00");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['checkOutTime']").isEqualTo("13:00");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['cellCode']").isEqualTo("cellCode");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['roomNumber']").isEqualTo("120");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").field("['bookingChannel']").isEqualTo("PI.com");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").array("['ratesPerNight']").contains("['startDate']").isEqualTo("2022-08-20");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").array("['ratesPerNight']").contains("['pricePerNight']").isEqualTo(30);
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").array("['ratesPerNight']").contains("['grossPricePerNight']").isNull();
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").array("['ratesPerNight']").contains("['cityTaxPerNight']").isEqualTo(0);
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").array("['ratesPerNight']").contains("['vatRate']").isNull();
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").array("['ratesPerNight']").contains("['cityTaxAmountBeforeTax']").isNull();
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['roomStay']").array("['ratesPerNight']").contains("['cityTaxVat']").isNull();
			assertThatJson(parsedJson).array("['reservationByIdList']").array("['depositPolicies']").field("['amountPaid']").field("['amount']").isEqualTo(0);
			assertThatJson(parsedJson).array("['reservationByIdList']").array("['depositPolicies']").field("['amountDue']").field("['amount']").isEqualTo(0);
			assertThatJson(parsedJson).array("['reservationByIdList']").array("['depositPolicies']").contains("['policyCode']").isEqualTo("OA");
			assertThatJson(parsedJson).array("['reservationByIdList']").array("['reservationPackageList']").isEmpty();
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['billing']").field("['address']").field("['companyName']").isEqualTo("Whitbread");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['billing']").field("['address']").field("['country']").isEqualTo("AU");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['billing']").field("['address']").field("['addressLine1']").isEqualTo("Riverside Corporate Park");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['billing']").field("['address']").field("['addressLine2']").isEqualTo("4 Julius Avenue");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['billing']").field("['address']").field("['postalCode']").isEqualTo("2113");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['billing']").field("['email']").isEqualTo("no email info provided");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['billing']").field("['firstName']").isEqualTo("Sarah");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['billing']").field("['lastName']").isEqualTo("Smith");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['billing']").field("['telephone']").isEqualTo("no telephone info provided");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['paymentCard']").field("['cardNumberMasked']").isEqualTo("XXXXXXXXXXXX1103");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['paymentCard']").field("['token']").isEqualTo("4216333880397891103");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['paymentCard']").field("['expirationDate']").isEqualTo("2026-05-31");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['paymentCard']").field("['cardType']").isEqualTo("Va");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['reservationOverrideReasons']").field("['reasonCode']").isEqualTo("ILL");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['reservationOverrideReasons']").field("['reasonName']").isEqualTo("Illness");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['reservationOverrideReasons']").field("['callerName']").isEqualTo("John Doe");
			assertThatJson(parsedJson).array("['reservationByIdList']").field("['reservationOverrideReasons']").field("['managerName']").isEqualTo("James Bond");
			assertThatJson(parsedJson).array("['reservationByIdList']").contains("['reservationOverridden']").isEqualTo(true);
			assertThatJson(parsedJson).array("['reservationByIdList']").contains("['balanceAmount']").isEqualTo(60);
			assertThatJson(parsedJson).field("['totalCost']").isEqualTo(0);
			assertThatJson(parsedJson).field("['policyCode']").isEqualTo("OA");
			assertThatJson(parsedJson).field("['currencyCode']").isEqualTo("USD");
	}

	@Test
	public void validate_get_cancellation_policies_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("hotelId","HOTELTEST")
					.queryParam("basketReference","basketCancellation")
					.queryParam("ratePlanCode","")
					.queryParam("arrivalDate","")
					.get("/v1/reservations/cancellationPolicies");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['time']").isEqualTo("2022-04-04T01:00:00+01:00");
			assertThatJson(parsedJson).field("['text']").isEqualTo("Cancellations after 1pm on the day of arrival charged 100% of 1 night");
	}

	@Test
	public void validate_post_copyBooking_POA_PaymentType_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"originalBasketReference\":\"AEG5222658\",\"bookingChannel\":{\"channel\":\"PI\",\"subchannel\":\"WEB\",\"language\":\"EN\"},\"token\":\"token\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/reservations/copy");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['copyBasketReference']").isEqualTo("AEG5621201");
	}

	@Test
	public void validate_removeRoom_successResponse() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("tempBookingRef","AWM5555555")
					.queryParam("reservationId","6657777")
					.queryParam("channel","PI")
					.queryParam("subchannel","WEB")
					.queryParam("language","EN")
					.queryParam("token","token")
					.post("/v1/reservations/rooms/delete");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['tempBookingRef']").isEqualTo("AWM5555555");
	}

	@Test
	public void validate_save_Reservation_ByReservationId_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"basketReferenceId\":\"TestId1234567\",\"arrivalDate\":\"2023-10-10\",\"departureDate\":\"2023-10-12\",\"hotelId\":\"HOTELCODE\",\"previousRoomsSelections\":[{\"reservationId\":\"3514830\",\"packagesSelection\":[]}],\"roomsSelections\":[{\"reservationId\":\"3514830\",\"packagesSelection\":[{\"id\":\"BFADBF\",\"noOfSelections\":1},{\"id\":\"CITYTAX\",\"noOfSelections\":1}]}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/ancillaries/reservation-id");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['basketReference']").isEqualTo("TestId1234567");
	}

	@Test
	public void validate_save_Reservation_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"basketReferenceId\":\"TestId1234567\",\"arrivalDate\":\"2022-04-02\",\"departureDate\":\"2022-04-03\",\"roomsSelections\":[{\"packagesSelection\":[{\"id\":\"PIBTEST\",\"noOfSelections\":1}]}],\"hotelId\":\"HOTELCODE\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/ancillaries");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['basketReference']").isEqualTo("TestId1234567");
	}

	@Test
	public void validate_searchBookingsOpera_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("bookingReference","GAA8056792")
					.queryParam("bookerLastName","Pop")
					.queryParam("arrivalDate","3023-01-23")
					.get("/v1/reservations/search");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['bookings']").contains("['bookingReference']").isEqualTo("GAA8056792");
			assertThatJson(parsedJson).array("['bookings']").contains("['status']").isEqualTo("UPCOMING");
			assertThatJson(parsedJson).array("['bookings']").contains("['sourcePms']").isEqualTo("OPERA");
			assertThatJson(parsedJson).array("['bookings']").contains("['hotelId']").isEqualTo("FRAMTI");
			assertThatJson(parsedJson).array("['bookings']").contains("['hotelName']").isEqualTo("Frankfurt Messe");
			assertThatJson(parsedJson).array("['bookings']").field("['booker']").field("['title']").isEqualTo("Master");
			assertThatJson(parsedJson).array("['bookings']").field("['booker']").field("['firstName']").isEqualTo("John");
			assertThatJson(parsedJson).array("['bookings']").field("['booker']").field("['lastName']").isEqualTo("Pop");
			assertThatJson(parsedJson).array("['bookings']").array("['stayingGuests']").contains("['title']").isEqualTo("Master");
			assertThatJson(parsedJson).array("['bookings']").array("['stayingGuests']").contains("['firstName']").isEqualTo("John");
			assertThatJson(parsedJson).array("['bookings']").array("['stayingGuests']").contains("['lastName']").isEqualTo("Pop");
			assertThatJson(parsedJson).array("['bookings']").contains("['arrivalDate']").isEqualTo("3023-01-23");
			assertThatJson(parsedJson).array("['bookings']").contains("['departureDate']").isEqualTo("3023-01-24");
			assertThatJson(parsedJson).array("['bookings']").contains("['totalCost']").isEqualTo(0);
			assertThatJson(parsedJson).array("['bookings']").contains("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['totalPages']").isEqualTo(1);
			assertThatJson(parsedJson).field("['offset']").isEqualTo(20);
			assertThatJson(parsedJson).field("['limit']").isEqualTo(20);
			assertThatJson(parsedJson).field("['hasMore']").isEqualTo(false);
			assertThatJson(parsedJson).field("['totalResults']").isEqualTo(1);
			assertThatJson(parsedJson).field("['responseLimitExceeded']").isEqualTo(false);
	}

	@Test
	public void validate_searchBookings_NoResults_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("bookingReference","GAA8056793")
					.queryParam("bookerLastName","Pop")
					.queryParam("arrivalDate","3023-01-23")
					.get("/v1/reservations/search");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['bookings']").isEmpty();
			assertThatJson(parsedJson).field("['totalPages']").isEqualTo(0);
			assertThatJson(parsedJson).field("['offset']").isEqualTo(0);
			assertThatJson(parsedJson).field("['limit']").isEqualTo(20);
			assertThatJson(parsedJson).field("['hasMore']").isEqualTo(false);
			assertThatJson(parsedJson).field("['totalResults']").isEqualTo(0);
	}

	@Test
	public void validate_update_Business_Items_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"hotelId\":\"HOTELCODE\",\"reservationIds\":[\"123456\"],\"businessItems\":{\"businessNotes\":\"TestingNote is authorized\",\"purchaseOrderNumber\":\"1234521345\",\"customReferenceNumber\":\"ABC123456\",\"businessAllowances\":[{\"allowance\":\"ultimateWifi\",\"budget\":\"0.0\",\"isAuthorised\":true}]}}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/business");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
	}

	@Test
	public void validate_update_Discount_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"reservationIds\":[\"12345\",\"123457\"],\"discountAmount\":\"10\",\"currency\":\"EUR\",\"hotelId\":\"HOTELTEST\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/discount");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
	}

	@Test
	public void validate_update_Reservation_Preferences_Success_204Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"hotelId\":\"TestId\",\"reservationsIds\":[\"12345\"],\"preferencesCollections\":[{\"preferenceType\":\"EVENTS\",\"preferences\":[\"BDAY\"]}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/preferences");

		// then:
			assertThat(response.statusCode()).isEqualTo(204);
	}

	@Test
	public void validate_update_Reservation_Scheduled_Packages_Success_201Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"hotelId\":\"TestId\",\"reservations\":[{\"reservationsId\":\"12345\",\"addPackages\":[{\"id\":\"PIBTEST\",\"noOfSelections\":1,\"scheduledDates\":[\"2022-08-20\"]}],\"removePackages\":[{\"id\":\"PIBTEST\",\"noOfSelections\":1}]}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/ancillaries/scheduled");

		// then:
			assertThat(response.statusCode()).isEqualTo(201);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['basketReference']").isEqualTo("TST-16a014c5-d8a4-4418-8d15-2537660f08e9");
	}

	@Test
	public void validate_update_Reservation_Scheduled_Packages_Success_400Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"hotelId\":\"TestId\",\"reservations\":[{\"reservationsId\":\"12345\",\"addPackages\":[{\"id\":\"PIBTEST\",\"noOfSelections\":1,\"scheduledDates\":[\"2021-08-20\"]}],\"removePackages\":[{\"id\":\"PIBTEST\",\"noOfSelections\":1}]}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/ancillaries/scheduled");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");
	}

	@Test
	public void validate_update_reasonForStay_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"hotelId\":\"TestId\",\"reasonForStay\":\"LEI\",\"basketReference\":\"TestId1234567\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/reasonForStay");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['basketReference']").isEqualTo("TestId1234567");
	}

	@Test
	public void validate_update_reservationOverrideReasons_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"basketReference\":\"TestId1234567\",\"hotelId\":\"HOTELTEST\",\"reasonCode\":\"ILL\",\"reasonName\":\"Illness\",\"callerName\":\"John Doe\",\"managerName\":\"James Bond\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/v1/reservations/overrideReasons");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['basketReference']").isEqualTo("TestId1234567");
	}

}
