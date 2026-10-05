package uk.co.whitbread.company;

import uk.co.whitbread.company.ContractVerifierBaseTest;
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
	public void validate_get_getCompaniesProfile_exception() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("hotelId","MANOLD")
					.queryParam("limit","30")
					.get("/v1/companies/profile");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");
	}

	@Test
	public void validate_get_getCompaniesProfile_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("hotelId","MANOLD")
					.queryParam("arNumber","167003")
					.queryParam("companyName","AMEROPA-REISEN G")
					.queryParam("limit","50")
					.get("/v1/companies/profile");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['companies']").contains("['name']").isEqualTo("AMEROPA-REISEN GmbH");
			assertThatJson(parsedJson).array("['companies']").contains("['telephoneNumber']").isEqualTo("+1 415 555 0101");
			assertThatJson(parsedJson).array("['companies']").contains("['profileType']").isEqualTo("Company");
			assertThatJson(parsedJson).array("['companies']").contains("['corpId']").isEqualTo("AB-63");
			assertThatJson(parsedJson).array("['companies']").contains("['companyId']").isEqualTo("43215");
			assertThatJson(parsedJson).array("['companies']").contains("['language']").isEqualTo("en");
			assertThatJson(parsedJson).array("['companies']").contains("['arNumber']").isEqualTo("167003");
			assertThatJson(parsedJson).array("['companies']").contains("['active']").isEqualTo(true);
			assertThatJson(parsedJson).array("['companies']").contains("['negotiatedRateEnabled']").isEqualTo(false);
			assertThatJson(parsedJson).array("['companies']").contains("['restricted']").isEqualTo(true);
			assertThatJson(parsedJson).array("['companies']").contains("['restrictedReason']").isEqualTo("A2CNACTV");
			assertThatJson(parsedJson).array("['companies']").field("['address']").field("['addressLine1']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).array("['companies']").field("['address']").field("['addressLine2']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).array("['companies']").field("['address']").field("['addressLine3']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).array("['companies']").field("['address']").field("['addressLine4']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).array("['companies']").field("['address']").field("['city']").isEqualTo("Bad Homburg");
			assertThatJson(parsedJson).array("['companies']").field("['address']").field("['country']").isEqualTo("Romania");
			assertThatJson(parsedJson).array("['companies']").field("['address']").field("['postalCode']").isEqualTo("61352");
			assertThatJson(parsedJson).field("['totalResults']").isEqualTo(1);
			assertThatJson(parsedJson).field("['hasMore']").isEqualTo(false);
			assertThatJson(parsedJson).field("['limit']").isEqualTo(1);
			assertThatJson(parsedJson).field("['offset']").isEqualTo(0);
			assertThatJson(parsedJson).field("['page']").isEqualTo(0);
			assertThatJson(parsedJson).field("['pageSize']").isEqualTo(0);
	}

	@Test
	public void validate_get_getCompanyProfileWithExcludeNegotiated_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("excludeNegotiatedRates","true")
					.get("/v1/companies/123455");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['name']").isEqualTo("AMEROPA-REISEN GmbH");
			assertThatJson(parsedJson).field("['telephoneNumber']").isEqualTo("+1 415 555 0101");
			assertThatJson(parsedJson).field("['profileType']").isEqualTo("Company");
			assertThatJson(parsedJson).field("['corpId']").isEqualTo("AB-63");
			assertThatJson(parsedJson).field("['companyId']").isEqualTo("123455");
			assertThatJson(parsedJson).field("['language']").isEqualTo("en");
			assertThatJson(parsedJson).field("['arNumber']").isEqualTo("167003");
			assertThatJson(parsedJson).field("['active']").isEqualTo(true);
			assertThatJson(parsedJson).field("['negotiatedRateEnabled']").isEqualTo(false);
			assertThatJson(parsedJson).field("['restricted']").isEqualTo(true);
			assertThatJson(parsedJson).field("['restrictedReason']").isEqualTo("A2CNACTV");
			assertThatJson(parsedJson).field("['address']").field("['addressLine1']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).field("['address']").field("['addressLine2']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).field("['address']").field("['addressLine3']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).field("['address']").field("['addressLine4']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).field("['address']").field("['city']").isEqualTo("Bad Homburg");
			assertThatJson(parsedJson).field("['address']").field("['country']").isEqualTo("Romania");
			assertThatJson(parsedJson).field("['address']").field("['postalCode']").isEqualTo("61352");
	}

	@Test
	public void validate_get_getCompanyProfile_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json;charset=UTF-8");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/companies/123455");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['name']").isEqualTo("AMEROPA-REISEN GmbH");
			assertThatJson(parsedJson).field("['telephoneNumber']").isEqualTo("+1 415 555 0101");
			assertThatJson(parsedJson).field("['profileType']").isEqualTo("Company");
			assertThatJson(parsedJson).field("['corpId']").isEqualTo("AB-63");
			assertThatJson(parsedJson).field("['companyId']").isEqualTo("123455");
			assertThatJson(parsedJson).field("['language']").isEqualTo("en");
			assertThatJson(parsedJson).field("['arNumber']").isEqualTo("167003");
			assertThatJson(parsedJson).field("['active']").isEqualTo(true);
			assertThatJson(parsedJson).field("['negotiatedRateEnabled']").isEqualTo(true);
			assertThatJson(parsedJson).field("['restricted']").isEqualTo(true);
			assertThatJson(parsedJson).field("['restrictedReason']").isEqualTo("A2CNACTV");
			assertThatJson(parsedJson).field("['address']").field("['addressLine1']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).field("['address']").field("['addressLine2']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).field("['address']").field("['addressLine3']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).field("['address']").field("['addressLine4']").isEqualTo("Siemensstra\u00DFe 27");
			assertThatJson(parsedJson).field("['address']").field("['city']").isEqualTo("Bad Homburg");
			assertThatJson(parsedJson).field("['address']").field("['country']").isEqualTo("Romania");
			assertThatJson(parsedJson).field("['address']").field("['postalCode']").isEqualTo("61352");
	}

}
