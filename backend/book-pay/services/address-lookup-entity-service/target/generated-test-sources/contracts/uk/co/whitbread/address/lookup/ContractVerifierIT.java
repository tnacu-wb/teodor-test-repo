package uk.co.whitbread.address.lookup;

import uk.co.whitbread.address.lookup.ContractVerifierBaseTest;
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
	public void validate_format_Address_IncorrectMonikerId() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/addresses/GBX|incorrectMoniker|Id");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo("525");
			assertThatJson(parsedJson).field("['debugMessage']").isEqualTo("Unable to get search result by monikerId: GBX|incorrectMoniker|IdThe specified moniker is invalid. (19e83d97-1aaf-46be-a335-3474c9bb901e)");
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("invalid.moniker.id");
	}

	@Test
	public void validate_format_Address_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.get("/v1/addresses/GBX|ed2025d8-fd39-4d53-8a18-f65f233568ad|7.730BOGBXEgHjBwAAAAABAwEAAAAEL_2mkgAhEAYRAKEAAgAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTiAyVEQAAAAAAA--$8");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['postalCode']").isEqualTo("NW3 4NB");
			assertThatJson(parsedJson).field("['label']").isEqualTo("Flat 1, Manor Mansions, Belsize Grove, LONDON, NW3 4NB");
			assertThatJson(parsedJson).field("['addressLine1']").isEqualTo("Flat 1, Manor Mansions");
			assertThatJson(parsedJson).field("['addressLine2']").isEqualTo("Belsize Grove");
			assertThatJson(parsedJson).field("['addressLine4']").isEqualTo("LONDON");
			assertThatJson(parsedJson).field("['country']").isEqualTo("GB");
	}

	@Test
	public void validate_search_Address_400Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("countryCode","gb")
					.get("/v1/addresses");

		// then:
			assertThat(response.statusCode()).isEqualTo(422);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['errCode']").isEqualTo("401");
			assertThatJson(parsedJson).field("['globalErrTextTemplate']").isEqualTo("validation.error.form");
			assertThatJson(parsedJson).array("['details']").contains("['elementId']").isEqualTo("searchTerm");
			assertThatJson(parsedJson).array("['details']").contains("['errTextTemplate']").isEqualTo("must not be blank");
	}

	@Test
	public void validate_search_Address_Success_200Response() throws Exception {
		// given:
			MockMvcRequestSpecification request = given();


		// when:
			ResponseOptions response = given().spec(request)
					.queryParam("searchTerm","EC1N2TD")
					.get("/v1/addresses");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array().contains("['id']").isEqualTo("GBR|42396479-4167-462a-b739-92f36392641b|7.730fOGBREwTjBwAAAAABAwEAAAABg5zTkgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTjJURAAAAAAA$7");
			assertThatJson(parsedJson).array().contains("['addressText']").isEqualTo("North Highland, 120 Holborn, LONDON EC1N 2TD");
			assertThatJson(parsedJson).array().contains("['id']").isEqualTo("GBR|42396479-4167-462a-b739-92f36392641b|7.730COGBREwTjBwAAAAABAwEAAAABg5zVkgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTjJURAAAAAAA$7");
			assertThatJson(parsedJson).array().contains("['addressText']").isEqualTo("Secret Escapes Ltd, 120 Holborn, LONDON EC1N 2TD");
			assertThatJson(parsedJson).array().contains("['id']").isEqualTo("GBR|42396479-4167-462a-b739-92f36392641b|7.730ROGBREwTjBwAAAAABAwEAAAABg5zXkgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTjJURAAAAAAA$7");
			assertThatJson(parsedJson).array().contains("['addressText']").isEqualTo("Steria Learning Services, 120 Holborn, LONDON EC1N 2TD");
			assertThatJson(parsedJson).array().contains("['id']").isEqualTo("GBR|42396479-4167-462a-b739-92f36392641b|7.730MOGBREwTjBwAAAAABAwEAAAABg5zZkgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTjJURAAAAAAA$7");
			assertThatJson(parsedJson).array().contains("['addressText']").isEqualTo("The Money Advice Service, 120 Holborn, LONDON EC1N 2TD");
			assertThatJson(parsedJson).array().contains("['id']").isEqualTo("GBR|42396479-4167-462a-b739-92f36392641b|7.730NOGBREwTjBwAAAAABAwEAAAABg5zckgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTjJURAAAAAAA$7");
			assertThatJson(parsedJson).array().contains("['addressText']").isEqualTo("Trainline, 120 Holborn, LONDON EC1N 2TD");
			assertThatJson(parsedJson).array().contains("['id']").isEqualTo("GBR|42396479-4167-462a-b739-92f36392641b|7.730qOGBREwTjBwAAAAABAwEAAAABg5zekgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTjJURAAAAAAA$7");
			assertThatJson(parsedJson).array().contains("['addressText']").isEqualTo("Whitbread Group Plc, 120 Holborn, LONDON EC1N 2TD");
			assertThatJson(parsedJson).array().contains("['id']").isEqualTo("GBR|42396479-4167-462a-b739-92f36392641b|7.7301OGBREwTjBwAAAAABAwEAAAABg5zgkgAhAAIAAAAAAAAAAAD..2QAAAAA.....wAAAAAAAAAAAAAAAAAAAEVDMU4yVEQAAAAAAA--$7");
			assertThatJson(parsedJson).array().contains("['addressText']").isEqualTo("121 Holborn, LONDON EC1N 2TD");
			assertThatJson(parsedJson).array().contains("['id']").isEqualTo("GBR|42396479-4167-462a-b739-92f36392641b|7.730kOGBREwTjBwAAAAABAwEAAAABg5zikgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTjJURAAAAAAA$7");
			assertThatJson(parsedJson).array().contains("['addressText']").isEqualTo("Boots the Chemists Ltd, 122 Holborn, LONDON EC1N 2TD");
			assertThatJson(parsedJson).array().contains("['id']").isEqualTo("GBR|42396479-4167-462a-b739-92f36392641b|7.7305OGBREwTjBwAAAAABAwEAAAABg5zkkgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTjJURAAAAAAA$7");
			assertThatJson(parsedJson).array().contains("['addressText']").isEqualTo("Sainsburys Supermarkets Ltd, 123 Holborn, LONDON EC1N 2TD");
			assertThatJson(parsedJson).array().contains("['id']").isEqualTo("GBR|42396479-4167-462a-b739-92f36392641b|7.7305OGBREwTjBwAAAAABAwEAAAABg5x5JABFEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTjJURAAAAAAA$7");
			assertThatJson(parsedJson).array().contains("['addressText']").isEqualTo("D Young & Co, 120 Holborn, LONDON EC1N 2DY");
			assertThatJson(parsedJson).array().contains("['id']").isEqualTo("GBR|42396479-4167-462a-b739-92f36392641b|7.7304OGBREwTjBwAAAAABAwEAAAABg5y15ABFEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTjJURAAAAAAA$7");
			assertThatJson(parsedJson).array().contains("['addressText']").isEqualTo("W H Smith Ltd, 124 Holborn, LONDON EC1N 2QX");
	}

}
