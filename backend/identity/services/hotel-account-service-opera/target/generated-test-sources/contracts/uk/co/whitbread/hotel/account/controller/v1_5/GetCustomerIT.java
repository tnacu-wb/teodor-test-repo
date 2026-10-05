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
public class GetCustomerIT extends ContractVerifierBaseTest {

	@Test
	public void validate_getCustomerNoAuthorisation() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com?business=false");

		// then:
			assertThat(response.statusCode()).isEqualTo(401);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("7101");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("No authorization provided for customer request").value();
	}

	@Test
	public void validate_getCustomerWithExpiredToken() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer dummy_expired_value");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com?business=false");

		// then:
			assertThat(response.statusCode()).isEqualTo(401);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("035");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Provided token was invalid or expired").value();
	}

	@Test
	public void validate_getCustomerWithInvalidJwtToken() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer invalid_token");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com?business=false");

		// then:
			assertThat(response.statusCode()).isEqualTo(401);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("035");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Provided token was invalid or expired").value();
	}

	@Test
	public void validate_getCustomerWithKeyNotFoundInJWK() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer dummy_not_found_value");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com?business=false");

		// then:
			assertThat(response.statusCode()).isEqualTo(401);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("035");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Provided token was invalid or expired").value();
	}

	@Test
	public void validate_getCustomerWithMissingKeyInToken() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer dummy_missing_value");

		// when:
			ResponseOptions response = given().spec(request)

					.get("/customers/hotels/customer@test.com?business=false");

		// then:
			assertThat(response.statusCode()).isEqualTo(401);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");


		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("035");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Provided token was invalid or expired").value();
	}

}
