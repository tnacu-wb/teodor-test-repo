package org.springframework.cloud.contract.verifier.tests.oauth.status_200;

import uk.co.whitbread.marketing.controller.PermissionsValidOauthTokenBaseIT;
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
public class PermissionManagementApiIT extends PermissionsValidOauthTokenBaseIT {

	@Test
	public void validate_confirmDoubleOptIn() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"brandCodes\":[\"PINN\"],\"customerId\":\"122434\",\"contactType\":\"email\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/marketing/newsletter/channel/CHNL39288_28a5d984-7782-4e93-99a5-b2e9f8a746f3/confirm");

		// then:
			assertThat(response.statusCode()).isEqualTo(204);
	}

	@Test
	public void validate_editPreferencesSuccessfull() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authentication", "Bearer dummyToken")
					.body("{\"brandCodes\":[\"PINN\"],\"optIn\":false,\"secondPartyOptIn\":true,\"thirdPartyVendorsOptIn\":true,\"doubleOptIn\":false,\"customer\":{\"countryOfResidence\":\"DE\",\"title\":\"Mr\",\"firstName\":\"liam\",\"lastName\":\"wilson\",\"nationality\":\"GB\",\"language\":\"de\",\"userId\":\"liam.wilson.test1\"}}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/marketing/newsletter/email/email_success@gmail.com");

		// then:
			assertThat(response.statusCode()).isEqualTo(204);
	}

	@Test
	public void validate_getPreferencesSuccessfullWithContactChannelId() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/marketing/newsletter/email/channel/CHNL39288_28a5d984-7782-4e93-99a5-b2e9f8a746f3?brandCodes=PINN");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['permissions']").contains("['brandCode']").isEqualTo("PINN");
			assertThatJson(parsedJson).array("['permissions']").contains("['brand']").isEqualTo("Premier Inn");
			assertThatJson(parsedJson).array("['permissions']").contains("['optIn']").isEqualTo(true);
			assertThatJson(parsedJson).array("['permissions']").contains("['2ndOptInReq']").isEqualTo(false);
			assertThatJson(parsedJson).array("['permissions']").contains("['2ndOptIn']").isEqualTo(false);
			assertThatJson(parsedJson).array("['permissions']").contains("['secondPartyOptIn']").isEqualTo(true);
			assertThatJson(parsedJson).array("['permissions']").contains("['thirdPartyVendorsOptIn']").isEqualTo(true);
			assertThatJson(parsedJson).field("['valid']").isEqualTo(true);
			assertThatJson(parsedJson).array("['loyaltyAccounts']").contains("['loyaltyBrand']").isEqualTo("Brewers Fayre");
			assertThatJson(parsedJson).array("['loyaltyAccounts']").contains("['loyaltySystemId']").isEqualTo("987293");
			assertThatJson(parsedJson).field("['deleted']").isEqualTo(false);
	}

	@Test
	public void validate_unsubscribe() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"brandCodes\":[\"PINN\"],\"customerId\":\"122434\",\"contactType\":\"email\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.delete("/marketing/newsletter/channel/CHNL40088_28a5d984-7782-4e93-99a5-b2e9f8a746f3/unsubscribe");

		// then:
			assertThat(response.statusCode()).isEqualTo(204);
	}

}
