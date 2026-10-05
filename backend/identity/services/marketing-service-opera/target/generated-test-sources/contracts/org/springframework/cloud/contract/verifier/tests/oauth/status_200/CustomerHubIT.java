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
public class CustomerHubIT extends PermissionsValidOauthTokenBaseIT {

	@Test
	public void validate_editNewsletterPreferencesSuccessfull() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"userId\":\"name.surname\",\"subscriptionData\":[{\"contactChannelType\":\"Email\",\"contactChannelValue\":\"email_success@gmail.com\",\"contactChannelPermission\":\"false\",\"brandCodes\":[\"PINN\",\"WINN\"],\"contentPermission\":{\"secondParty\":true,\"thirdParty\":true}}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/marketing/hotels/newsletter/edit");

		// then:
			assertThat(response.statusCode()).isEqualTo(204);
	}

	@Test
	public void validate_getNewsletterPreferencesSuccessfull() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"requestId\":\"fsytdfxusdgcilugasld\",\"contactChannel\":{\"contactChannelType\":\"Email\",\"contactChannelValue\":\"test@gmail.com\"},\"brandCodes\":[\"PINN\",\"WINN\"]}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/marketing/hotels/newsletter/get");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['contactChannelId']").isEqualTo("CHNL12047_bjghsdu_dahbj_dsjhbj");
			assertThatJson(parsedJson).field("['contactChannelValue']").isEqualTo("email@email.com");
			assertThatJson(parsedJson).array("['brandPermissions']").contains("['brandCode']").isEqualTo("PINN");
			assertThatJson(parsedJson).array("['brandPermissions']").contains("['brand']").isEqualTo("Premier Inn");
			assertThatJson(parsedJson).array("['brandPermissions']").contains("['optIn']").isEqualTo(true);
			assertThatJson(parsedJson).array("['brandPermissions']").contains("['2ndOptInReq']").isEqualTo(false);
			assertThatJson(parsedJson).array("['brandPermissions']").contains("['2ndOptIn']").isEqualTo(false);
			assertThatJson(parsedJson).array("['brandPermissions']").field("['contentPermission']").field("['2ndParty']").isEqualTo(true);
			assertThatJson(parsedJson).array("['brandPermissions']").field("['contentPermission']").field("['3rdParty']").isEqualTo(true);
			assertThatJson(parsedJson).array("['brandPermissions']").contains("['lastOptInDate']").isEqualTo("2019-11-20T18:08:15.323");
			assertThatJson(parsedJson).array("['brandPermissions']").contains("['lastSourceBusinessKey']").isEqualTo("ACE_EditPermissions_business_key");
			assertThatJson(parsedJson).array("['brandPermissions']").contains("['lastModifiedBy']").isEqualTo("name.surname");
			assertThatJson(parsedJson).field("['shared']").isEqualTo(false);
			assertThatJson(parsedJson).field("['valid']").isEqualTo(true);
			assertThatJson(parsedJson).array("['loyaltyAccounts']").contains("['loyaltyBrand']").isEqualTo("Brewers Fayre");
			assertThatJson(parsedJson).array("['loyaltyAccounts']").contains("['loyaltySystemId']").isEqualTo("987293");
			assertThatJson(parsedJson).field("['modified']").isEqualTo("2019-12-03T09:50:54.0633726Z");
			assertThatJson(parsedJson).field("['deleted']").isEqualTo(false);
	}

}
