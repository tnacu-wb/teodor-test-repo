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
public class AuthIT extends ContractVerifierBaseTest {

	@Test
	public void validate_postForgotPassword() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "jwt_session_id")
					.body("{\"username\":\"forgot_password_success_with_url\",\"url\":\"http://www.premierinn.com\"}");

		// when:
			ResponseOptions response = given().spec(request)

					.post("/auth/hotels/forgot-password");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");
	}

	@Test
	public void validate_postForgotPasswordUseDefaultUrl() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "jwt_session_id")
					.body("{\"username\":\"forgot_password_success_default_url\"}");

		// when:
			ResponseOptions response = given().spec(request)

					.post("/auth/hotels/forgot-password");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");
	}

}
