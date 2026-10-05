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
public class CustomerHubIT extends PermissionsInvalidOauthTokenBaseIT {

	@Test
	public void validate_editNewsletterPreferences401() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"userId\":\"name.surname\",\"subscriptionData\":[{\"contactChannelType\":\"Email\",\"contactChannelValue\":\"unauthorized@gmail.com\",\"contactChannelPermission\":\"false\",\"brandCodes\":[\"PINN\",\"WINN\"],\"contentPermission\":{\"secondParty\":true,\"thirdParty\":true}}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/marketing/hotels/newsletter/edit");

		// then:
			assertThat(response.statusCode()).isEqualTo(401);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("3001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("You do not have permission to view this directory or page.").value();
	}

	@Test
	public void validate_getNewsletterPreferences401() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"requestId\":\"fsytdfxusdgcilugasld\",\"contactChannel\":{\"contactChannelType\":\"Email\",\"contactChannelValue\":\"unauthorized@gmail.com\"},\"brandCodes\":[\"PINN\",\"WINN\"]}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/marketing/hotels/newsletter/get");

		// then:
			assertThat(response.statusCode()).isEqualTo(401);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("3001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("You do not have permission to view this directory or page.").value();
	}

}
