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
public class PermissionManagementApiIT extends BadRequestPermissionsBaseIT {

	@Test
	public void validate_confirmDoubleOptIn400() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"customerId\":\"122434\",\"contactType\":\"email\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/marketing/newsletter/channel/HNL39288_28a5d984-7782-4e93-99a5-b2e9f8a746f3/confirm");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("brandCodes must not be null").value();
	}

	@Test
	public void validate_getPreferencesValidationErrorEmptyBrandCodes() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authentication", "Bearer dummyToken");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/marketing/newsletter/phone/rachel.cody@housemark.co.uk?brandCodes=");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("At least one brand code must be present").value();
	}

	@Test
	public void validate_getPreferencesValidationErrorInvalidEmail() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authentication", "Bearer dummyToken");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/marketing/newsletter/email/rachel.cody@housemark.c?brandCodes=PINN,WINN");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Invalid email").value();
	}

	@Test
	public void validate_getPreferencesValidationErrorNullBrandCodes() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authentication", "Bearer dummyToken");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/marketing/newsletter/phone/rachel.cody@housemark.co.uk");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("brandCodes must not be null").value();
	}

	@Test
	public void validate_unsubscribe400() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"customerId\":\"122434\",\"contactType\":\"email\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.delete("/marketing/newsletter/channel/HNL39288_28a5d984-7782-4e93-99a5-b2e9f8a746f3/unsubscribe");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("brandCodes must not be null").value();
	}

	@Test
	public void validate_updatePreferencesValidationErrorEmptyBrandCodes() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authentication", "Bearer dummyToken")
					.body("{\"brandCodes\":[\"\"],\"optIn\":false,\"secondPartyOptIn\":true,\"thirdPartyVendorsOnpIn\":true,\"customer\":{\"countryOfResidence\":\"DE\",\"title\":\"Mr\",\"firstName\":\"liam\",\"lastName\":\"wilson\",\"nationality\":\"GB\",\"language\":\"en\",\"userId\":\"liam.wilson.test1\"}}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/marketing/newsletter/phone/rachel.cody@housemark.co.uk");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Invalid Brand code.Ensure brand codes supplied are valid").value();
	}

	@Test
	public void validate_updatePreferencesValidationErrorInvalidEmail() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authentication", "Bearer dummyToken")
					.body("{\"brandCodes\":[\"PINN\"],\"optIn\":false,\"secondPartyOptIn\":true,\"thirdPartyVendorsOptIn\":true,\"doubleOptIn\":false,\"customer\":{\"countryOfResidence\":\"DE\",\"title\":\"Mr\",\"firstName\":\"liam\",\"lastName\":\"wilson\",\"nationality\":\"GB\",\"language\":\"en\",\"userId\":\"liam.wilson.test1\"}}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/marketing/newsletter/email/rachel.cody@housemark.c");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Invalid email").value();
	}

	@Test
	public void validate_updatePreferencesValidationErrorNullBrandCodes() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authentication", "Bearer dummyToken")
					.body("{\"brandCodes\":null,\"optIn\":false,\"secondPartyOptIn\":true,\"thirdPartyVendorsOptIn\":true,\"doubleOptIn\":false,\"customer\":{\"countryOfResidence\":\"DE\",\"title\":\"Mr\",\"firstName\":\"liam\",\"lastName\":\"wilson\",\"nationality\":\"GB\",\"language\":\"en\",\"userId\":\"liam.wilson.test1\"}}");

		// when:
			ResponseOptions response = given().spec(request)
					.put("/marketing/newsletter/phone/rachel.cody@housemark.co.uk");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("brandCodes must not be null").value();
	}

}
