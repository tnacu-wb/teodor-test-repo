package uk.co.whitbread.feedback.controller;

import uk.co.whitbread.feedback.controller.ContractVerifierBaseTest;
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
public class ContractVerifierTest extends ContractVerifierBaseTest {

	@Test
	public void validate_sendingFeedbackReturningError() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Accept", "application/json")
					.body("{\"whb_wfsource\":\"https://www.whitbreadinns.co.uk\",\"title\":\"Failing Feedback Form\",\"whb_contacttype\":130570009,\"whb_reasonforcontact\":2,\"whb_wfbookingrefifapplicable\":\"AB12345\",\"whb_wfcontactnumber\":\"+447724547605\",\"whb_wfdateofstayifapplicable\":\"2018-02-14\",\"whb_wfemailaddress\":\"dimitris.damilos@whitbread.com\",\"whb_summary\":\"This was a weird hotel\",\"whb_wffeedbacktype\":130570000,\"whb_wffirstname\":\"Walter\",\"whb_wfhotelname\":\"ABEMAI\",\"whb_wfididntbookthroughpremierinncom\":false,\"whb_wflastname\":\"White\",\"whb_wfpostcode\":\"AB1 2CD\",\"whb_wfreasonforfeedback\":\"Restaurant/Bar experience\",\"whb_wftypeofvisit\":\"Dinner\",\"whb_loyaltycardnumber\":\"my-loyalty-card-num\",\"whb_wfchecknumber\":\"check-number\",\"whb_wfsleep\":false,\"whb_wfreported\":true}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/feedback");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");
	}

	@Test
	public void validate_sendingFeedbackSuccessfull() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Accept", "application/json")
					.body("{\"whb_wfsource\":\"https://www.whitbreadinns.co.uk\",\"title\":\"Feedback Form\",\"whb_contacttype\":130570009,\"whb_reasonforcontact\":2,\"whb_wfbookingrefifapplicable\":\"AB12345\",\"whb_wfcontactnumber\":\"+447724547605\",\"whb_wfdateofstayifapplicable\":\"2018-02-14\",\"whb_wfemailaddress\":\"dimitris.damilos@whitbread.com\",\"whb_summary\":\"This was a weird hotel\",\"whb_wffeedbacktype\":130570000,\"whb_wffirstname\":\"Walter\",\"whb_wfhotelname\":\"ABEMAI\",\"whb_wfididntbookthroughpremierinncom\":false,\"whb_wflastname\":\"White\",\"whb_wfpostcode\":\"AB1 2CD\",\"whb_wfreasonforfeedback\":\"Restaurant/Bar experience\",\"whb_wftypeofvisit\":\"Dinner\",\"whb_loyaltycardnumber\":\"my-loyalty-card-num\",\"whb_wfchecknumber\":\"check-number\",\"whb_wfsleep\":false,\"whb_wfreported\":true}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/feedback");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['caseReferenceId']").isEqualTo("SUCCESS-TICKET-ID");
	}

	@Test
	public void validate_sendingFeedbackSuccessfullWithDefaultSource() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Accept", "application/json")
					.body("{\"title\":\"Feedback Form\",\"whb_contacttype\":130570009,\"whb_reasonforcontact\":2,\"whb_wfbookingrefifapplicable\":\"AB12345\",\"whb_wfcontactnumber\":\"+447724547605\",\"whb_wfdateofstayifapplicable\":\"2018-02-14\",\"whb_wfemailaddress\":\"dimitris.damilos@whitbread.com\",\"whb_summary\":\"This was a weird hotel\",\"whb_wffeedbacktype\":130570000,\"whb_wffirstname\":\"Walter\",\"whb_wfhotelname\":\"ABEMAI\",\"whb_wfididntbookthroughpremierinncom\":false,\"whb_wflastname\":\"White\",\"whb_wfpostcode\":\"AB1 2CD\",\"whb_wfreasonforfeedback\":\"Restaurant/Bar experience\",\"whb_wftypeofvisit\":\"Dinner\",\"whb_loyaltycardnumber\":\"my-loyalty-card-num\",\"whb_wfchecknumber\":\"check-number\",\"whb_wfsleep\":false,\"whb_wfreported\":true}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/feedback");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['caseReferenceId']").isEqualTo("SUCCESS-TICKET-ID");
	}

	@Test
	public void validate_validationError() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Accept", "application/json")
					.body("{}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/feedback");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("whb_wffeedbacktype must not be null").value();
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("whb_reasonforcontact must not be null").value();
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("title must not be blank").value();
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("whb_wflastname must not be blank").value();
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("whb_wfemailaddress must not be blank").value();
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("whb_summary must not be blank").value();
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("whb_contacttype must not be null").value();
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("whb_wffirstname must not be blank").value();
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("whb_wfididntbookthroughpremierinncom must not be null").value();
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("whb_wfreasonforfeedback must not be blank").value();
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("whb_wfpostcode must not be blank").value();
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("whb_wfcontactnumber must not be blank").value();
	}

}
