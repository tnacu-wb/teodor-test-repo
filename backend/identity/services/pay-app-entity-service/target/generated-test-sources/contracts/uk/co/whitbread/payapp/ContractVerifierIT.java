package uk.co.whitbread.payapp;

import uk.co.whitbread.payapp.ContractVerifierBaseTest;
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
	public void validate_payapp_GetException() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"email\":\"john.doe@email.com\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/pay-app/application/initialize");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");
	}

	@Test
	public void validate_payapp_SubmitApplicationException() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"applicationGuid\":\"invalid-guid\",\"hostedPageGuid\":\"hosted-page-guid\",\"hotelBookingRole\":\"hotel-booking-role\",\"isDirectDebit\":false,\"termsAndConditionAccepted\":\"Y\",\"registrationQuestion\":\"registration-question\",\"registrationAnswer\":\"registration-answer\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/pay-app/application/submit");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");
	}

}
