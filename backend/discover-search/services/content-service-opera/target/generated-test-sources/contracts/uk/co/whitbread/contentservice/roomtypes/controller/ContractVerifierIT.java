package uk.co.whitbread.contentservice.roomtypes.controller;

import uk.co.whitbread.contentservice.roomtypes.controller.ContractVerifierBaseTest;
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
	public void validate_retrieve_booking_notifications_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("country","gb")
					.queryParam("language","en")
					.queryParam("brand","pi")
					.queryParam("rate","a")
					.get("/content/bookinginfomessages");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['bookingNotifications']").contains("['bookingInfoMessage']").isEqualTo("You can amend or cancel your booking any time up to 1pm on the day you\u2019re due to arrive.");
	}

	@Test
	public void validate_retrieve_rates_en_q_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("language","en")
					.queryParam("brand","pi")
					.get("/content/rateclassifications/Q");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateClassification']").isEqualTo("Q");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateOrder']").isEqualTo("0");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateName']").isEqualTo("Semi-Flex");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateDescription']").isEqualTo("Pay now. Change arrival date. Cancel up to 3 days before arrival.");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateLongDescription']").isEqualTo("In general, if you repay the price of what you bought within the delay period you won\u2019t pay any interest. That\u2019s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won\u2019t charge you any interest if you clear your balance before your delay period is up \u2013 even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateNotes']").isEqualTo("Lorem ipsum dolor sit amet");
	}

	@Test
	public void validate_retrieve_rates_en_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("language","en")
					.queryParam("brand","pi")
					.get("/content/rateclassifications");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateClassification']").isEqualTo("Q");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateOrder']").isEqualTo("0");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateName']").isEqualTo("Semi-Flex");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateDescription']").isEqualTo("Pay now. Change arrival date. Cancel up to 3 days before arrival.");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateLongDescription']").isEqualTo("In general, if you repay the price of what you bought within the delay period you won\u2019t pay any interest. That\u2019s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won\u2019t charge you any interest if you clear your balance before your delay period is up \u2013 even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateNotes']").isEqualTo("Lorem ipsum dolor sit amet");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateClassification']").isEqualTo("F");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateOrder']").isEqualTo("1");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateName']").isEqualTo("Advance");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateDescription']").isEqualTo("Pay now. Change arrival date. Cancel up to 28 days before arrival.");
			assertThatJson(parsedJson).array("['rateClassifications']").contains("['rateNotes']").isEqualTo("Lorem ipsum dolor sit amet...");
	}

	@Test
	public void validate_retrieve_rates_failure() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("language","en")
					.queryParam("brand","somebrand")
					.get("/content/rateclassifications");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);

	}

	@Test
	public void validate_retrieve_rates_nodatafound() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("language","en")
					.queryParam("brand","pi")
					.get("/content/rateclassifications/ZZZ");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['rateClassifications']").isEmpty();
	}

	@Test
	public void validate_retrieve_rooms_en_gb_sb_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("language","en")
					.queryParam("country","gb")
					.queryParam("brand","pi")
					.get("/content/roomtypes/sb");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomTypeCode']").isEqualTo("SB");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomCategory']").isEqualTo("Standard");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomLabel']").isEqualTo("Standard Room");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomDescription']").isEqualTo("Enjoy everything that\u2019s included in a Standard room, plus a few little extras to enhance your stay.");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomInfoLabel']").isEqualTo("Room Information");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomInfo']").isEqualTo("This hotel is being refurbished, we're sorry for any inconvenience.");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['gridImage']").isNull();
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomImage']").isNull();
			assertThatJson(parsedJson).array("['roomTypes']").array("['facilities']").contains("['facilityTitle']").isEqualTo("Comfort");
			assertThatJson(parsedJson).array("['roomTypes']").array("['facilities']").contains("['facilityDescription']").isEqualTo("A kingsize Hypnos bed with a cosy duvet and choice of pillows, plus a handy desk and chair");
			assertThatJson(parsedJson).array("['roomTypes']").array("['facilities']").contains("['facilityTitle']").isEqualTo("Convenience");
			assertThatJson(parsedJson).array("['roomTypes']").array("['facilities']").contains("['facilityDescription']").isEqualTo("Tea & coffee making facilities and a power shower \u2013 plus a bath in most rooms");
	}

	@Test
	public void validate_retrieve_rooms_en_gb_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("language","en")
					.queryParam("country","gb")
					.queryParam("brand","pi")
					.get("/content/roomtypes");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomTypeCode']").isEqualTo("SB");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomCategory']").isEqualTo("Standard");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomLabel']").isEqualTo("Standard Room");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomDescription']").isEqualTo("Enjoy everything that\u2019s included in a Standard room, plus a few little extras to enhance your stay.");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomInfoLabel']").isEqualTo("Room Information");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomInfo']").isEqualTo("This hotel is being refurbished, we're sorry for any inconvenience.");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['gridImage']").isNull();
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomImage']").isNull();
			assertThatJson(parsedJson).array("['roomTypes']").array("['facilities']").contains("['facilityTitle']").isEqualTo("Comfort");
			assertThatJson(parsedJson).array("['roomTypes']").array("['facilities']").contains("['facilityDescription']").isEqualTo("A kingsize Hypnos bed with a cosy duvet and choice of pillows, plus a handy desk and chair");
			assertThatJson(parsedJson).array("['roomTypes']").array("['facilities']").contains("['facilityTitle']").isEqualTo("Convenience");
			assertThatJson(parsedJson).array("['roomTypes']").array("['facilities']").contains("['facilityDescription']").isEqualTo("Tea & coffee making facilities and a power shower \u2013 plus a bath in most rooms");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomTypeCode']").isEqualTo("PB");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomCategory']").isEqualTo("Business");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomLabel']").isEqualTo("Business room");
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomDescription']").isNull();
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomInfoLabel']").isNull();
			assertThatJson(parsedJson).array("['roomTypes']").contains("['roomInfo']").isNull();
			assertThatJson(parsedJson).array("['roomTypes']").array("['facilities']").contains("['facilityDescription']").isEqualTo("A double or kingsize* Hypnos bed with a cosy duvet and choice of pillows");
	}

	@Test
	public void validate_retrieve_rooms_failure() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("language","en")
					.queryParam("country","gb")
					.queryParam("brand","somebrand")
					.get("/content/roomtypes");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);

	}

	@Test
	public void validate_retrieve_rooms_nodatafound() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("language","en")
					.queryParam("country","gb")
					.queryParam("brand","pi")
					.get("/content/roomtypes/zz");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['roomTypes']").isEmpty();
	}

}
