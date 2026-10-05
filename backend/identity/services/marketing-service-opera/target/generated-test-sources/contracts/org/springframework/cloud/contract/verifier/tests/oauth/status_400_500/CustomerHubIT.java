package org.springframework.cloud.contract.verifier.tests.oauth.status_400_500;

import uk.co.whitbread.marketing.controller.BadRequestPermissionsBaseIT;
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
public class CustomerHubIT extends BadRequestPermissionsBaseIT {

	@Test
	public void validate_editNewsletterPreferences400() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"userId\":\"name.surname\",\"subscriptionData\":[{\"contactChannelType\":\"Telephone\",\"contactChannelValue\":\"+447777777777\",\"contactChannelSubType\":\"Mobile\",\"contactChannelPermission\":\"false\",\"brandCodes\":[\"PINN\",\"WINN\"],\"contentPermission\":{\"secondParty\":true,\"thirdParty\":true}}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/marketing/hotels/newsletter/edit");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("3001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Required properties not found or invalid. Please ensure request contains all required properties. Ensure brand codes supplied are valid.").value();
	}

	@Test
	public void validate_editNewsletterPreferences500() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"userId\":\"name.surname\",\"subscriptionData\":[{\"contactChannelType\":\"Email\",\"contactChannelValue\":\"email500@housemark.co.uk\",\"contactChannelPermission\":\"false\",\"brandCodes\":[\"PINN\",\"WINN\"],\"contentPermission\":{\"secondParty\":true,\"thirdParty\":true}}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/marketing/hotels/newsletter/edit");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("3001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Unknown error.").value();
	}

	@Test
	public void validate_getNewsletterPreferences400() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"requestId\":\"fsytdfxusdgcilugasld\",\"contactChannel\":{\"contactChannelType\":\"Telephone\",\"contactChannelValue\":\"+447777777777\"},\"brandCodes\":[\"PINN\",\"WINN\"]}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/marketing/hotels/newsletter/get");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("3001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Required properties not found or invalid. Please ensure request contains all required properties. Ensure brand codes supplied are valid.").value();
	}

	@Test
	public void validate_getNewsletterPreferences500() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"requestId\":\"fsytdfxusdgcilugasld\",\"contactChannel\":{\"contactChannelType\":\"Email\",\"contactChannelValue\":\"email500@housemark.co.uk\"},\"brandCodes\":[\"PINN\",\"WINN\"]}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/marketing/hotels/newsletter/get");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("3001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Unknown error.").value();
	}

}
