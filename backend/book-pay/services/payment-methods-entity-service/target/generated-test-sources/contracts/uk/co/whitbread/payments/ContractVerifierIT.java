package uk.co.whitbread.payments;

import uk.co.whitbread.payments.ContractVerifierBaseTest;
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
	public void validate_get_paymentActions_rule1_noPayment_uk() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/payment-methods/payment-actions/BASKET_UK_NO_PAYMENT");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['displayPaymentPage']").isEqualTo(false);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['chargeType']").isEqualTo("NO_PAYMENT");
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['paymentActions']").contains("['order']").isEqualTo(1);
	}

	@Test
	public void validate_get_paymentActions_rule2_authorizeCard_de() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/payment-methods/payment-actions/BASKET_DE_AUTHORIZE");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['displayPaymentPage']").isEqualTo(true);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['chargeType']").isEqualTo("AUTHORIZE_CARD");
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['currency']").isEqualTo("EUR");
			assertThatJson(parsedJson).array("['paymentActions']").contains("['order']").isEqualTo(1);
	}

	@Test
	public void validate_get_paymentActions_rule3_creditCard_uk() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/payment-methods/payment-actions/BASKET_UK_CREDIT_CARD");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['displayPaymentPage']").isEqualTo(true);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['chargeType']").isEqualTo("CREDIT_CARD");
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['amount']").isEqualTo(80.0);
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['paymentActions']").contains("['order']").isEqualTo(1);
	}

	@Test
	public void validate_get_paymentActions_rule3_multiRoom_creditCard_uk() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/payment-methods/payment-actions/BASKET_UK_MULTI_ROOM_CREDIT_CARD");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['displayPaymentPage']").isEqualTo(true);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['chargeType']").isEqualTo("CREDIT_CARD");
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['amount']").isEqualTo(180.0);
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['paymentActions']").contains("['order']").isEqualTo(1);
	}

	@Test
	public void validate_get_paymentActions_rule4_combined_uk() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/payment-methods/payment-actions/BASKET_UK_COMBINED");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['displayPaymentPage']").isEqualTo(true);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['chargeType']").isEqualTo("CARD_ON_FILE");
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['amount']").isEqualTo(100.0);
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['paymentActions']").contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['chargeType']").isEqualTo("CREDIT_CARD");
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['amount']").isEqualTo(50.0);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['order']").isEqualTo(2);
	}

	@Test
	public void validate_get_paymentActions_rule4_multiRoom_combined_uk() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/payment-methods/payment-actions/BASKET_UK_MULTI_ROOM_COMBINED");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['displayPaymentPage']").isEqualTo(true);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['chargeType']").isEqualTo("CARD_ON_FILE");
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['amount']").isEqualTo(140.0);
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['paymentActions']").contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['chargeType']").isEqualTo("CREDIT_CARD");
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['amount']").isEqualTo(90.0);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['order']").isEqualTo(2);
	}

	@Test
	public void validate_get_paymentActions_rule5_cardOnFile_uk() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/payment-methods/payment-actions/BASKET_UK_CARD_ON_FILE");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['displayPaymentPage']").isEqualTo(false);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['chargeType']").isEqualTo("CARD_ON_FILE");
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['amount']").isEqualTo(60.0);
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['paymentActions']").contains("['order']").isEqualTo(1);
	}

	@Test
	public void validate_get_paymentActions_rule5_multiRoom_cardOnFile_uk() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/payment-methods/payment-actions/BASKET_UK_MULTI_ROOM_CARD_ON_FILE");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['displayPaymentPage']").isEqualTo(false);
			assertThatJson(parsedJson).array("['paymentActions']").contains("['chargeType']").isEqualTo("CARD_ON_FILE");
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['amount']").isEqualTo(120.0);
			assertThatJson(parsedJson).array("['paymentActions']").field("['price']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['paymentActions']").contains("['order']").isEqualTo(1);
	}

	@Test
	public void validate_get_paymentCcuiMethods_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("bookingChannel", "WEB");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("basketReference","LONHOL5778172")
					.queryParam("country","gb")
					.queryParam("language","en")
					.queryParam("userType","AGENT")
					.get("/v1/payment-methods/ccui");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("CARD");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("NEW_CARD");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("MC");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Mastercard Credit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Mastercard.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("AX");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("American Express");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/AX.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("DN");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Diners Club");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/DClub.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("VS");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Visa Debit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Visa_Debit.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Electron");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Electron_white_v.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("MA");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Maestro");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/maestro.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Mastercard Debit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/MD.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Visa Credit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/VC.jpg");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['type']").isEqualTo("PAY_NOW");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['type']").isEqualTo("PAY_ON_ARRIVAL");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['order']").isEqualTo(2);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['enabled']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).array().contains("['cnpPreSelected']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['cnpOptionAvailable']").isEqualTo(false);
			assertThatJson(parsedJson).array().array("['reasons']").isEmpty();
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("PIBA UK");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("NEW_PIBA");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(2);
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("PI");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Business Account");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Business_Account.jpg");
			assertThatJson(parsedJson).array().contains("['cnpOptionAvailable']").isEqualTo(true);
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("PIBA EU");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(3);
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("Account to company");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("ACCOUNT_COMPANY");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(4);
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("Non-guaranteed booking");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("RESERVE_WITHOUT_CARD");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(5);
	}

	@Test
	public void validate_get_paymentMethods_anonymousUser_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("bookingChannel", "WEB");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("basketReference","LONHOL5778172")
					.queryParam("country","gb")
					.queryParam("language","en")
					.get("/v1/payment-methods");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("CARD");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("NEW_CARD");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("MC");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Mastercard Credit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Mastercard.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("AX");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("American Express");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/AX.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("DN");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Diners Club");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/DClub.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("VS");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Visa Debit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Visa_Debit.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Electron");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Electron_white_v.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("MA");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Maestro");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/maestro.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Mastercard Debit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/MD.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Visa Credit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/VC.jpg");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['type']").isEqualTo("PAY_NOW");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['type']").isEqualTo("PAY_ON_ARRIVAL");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['order']").isEqualTo(2);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['enabled']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).array().contains("['cnpPreSelected']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['cnpOptionAvailable']").isEqualTo(false);
			assertThatJson(parsedJson).array().array("['reasons']").isEmpty();
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("PIBA");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("NEW_PIBA");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(2);
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("PI");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Business Account");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Business_Account.jpg");
			assertThatJson(parsedJson).array().contains("['cnpOptionAvailable']").isEqualTo(true);
	}

	@Test
	public void validate_get_paymentMethods_businessUserDE_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("WB-Authorization", "Bearer test==")
					.header("bookingChannel", "WEB");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("basketReference","MUNCIT5778172")
					.queryParam("country","de")
					.queryParam("language","de")
					.queryParam("userType","BUSINESS")
					.get("/v1/payment-methods");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("CARD");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("NEW_CARD");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("MC");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Mastercard Credit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Mastercard.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("AX");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("American Express");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/AX.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("VS");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Visa Debit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Visa_Debit.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Mastercard Debit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/MD.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Visa Credit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/VC.jpg");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['type']").isEqualTo("PAY_NOW");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['type']").isEqualTo("PAY_ON_ARRIVAL");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['order']").isEqualTo(2);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['type']").isEqualTo("RESERVE_WITHOUT_CARD");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['order']").isEqualTo(3);
			assertThatJson(parsedJson).array().contains("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).array().contains("['cnpPreSelected']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['cnpOptionAvailable']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("PIBA");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("NEW_PIBA");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(2);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['enabled']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['enabled']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['cnpOptionAvailable']").isEqualTo(true);
			assertThatJson(parsedJson).array().array("['reasons']").arrayField().isEqualTo("PIBA_ALLOWED_ONLY_IN_UK").value();

		// and:
			assertThat((Object) parsedJson.read("$[0].reasons")).isInstanceOf(java.util.List.class);
			assertThat((java.lang.Iterable) parsedJson.read("$[0].reasons", java.util.Collection.class)).as("$[0].reasons").hasSizeGreaterThanOrEqualTo(0);
			assertThat((Object) parsedJson.read("$[1].acceptedCardTypes")).isInstanceOf(java.util.List.class);
			assertThat((java.lang.Iterable) parsedJson.read("$[1].acceptedCardTypes", java.util.Collection.class)).as("$[1].acceptedCardTypes").hasSizeGreaterThanOrEqualTo(0);
	}

	@Test
	public void validate_get_paymentMethods_businessUserWithSavedCardDE_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("WB-Authorization", "Bearer test==")
					.header("bookingChannel", "WEB");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("basketReference","MUNCIT5778172")
					.queryParam("country","de")
					.queryParam("language","de")
					.queryParam("userType","BUSINESS")
					.get("/v1/payment-methods");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("PIBA");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("SAVED_CARD");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array().field("['card']").field("['expiryMonth']").isEqualTo("05");
			assertThatJson(parsedJson).array().field("['card']").field("['expiryYear']").isEqualTo("30");
			assertThatJson(parsedJson).array().field("['card']").field("['type']").isEqualTo("AT");
			assertThatJson(parsedJson).array().field("['card']").field("['logoSrc']").isEqualTo("");
			assertThatJson(parsedJson).array().field("['card']").field("['cardHolderName']").isEqualTo("John Smith");
			assertThatJson(parsedJson).array().field("['card']").field("['cardType']").isEqualTo("BUSINESS_PERSONAL_STORED_CARD");
			assertThatJson(parsedJson).array().field("['card']").field("['cnpRequired']").isEqualTo(false);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['type']").isEqualTo("PAY_NOW");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['enabled']").isEqualTo(false);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['type']").isEqualTo("PAY_ON_ARRIVAL");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['order']").isEqualTo(2);
			assertThatJson(parsedJson).array().contains("['enabled']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['cnpPreSelected']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['cnpOptionAvailable']").isEqualTo(true);
			assertThatJson(parsedJson).array().array("['reasons']").arrayField().isEqualTo("CARD_NOT_ACCEPTED_AT_HOTEL").value();
			assertThatJson(parsedJson).array().array("['reasons']").arrayField().isEqualTo("PIBA_ALLOWED_ONLY_IN_UK").value();
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("CARD");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("NEW_CARD");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(2);
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("MC");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Mastercard Credit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Mastercard.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("AX");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("American Express");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/AX.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("VS");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Visa Debit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Visa_Debit.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Mastercard Debit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/MD.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Visa Credit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/VC.jpg");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).array().contains("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).array().contains("['cnpOptionAvailable']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("NEW_PIBA");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(3);

		// and:
			assertThat((Object) parsedJson.read("$[1].reasons")).isInstanceOf(java.util.List.class);
			assertThat((java.lang.Iterable) parsedJson.read("$[1].reasons", java.util.Collection.class)).as("$[1].reasons").hasSizeGreaterThanOrEqualTo(0);
			assertThat((Object) parsedJson.read("$[2].acceptedCardTypes")).isInstanceOf(java.util.List.class);
			assertThat((java.lang.Iterable) parsedJson.read("$[2].acceptedCardTypes", java.util.Collection.class)).as("$[2].acceptedCardTypes").hasSizeGreaterThanOrEqualTo(0);
	}

	@Test
	public void validate_get_paymentMethods_businessUser_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("WB-Authorization", "Bearer test==")
					.header("bookingChannel", "WEB");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("basketReference","LONHOL5778172")
					.queryParam("country","gb")
					.queryParam("language","en")
					.queryParam("userType","BUSINESS")
					.get("/v1/payment-methods");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("CARD");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("SAVED_CARD");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array().field("['card']").field("['expiryMonth']").isEqualTo("12");
			assertThatJson(parsedJson).array().field("['card']").field("['expiryYear']").isEqualTo("30");
			assertThatJson(parsedJson).array().field("['card']").field("['type']").isEqualTo("AC");
			assertThatJson(parsedJson).array().field("['card']").field("['logoSrc']").isEqualTo("/content/dam/global/booking/Mastercard.jpg");
			assertThatJson(parsedJson).array().field("['card']").field("['cardHolderName']").isEqualTo("John Smith");
			assertThatJson(parsedJson).array().field("['card']").field("['cardType']").isEqualTo("BUSINESS_PERSONAL_STORED_CARD");
			assertThatJson(parsedJson).array().field("['card']").field("['cnpRequired']").isEqualTo(false);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['type']").isEqualTo("PAY_NOW");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['order']").isEqualTo(1);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['type']").isEqualTo("PAY_ON_ARRIVAL");
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['order']").isEqualTo(2);
			assertThatJson(parsedJson).array().array("['paymentOptions']").contains("['enabled']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['enabled']").isEqualTo(true);
			assertThatJson(parsedJson).array().contains("['cnpPreSelected']").isEqualTo(false);
			assertThatJson(parsedJson).array().contains("['cnpOptionAvailable']").isEqualTo(false);
			assertThatJson(parsedJson).array().array("['reasons']").isEmpty();
			assertThatJson(parsedJson).array().contains("['name']").isEqualTo("PIBA");
			assertThatJson(parsedJson).array().contains("['subType']").isEqualTo("PIBAGB");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(2);
			assertThatJson(parsedJson).array().field("['card']").field("['expiryMonth']").isEqualTo("04");
			assertThatJson(parsedJson).array().field("['card']").field("['type']").isEqualTo("AT");
			assertThatJson(parsedJson).array().field("['card']").field("['logoSrc']").isEqualTo("/content/dam/global/booking/Business_Account.jpg");
			assertThatJson(parsedJson).array().field("['card']").field("['cardHolderName']").isEqualTo("PIBA Test CC");
			assertThatJson(parsedJson).array().field("['card']").field("['cardType']").isEqualTo("BUSINESS_CENTRALLY_STORED_CARD");
			assertThatJson(parsedJson).array().field("['card']").field("['cnpRequired']").isEqualTo(true);
			assertThatJson(parsedJson).array().contains("['cnpPreSelected']").isEqualTo(true);
			assertThatJson(parsedJson).array().field("['bookingAllowances']").field("['allowCarParking']").isEqualTo(true);
			assertThatJson(parsedJson).array().field("['bookingAllowances']").field("['allowIndividualCards']").isEqualTo(true);
			assertThatJson(parsedJson).array().field("['bookingAllowances']").field("['allowAlcohol']").isEqualTo(false);
			assertThatJson(parsedJson).array().field("['bookingAllowances']").field("['allowAdditionalCosts']").isEqualTo(false);
			assertThatJson(parsedJson).array().field("['bookingAllowances']").field("['allowPremierSaverRates']").isEqualTo(true);
			assertThatJson(parsedJson).array().field("['bookingAllowances']").field("['maxDinnerBudgets']").field("['ukWide']").field("['amount']").isEqualTo(25);
			assertThatJson(parsedJson).array().field("['bookingAllowances']").field("['maxDinnerBudgets']").field("['ukWide']").field("['currency']").isEqualTo("GBP");
			assertThatJson(parsedJson).array().field("['bookingAllowances']").field("['maxNumberOfNights']").isEqualTo(14);
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("NEW_CARD");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(3);
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("MC");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Mastercard Credit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Mastercard.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("AX");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("American Express");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/AX.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("DN");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Diners Club");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/DClub.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("VS");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Visa Debit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Visa_Debit.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Electron");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Electron_white_v.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("MA");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Maestro");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/maestro.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Mastercard Debit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/MD.jpg");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Visa Credit");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/VC.jpg");
			assertThatJson(parsedJson).array().contains("['type']").isEqualTo("NEW_PIBA");
			assertThatJson(parsedJson).array().contains("['order']").isEqualTo(4);
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['type']").isEqualTo("PI");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['name']").isEqualTo("Business Account");
			assertThatJson(parsedJson).array().array("['acceptedCardTypes']").contains("['logoSrc']").isEqualTo("/content/dam/global/booking/Business_Account.jpg");
			assertThatJson(parsedJson).array().contains("['cnpOptionAvailable']").isEqualTo(true);
	}

	@Test
	public void validate_post_paymentMethods_anonymous_invalidPaymentMethod() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"basketReference\":\"LONHOL5778172\",\"type\":\"CARD\",\"selectedPaymentOption\":\"RESERVE_WITHOUT_CARD\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/payment-methods");

		// then:
			assertThat(response.statusCode()).isEqualTo(409);
	}

	@Test
	public void validate_post_paymentMethods_anonymous_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"basketReference\":\"LONHOL5778172\",\"type\":\"CARD\",\"selectedPaymentOption\":\"PAY_ON_ARRIVAL\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/payment-methods");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
	}

}
