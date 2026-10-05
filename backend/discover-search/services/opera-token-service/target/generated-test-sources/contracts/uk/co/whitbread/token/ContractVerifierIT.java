package uk.co.whitbread.token;

import uk.co.whitbread.token.ContractVerifierBaseTest;
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
	public void validate_get_opera_token_500Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/tokens/invalid-provider/access-token");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo("974");
			assertThatJson(parsedJson).field("['debugMessage']").isEqualTo("Unable to acquire access token for invalid-provider");
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("internal.server.exception");
	}

	@Test
	public void validate_get_opera_token_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/tokens/ohip/access-token");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['accessToken']").isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.uXrIhrveuvbR4tD1ULholQObboLVC-wIfJOEVElEzcs");
			assertThatJson(parsedJson).field("['tokenType']").isEqualTo("Bearer");
			assertThatJson(parsedJson).field("['expiresIn']").isEqualTo(3600);
			assertThatJson(parsedJson).field("['issuedAt']").isEqualTo("2025-08-06T11:32:00Z");
	}

}
