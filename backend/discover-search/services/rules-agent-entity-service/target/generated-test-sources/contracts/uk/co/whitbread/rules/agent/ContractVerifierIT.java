package uk.co.whitbread.rules.agent;

import uk.co.whitbread.rules.agent.ContractVerifierBaseTest;
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
	public void validate_getMultiOccupancySupplementPricing_dictionary_success_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"hotelIds\":[{\"hotelId\":\"FRAMTI\"}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/rules/multi-occupancy-supplement");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['dictionary']").field("['FRAMTI']").isEqualTo(9.99);
	}

	@Test
	public void validate_getMultiOccupancySupplementPricing_failure_422() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/rules/multi-occupancy-supplement");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
	}

	@Test
	public void validate_getMultiOccupancySupplementPricing_list_success_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"hotelIds\":[{\"hotelId\":\"FRAMTI\"}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("dictionary","false")
					.post("/v1/rules/multi-occupancy-supplement");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['list']").contains("['hotelId']").isEqualTo("FRAMTI");
			assertThatJson(parsedJson).array("['list']").contains("['pricing']").isEqualTo(9.99);
	}

	@Test
	public void validate_getMultiOccupancySupplementPricing_success_200_NOT_FOUND_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.body("{\"hotelIds\":[{\"hotelId\":\"MANOLD\"}]}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/v1/rules/multi-occupancy-supplement");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['dictionary']").field("['MANOLD']").isEqualTo(0.0);
	}

	@Test
	public void validate_getOccupancySupplementPricing_failure_422() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/rules/occupancy-supplement");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
	}

	@Test
	public void validate_getOccupancySupplementPricing_success_200_NOT_FOUND_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("hotelId","MANOLD")
					.get("/v1/rules/occupancy-supplement");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['hotelId']").isEqualTo("MANOLD");
			assertThatJson(parsedJson).field("['pricing']").isEqualTo(0.0);
	}

	@Test
	public void validate_getOccupancySupplementPricing_success_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("hotelId","FRAMTI")
					.get("/v1/rules/occupancy-supplement");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['hotelId']").isEqualTo("FRAMTI");
			assertThatJson(parsedJson).field("['pricing']").isEqualTo(9.99);
	}

	@Test
	public void validate_get_AmendmentRule_emptyInput_422_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("rateType","")
					.queryParam("arrivalDate","20220401")
					.queryParam("hotelLocalDateTime","20200208T080910")
					.queryParam("hotelCountryCode","GB")
					.get("/v1/rules/amendments");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo(401);
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("validation.error.form");
			assertThatJson(parsedJson).array("['details']").contains("['elementId']").isEqualTo("rateType");
			assertThatJson(parsedJson).array("['details']").contains("['errTextTemplate']").isEqualTo("must not be blank");
	}

	@Test
	public void validate_get_AmendmentRule_succes_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("rateType","Flex")
					.queryParam("arrivalDate","20220401")
					.queryParam("hotelLocalDateTime","20220208T080910")
					.queryParam("hotelCountryCode","GB")
					.get("/v1/rules/amendments");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['isAmendable']").isEqualTo(true);
			assertThatJson(parsedJson).field("['requestDetails']").field("['rateType']").isEqualTo("Flex");
			assertThatJson(parsedJson).field("['requestDetails']").field("['arrivalDate']").isEqualTo("20220401");
			assertThatJson(parsedJson).field("['requestDetails']").field("['hotelLocalDateTime']").isEqualTo("20220208T080910");
			assertThatJson(parsedJson).field("['requestDetails']").field("['hotelCountryCode']").isEqualTo("GB");
	}

	@Test
	public void validate_get_BusinessAllowanceRules_success_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/rules/allowances");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['businessAllowances']").contains("['pms']").isEqualTo("OP");
			assertThatJson(parsedJson).array("['businessAllowances']").contains("['sourceId']").isEqualTo("alcohol");
			assertThatJson(parsedJson).array("['businessAllowances']").contains("['sourceType']").isEqualTo("ALLOWANCE");
			assertThatJson(parsedJson).array("['businessAllowances']").contains("['targetId']").isEqualTo("157");
			assertThatJson(parsedJson).array("['businessAllowances']").contains("['isTransactionCode']").isEqualTo(true);
	}

	@Test
	public void validate_get_ChannelRule_emptyInput_422_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("channel","")
					.queryParam("subchannel","WEB")
					.queryParam("language","EN")
					.queryParam("pms","OP")
					.get("/v1/rules/channel-info");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo(401);
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("validation.error.form");
			assertThatJson(parsedJson).array("['details']").contains("['elementId']").isEqualTo("channel");
			assertThatJson(parsedJson).array("['details']").contains("['errTextTemplate']").isEqualTo("must not be empty");
	}

	@Test
	public void validate_get_ChannelRule_succes_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("channel","PI")
					.queryParam("subchannel","WEB")
					.queryParam("language","EN")
					.queryParam("pms","OP")
					.get("/v1/rules/channel-info");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['sourceId']").isEqualTo("44");
			assertThatJson(parsedJson).array("['ratePlanSets']").arrayField().isEqualTo("PBF").value();
			assertThatJson(parsedJson).array("['ratePlanSets']").arrayField().isEqualTo("PBN").value();
			assertThatJson(parsedJson).field("['requestDetails']").field("['channel']").isEqualTo("PI");
			assertThatJson(parsedJson).field("['requestDetails']").field("['subchannel']").isEqualTo("WEB");
			assertThatJson(parsedJson).field("['requestDetails']").field("['language']").isEqualTo("EN");
			assertThatJson(parsedJson).field("['requestDetails']").field("['pms']").isEqualTo("OP");
	}

	@Test
	public void validate_get_MaxArrivalDateRule_emptyInput_422_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("channelId","")
					.get("/v1/rules/max-arrival-date");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo(401);
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("validation.error.form");
			assertThatJson(parsedJson).array("['details']").contains("['elementId']").isEqualTo("channelId");
			assertThatJson(parsedJson).array("['details']").contains("['errTextTemplate']").isEqualTo("must not be blank");
	}

	@Test
	public void validate_get_MaxArrivalDateRule_success_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("channelId","CCUI")
					.get("/v1/rules/max-arrival-date");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['maxArrivalDate']").isEqualTo(364);
			assertThatJson(parsedJson).field("['requestDetails']").field("['channelId']").isEqualTo("CCUI");
	}

	@Test
	public void validate_get_MaxNightsRule_emptyInput_400_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("channelId","")
					.get("/v1/rules/max-nights");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo(401);
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("validation.error.form");
			assertThatJson(parsedJson).array("['details']").contains("['elementId']").isEqualTo("channelId");
			assertThatJson(parsedJson).array("['details']").contains("['errTextTemplate']").isEqualTo("must not be blank");
	}

	@Test
	public void validate_get_MaxNightsRule_success_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("channelId","CCUI")
					.get("/v1/rules/max-nights");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['maxNights']").isEqualTo(364);
			assertThatJson(parsedJson).field("['requestDetails']").field("['channelId']").isEqualTo("CCUI");
	}

	@Test
	public void validate_get_MaxRoomsRule_emptyInput_422_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("channelId","")
					.get("/v1/rules/max-rooms");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo(401);
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("validation.error.form");
			assertThatJson(parsedJson).array("['details']").contains("['elementId']").isEqualTo("channelId");
			assertThatJson(parsedJson).array("['details']").contains("['errTextTemplate']").isEqualTo("must not be blank");
	}

	@Test
	public void validate_get_MaxRoomsRule_success_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("channelId","CCUI")
					.get("/v1/rules/max-rooms");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['maxRooms']").isEqualTo(9);
			assertThatJson(parsedJson).field("['requestDetails']").field("['channelId']").isEqualTo("CCUI");
	}

	@Test
	public void validate_get_RateSuppressionRule_success_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/rules/rate-suppressions");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['rate-suppression-list']").arrayField().isEqualTo("FLEX").value();
			assertThatJson(parsedJson).array("['rate-suppression-list']").arrayField().isEqualTo("SEMIFLEX").value();
			assertThatJson(parsedJson).array("['rate-suppression-list']").arrayField().isEqualTo("ADVANCE").value();
			assertThatJson(parsedJson).array("['rate-suppression-list']").arrayField().isEqualTo("STANDARD").value();
			assertThatJson(parsedJson).array("['rate-suppression-list']").arrayField().isEqualTo("NONFLEX").value();
	}

	@Test
	public void validate_get_RbacRule_emptyInput_400_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("resourceId","CCUI_RES1")
					.get("/v1/rules/ccui/rbac");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");
	}

	@Test
	public void validate_get_RbacRule_succes_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("resourceId","CC_Role01")
					.queryParam("roleIdList","AGENT_ROLE")
					.get("/v1/rules/ccui/rbac");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['hasAccess']").isEqualTo(true);
	}

	@Test
	public void validate_get_RoomSubstitutionRule_emptyInput_422_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("adults","2")
					.queryParam("children","0")
					.queryParam("pms","OP")
					.queryParam("roomType","")
					.get("/v1/rules/room-substitutions");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo(401);
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("validation.error.form");
			assertThatJson(parsedJson).array("['details']").contains("['elementId']").isEqualTo("roomType");
			assertThatJson(parsedJson).array("['details']").contains("['errTextTemplate']").isEqualTo("must not be empty");
	}

	@Test
	public void validate_get_RoomSubstitutionRule_success_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("adults","2")
					.queryParam("children","0")
					.queryParam("pms","OP")
					.queryParam("roomType","Double")
					.queryParam("channel","PI")
					.get("/v1/rules/room-substitutions");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['requestDetails']").field("['adults']").isEqualTo(2);
			assertThatJson(parsedJson).field("['requestDetails']").field("['children']").isEqualTo(0);
			assertThatJson(parsedJson).field("['requestDetails']").field("['roomType']").isEqualTo("Double");
			assertThatJson(parsedJson).field("['requestDetails']").field("['pms']").isEqualTo("OP");
			assertThatJson(parsedJson).array("['substitution-list']").contains("['type']").isEqualTo("DBLWIN");
			assertThatJson(parsedJson).array("['substitution-list']").contains("['silent']").isEqualTo(true);
	}

	@Test
	public void validate_get_VatRule_emptyInput_422_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("vatRegion","UK")
					.queryParam("pkgCodeArr","")
					.get("/v1/rules/vat-codes");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo(401);
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("validation.error.form");
			assertThatJson(parsedJson).array("['details']").contains("['elementId']").isEqualTo("pkgCodeArr");
			assertThatJson(parsedJson).array("['details']").contains("['errTextTemplate']").isEqualTo("must not be empty");
	}

	@Test
	public void validate_get_VatRule_success_200_contract() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("vatRegion","UK")
					.queryParam("pkgCodeArr","OBFGGO,INCPIO")
					.get("/v1/rules/vat-codes");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['vatRegion']").isEqualTo("UK");
			assertThatJson(parsedJson).array("['tranCodes']").contains("['pkgCode']").isEqualTo("INCPIO");
			assertThatJson(parsedJson).array("['tranCodes']").contains("['tranCode']").isEqualTo("9028");
			assertThatJson(parsedJson).array("['tranCodes']").contains("['tranCode']").isEqualTo("9026");
			assertThatJson(parsedJson).array("['tranCodes']").contains("['pkgCode']").isEqualTo("OBFGGO");
	}

}
