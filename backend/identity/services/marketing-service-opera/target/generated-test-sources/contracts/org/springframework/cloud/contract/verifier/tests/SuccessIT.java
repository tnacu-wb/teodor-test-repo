package org.springframework.cloud.contract.verifier.tests;

import uk.co.whitbread.marketing.controller.SuccessVerifierBaseTest;
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
public class SuccessIT extends SuccessVerifierBaseTest {

	@Test
	public void validate_getRegionsSuccesfull() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/marketing/hotels/regions");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['regions']").contains("['id']").isEqualTo("1");
			assertThatJson(parsedJson).array("['regions']").contains("['description']").isEqualTo("UK & Ireland");
			assertThatJson(parsedJson).array("['regions']").contains("['active']").isEqualTo(true);
			assertThatJson(parsedJson).array("['regions']").contains("['id']").isEqualTo("2");
			assertThatJson(parsedJson).array("['regions']").contains("['description']").isEqualTo("Dubai");
			assertThatJson(parsedJson).array("['regions']").contains("['id']").isEqualTo("3");
			assertThatJson(parsedJson).array("['regions']").contains("['description']").isEqualTo("India");
			assertThatJson(parsedJson).array("['regions']").contains("['id']").isEqualTo("4");
			assertThatJson(parsedJson).array("['regions']").contains("['description']").isEqualTo("hub by Premier Inn");
			assertThatJson(parsedJson).array("['regions']").contains("['id']").isEqualTo("5");
			assertThatJson(parsedJson).array("['regions']").contains("['description']").isEqualTo("Germany");
	}

	@Test
	public void validate_updateNewsletterPreferencesSuccessfull() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("authenticationKey", "auth-key-test")
					.body("{\"correlationId\":\"1984000\",\"subscriptions\":[{\"contactType\":\"Email\",\"contactValue\":\"test@gmail.com\",\"subscribe\":\"false\",\"brandCode\":\"PINN\"}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/marketing/hotels/newsletter/customer-id-test");

		// then:
			assertThat(response.statusCode()).isEqualTo(204);
	}

}
