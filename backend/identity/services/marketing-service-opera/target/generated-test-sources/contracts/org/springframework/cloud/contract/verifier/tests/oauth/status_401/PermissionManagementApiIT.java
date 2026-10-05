package org.springframework.cloud.contract.verifier.tests.oauth.status_401;

import uk.co.whitbread.marketing.controller.PermissionsInvalidOauthTokenBaseIT;
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
public class PermissionManagementApiIT extends PermissionsInvalidOauthTokenBaseIT {

	@Test
	public void validate_confirmDoubleOptIn401() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"brandCodes\":[\"PINN\"],\"customerId\":\"122434\",\"contactType\":\"email\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/marketing/newsletter/channel/CHNL401_28a5d984-7782-4e93-99a5-b2e9f8a746f3/confirm");

		// then:
			assertThat(response.statusCode()).isEqualTo(401);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("3001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("You do not have permission to view this directory or page.").value();
	}

	@Test
	public void validate_unsubscribe401() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"brandCodes\":[\"PINN\"],\"customerId\":\"122434\",\"contactType\":\"email\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.delete("/marketing/newsletter/channel/CHNL401_28a5d984-7782-4e93-99a5-b2e9f8a746f3/unsubscribe");

		// then:
			assertThat(response.statusCode()).isEqualTo(401);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("3001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("You do not have permission to view this directory or page.").value();
	}

}
