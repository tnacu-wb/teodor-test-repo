package uk.co.whitbread.business.tether.controller;

import uk.co.whitbread.business.tether.controller.ContractVerifierBaseTest;
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
	public void validate_tetherByAccountSuccess() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("Authorization", "dummy_value")
					.body("{\"linkCode\":\"qyg-ujn-xes\",\"linkId\":3089503200100176,\"memorableWord\":\"zoo\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/business/tether");

		// then:
			assertThat(response.statusCode()).isEqualTo(201);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['guid']").isEqualTo("1206577c-30f6-463f-9eff-6bfd046b5cee");
	}

	@Test
	public void validate_tetherByAccountSuccessDELinkCode() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("Authorization", "dummy_value")
					.body("{\"linkCode\":\"qyg-ujn-xde\",\"linkId\":3089503200100176,\"memorableWord\":\"zoo\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/business/tether");

		// then:
			assertThat(response.statusCode()).isEqualTo(201);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['guid']").isEqualTo("1206577c-30f6-463f-9eff-6bfd046b5cee");
	}

	@Test
	public void validate_tetherByAccountSuccessGBLinkCode() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("Authorization", "dummy_value")
					.body("{\"linkCode\":\"qyg-ujn-xgb\",\"linkId\":3089503200100176,\"memorableWord\":\"zoo\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/business/tether");

		// then:
			assertThat(response.statusCode()).isEqualTo(201);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['guid']").isEqualTo("1206577c-30f6-463f-9eff-6bfd046b5cee");
	}

	@Test
	public void validate_tetherByAccountSuccessSaveInCdh() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("Authorization", "Bearer test")
					.body("{\"linkCode\":\"qyg-ujn-xes\",\"linkId\":3089503200100176,\"memorableWord\":\"zoo\",\"saveInCdh\":true}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/business/tether");

		// then:
			assertThat(response.statusCode()).isEqualTo(201);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['guid']").isEqualTo("1206577c-30f6-463f-9eff-6bfd046b5cee");
	}

	@Test
	public void validate_tetherByCardFail() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("Authorization", "Bearer dummy_value")
					.body("{\"linkCode\":\"mtr-36s-unk\",\"linkId\":3089500110017600027,\"memorableWord\":\"word\"}");

		// when:
			ResponseOptions response = given().spec(request)

					.post("/business/tether");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);



		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("ValidationError");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("TetherFailCardNotCorrectAccount").value();
	}

	@Test
	public void validate_tetherByCardSuccess() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("Authorization", "dummy_value")
					.body("{\"linkCode\":\"mtr-36s-unk\",\"linkId\":3089500110017600026,\"memorableWord\":\"word\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/business/tether");

		// then:
			assertThat(response.statusCode()).isEqualTo(201);

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['guid']").isEqualTo("1206577c-30f6-463f-9eff-6bfd046b5cde");
	}

	@Test
	public void validate_tetherLoginFail() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("Authorization", "Bearer dummy_value")
					.body("{\"guid\":\"2458b429-bf1f-495d-9453-cc41d8070244\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/business/tether/login");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
	}

	@Test
	public void validate_tetherLoginSuccess() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8")
					.header("Authorization", "Bearer dummy_value")
					.body("{\"guid\":\"cdac7548-467b-4fbd-85bb-2803a4d38288\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/business/tether/login");

		// then:
			assertThat(response.statusCode()).isEqualTo(201);
	}

}
