package uk.co.whitbread.ocd;

import uk.co.whitbread.ocd.ContractVerifierBaseTest;
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
	public void validate_get_offer_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("arrivalDate","9999-10-01")
					.queryParam("departureDate","9999-10-02")
					.queryParam("roomType","DOUBLE")
					.queryParam("adults","1")
					.queryParam("ratePlanCode","FLEXRATE")
					.get("/v1/hotels/BERCIT/offer");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['roomType']").isEqualTo("DOUBLE");
			assertThatJson(parsedJson).field("['ratePlanCode']").isEqualTo("FLEXRATE");
			assertThatJson(parsedJson).field("['total']").field("['amountBeforeTax']").isEqualTo(613);
			assertThatJson(parsedJson).field("['total']").field("['amountAfterTax']").isEqualTo(916.25);
			assertThatJson(parsedJson).field("['total']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['description']").isEqualTo("Value Added Tax (VAT)");
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['code']").isEqualTo("36");
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['amount']").isEqualTo(61.3);
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['description']").isEqualTo("Occupancy tax");
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['code']").isEqualTo("10");
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['amount']").isEqualTo(30.65);
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['description']").isEqualTo("Resort fee");
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['code']").isEqualTo("12");
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['amount']").isEqualTo(150);
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['description']").isEqualTo("City tax");
			assertThatJson(parsedJson).field("['total']").field("['taxes']").array("['tax']").contains("['code']").isEqualTo("3");
			assertThatJson(parsedJson).field("['total']").field("['taxes']").field("['amount']").isEqualTo(303.25);
			assertThatJson(parsedJson).field("['total']").field("['taxes']").field("['currencyCode']").isEqualTo("GBP");
	}

}
