package uk.co.whitbread.piba.account.controller;

import uk.co.whitbread.piba.account.controller.ContractVerifierBaseTest;
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
	public void validate_download_invoices_fail() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/pdf")
					.header("session-id", "13245312321345")
					.header("Authorization", "Bearer dummy_value10");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/invoices/download/783582/1a409f7d-a66c-4991-a424-97b7ce0e6cf9/2294");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("ItemNotFound");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("ItemNotFound").value();
	}

	@Test
	public void validate_download_invoices_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/pdf")
					.header("session-id", "13245312321345")
					.header("Authorization", "Bearer dummy_value10");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/invoices/download/783582/1a409f7d-a66c-4991-a424-97b7ce0e6cf9/22094");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Disposition")).isEqualTo("attachment; filename=rptInvoice7835822021531.pdf");
	}

	@Test
	public void validate_download_transactions_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "13245312321345")
					.header("Authorization", "Bearer dummy_value10");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/transactions/download/782216/f6e317cf-bcf3-4859-9f7f-84068f15571a");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Disposition")).isEqualTo("attachment; filename=Transactions.csv");
	}

	@Test
	public void validate_propose_new_credit_limit_fail_server_error() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer dummy_value10")
					.body("{\"proposedCreditLimit\":800,\"schemeCustomerId\":783592344,\"tetheredUserGuid\":\"97BBBCF7-C5CA-4F95-57ED-3BE3055E333F\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/piba/account/proposecreditlimit");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("AuthenticationError");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("AuthenticationError").value();
	}

	@Test
	public void validate_propose_new_credit_limit_fail_validation_error() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer dummy_value10")
					.body("{\"proposedCreditLimit\":800,\"schemeCustomerId\":783592333,\"tetheredUserGuid\":\"97BBBCF7-C5CA-4F95-57ED-3BE3055E333F\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/piba/account/proposecreditlimit");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("2604");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Existing credit limit is 500. Requested credit limit 800 must not be greater than 750").value();
	}

	@Test
	public void validate_propose_new_credit_limit_fail_validation_error_withoutInfo() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer dummy_value10")
					.body("{\"proposedCreditLimit\":800,\"schemeCustomerId\":78359255,\"tetheredUserGuid\":\"97BBBCF7-C5CA-4F95-57GD-3BE3055E333F\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/piba/account/proposecreditlimit");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("2604");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("InvalidAction").value();
	}

	@Test
	public void validate_propose_new_credit_limit_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("Authorization", "Bearer dummy_value10")
					.body("{\"proposedCreditLimit\":670,\"schemeCustomerId\":78359222,\"tetheredUserGuid\":\"97BBBCF7-C5CA-4F95-56ED-3BE3055E333F\"}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/piba/account/proposecreditlimit");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['requestId']").isEqualTo("892198b7-818f-4ee5-8b55-02b0f52e27fb");
	}

	@Test
	public void validate_view_balance_de_success_two_customer() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "6589326768736")
					.header("company-id", "987")
					.header("employee-id", "2")
					.header("Authorization", "Bearer dummy_value9");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/balance");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(19000034);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(200.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(200.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("66b273de-8f40-4118-8bc8-29335ac1cf86");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("Frankfurt am Main");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3089500001000070");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("ACCOUNT_HOLDER").value();
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(123.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(567.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(600.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(270.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(56.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['amount']").isEqualTo(789.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("b484d2e2-38e0-4f70-9845-d029f90cdc88");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("CARD_HOLDER").value();
			assertThatJson(parsedJson).field("['totalRecordCount']").isEqualTo(2);
	}

	@Test
	public void validate_view_balance_success_all() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "13245312321345")
					.header("company-id", "123")
					.header("employee-id", "42")
					.header("Authorization", "Bearer dummy_value6");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/balance");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782810);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(8653.44);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(507.53);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(97.98);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(3000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(2902.02);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("CE3C22D3-D413-4FB2-8219-1160FEE0FC21");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("100216");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3089503200100301");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("ACCOUNT_HOLDER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782831);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(10000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(10000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("1EA0F9EF-A09B-479A-AD27-148BC6B19483");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("07734435");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3000003200100301");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782931);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['outstanding']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['newTransactions']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['available']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['creditLimit']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['currentBalance']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['interimPayments']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("62E3C0AB-1A10-4852-B5EC-BB6EB390C041");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("07788435");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("FINANCE_USER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['errorCode']").isEqualTo("Error while retrieving balance");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783139);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("AD70A44A-6324-4367-9C8C-60A985FF08D9");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783562);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(800.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(800.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("327F7A0C-9A33-41C2-808D-74F15F24797C");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783576);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("2EE9558D-1ECB-40AA-B58C-2187145B6026");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("6F5A0338-24B9-474B-B961-6EF9D07145AE");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783577);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(9999.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(9999.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("7AEC6C37-581D-44EE-AFA4-8D70E6B895F2");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783582);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("9B1B3ED5-F61C-403E-8A56-59F0B625F856");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("CARD_HOLDER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("27BC5494-E249-4577-AC98-980BE72F4966");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("6B2DDD1A-764D-445E-BC38-8C77DC215168");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("AB19C8D6-7F0C-420A-B3DE-63C9D562A9D0");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("4B3898F2-0EAC-4065-AE9A-DCBB6F617C2C");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("EECF9F1D-A69E-4D5B-8116-EF3A61E775E7");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("F74A3737-7B64-49BA-AAED-BC977C3B04F1");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("94323209-BC55-4BFA-8AA5-827CAA94DB05");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("3B9F93DE-8AB9-4A38-8581-3F56D7109986");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("1C3252B7-746B-4CC7-B07A-1C59EF007DF4");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783586);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(1500.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(1500.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("48362B2C-8B75-431C-89A8-B3A394CB0996");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783592);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(500.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(500.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("97BBBCF7-C5CA-4F95-9C8E-3BE3055E333F");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783585);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(5000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(5000.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("F9363FAD-7B08-4125-B156-ED560B66A7B1");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783593);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(200.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(200.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("9FD95156-DC9A-4FF3-9B08-114C623E8DE5");
			assertThatJson(parsedJson).field("['totalRecordCount']").isEqualTo(22);
	}

	@Test
	public void validate_view_balance_success_all_view_first_20() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("company-id", "123")
					.header("employee-id", "42")
					.header("viewAll", "false")
					.header("Authorization", "Bearer dummy_value6");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/balance");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782810);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(8653.44);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(507.53);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(97.98);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(3000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(2902.02);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("CE3C22D3-D413-4FB2-8219-1160FEE0FC21");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("100216");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3089503200100301");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("ACCOUNT_HOLDER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782831);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(10000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(10000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("1EA0F9EF-A09B-479A-AD27-148BC6B19483");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("07734435");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3000003200100301");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782931);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['outstanding']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['newTransactions']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['available']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['creditLimit']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['currentBalance']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['interimPayments']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("62E3C0AB-1A10-4852-B5EC-BB6EB390C041");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("07788435");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("FINANCE_USER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['errorCode']").isEqualTo("Error while retrieving balance");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783139);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("AD70A44A-6324-4367-9C8C-60A985FF08D9");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783562);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(800.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(800.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("327F7A0C-9A33-41C2-808D-74F15F24797C");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783576);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("2EE9558D-1ECB-40AA-B58C-2187145B6026");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("6F5A0338-24B9-474B-B961-6EF9D07145AE");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783577);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(9999.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(9999.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("7AEC6C37-581D-44EE-AFA4-8D70E6B895F2");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783582);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("9B1B3ED5-F61C-403E-8A56-59F0B625F856");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("CARD_HOLDER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("27BC5494-E249-4577-AC98-980BE72F4966");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("6B2DDD1A-764D-445E-BC38-8C77DC215168");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("AB19C8D6-7F0C-420A-B3DE-63C9D562A9D0");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("4B3898F2-0EAC-4065-AE9A-DCBB6F617C2C");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("EECF9F1D-A69E-4D5B-8116-EF3A61E775E7");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("F74A3737-7B64-49BA-AAED-BC977C3B04F1");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("94323209-BC55-4BFA-8AA5-827CAA94DB05");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("3B9F93DE-8AB9-4A38-8581-3F56D7109986");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("1C3252B7-746B-4CC7-B07A-1C59EF007DF4");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783586);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(1500.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(1500.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("48362B2C-8B75-431C-89A8-B3A394CB0996");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783592);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(500.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(500.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("97BBBCF7-C5CA-4F95-9C8E-3BE3055E333F");
			assertThatJson(parsedJson).field("['totalRecordCount']").isEqualTo(22);
	}

	@Test
	public void validate_view_balance_success_one_customer() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "13245312321345")
					.header("company-id", "123")
					.header("employee-id", "33")
					.header("Authorization", "Bearer dummy_value5");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/balance");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782810);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(8653.44);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(507.53);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(97.98);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(3000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(2902.02);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("CE3C22D3-D413-4FB2-8219-1160FEE0FC21");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("100216");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3089503200100301");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("ACCOUNT_HOLDER").value();
			assertThatJson(parsedJson).field("['totalRecordCount']").isEqualTo(1);
	}

	@Test
	public void validate_view_balance_success_piba_guid_error() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "13245312321345")
					.header("company-id", "123")
					.header("employee-id", "10")
					.header("Authorization", "Bearer dummy_value4");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/balance");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("2604");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("Error retrieving tetheredGuids").value();
	}

	@Test
	public void validate_view_balance_success_two_customer() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("company-id", "123")
					.header("employee-id", "55")
					.header("Authorization", "Bearer dummy_value7");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/balance");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782810);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(8653.44);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(507.53);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(97.98);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(3000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(2902.02);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("CE3C22D3-D413-4FB2-8219-1160FEE0FC21");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("100216");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3089503200100301");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("ACCOUNT_HOLDER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782831);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(10000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(10000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("1EA0F9EF-A09B-479A-AD27-148BC6B19483");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("07734435");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3000003200100301");
			assertThatJson(parsedJson).field("['totalRecordCount']").isEqualTo(2);
	}

	@Test
	public void validate_view_balance_worldline_error() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("company-id", "123")
					.header("employee-id", "40")
					.header("Authorization", "Bearer dummy_value2");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/balance");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783582);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['outstanding']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['newTransactions']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['available']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['creditLimit']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['currentBalance']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['interimPayments']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("9B1B3ED5-F61C-403E-8A56-59F0B625F856");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("07788435");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3089503200100301");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("CARD_HOLDER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['errorCode']").isEqualTo("Error while retrieving balance");
			assertThatJson(parsedJson).field("['totalRecordCount']").isEqualTo(1);
	}

	@Test
	public void validate_view_balance_worldline_error_some_accounts() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("company-id", "123")
					.header("employee-id", "41")
					.header("Authorization", "Bearer dummy_value1");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/balance");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("100216");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3089503200100301");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(97.98);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(3000);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(2902.02);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['amount']").isEqualTo(0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(507.53);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(8653.44);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("ACCOUNT_HOLDER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782810);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("CE3C22D3-D413-4FB2-8219-1160FEE0FC21");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("07734435");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3000003200100301");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['available']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['creditLimit']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['currentBalance']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['interimPayments']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['newTransactions']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['outstanding']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("CARD_HOLDER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782831);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("1EA0F9EF-A09B-479A-AD27-148BC6B19483");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("07788435");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['errorCode']").isEqualTo("Error while retrieving balance");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("FINANCE_USER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782931);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("62E3C0AB-1A10-4852-B5EC-BB6EB390C041");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783139);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("AD70A44A-6324-4367-9C8C-60A985FF08D9");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(800);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(800);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783562);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("327F7A0C-9A33-41C2-808D-74F15F24797C");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783576);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("2EE9558D-1ECB-40AA-B58C-2187145B6026");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(9999);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(9999);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783577);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("7AEC6C37-581D-44EE-AFA4-8D70E6B895F2");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783582);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("9B1B3ED5-F61C-403E-8A56-59F0B625F856");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("27BC5494-E249-4577-AC98-980BE72F4966");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(200);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(200);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(783593);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("9FD95156-DC9A-4FF3-9B08-114C623E8DE5");
			assertThatJson(parsedJson).field("['totalRecordCount']").isEqualTo(10);
	}

	@Test
	public void validate_view_balance_worldline_single_user_detail_error() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("company-id", "123")
					.header("employee-id", "43")
					.header("Authorization", "Bearer dummy_value8");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/balance");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['outstanding']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['newTransactions']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['available']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['creditLimit']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['currentBalance']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['interimPayments']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['registrationRoles']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['errorCode']").isEqualTo("Error while retrieving tethered user details");
			assertThatJson(parsedJson).field("['totalRecordCount']").isEqualTo(1);
	}

	@Test
	public void validate_view_balance_worldline_user_details_error() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("company-id", "123")
					.header("employee-id", "15")
					.header("Authorization", "Bearer dummy_value3");

		// when:
			ResponseOptions response = given().spec(request)
					.get("/piba/account/balance");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(782810);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['amount']").isEqualTo(8653.44);
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['outstanding']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['amount']").isEqualTo(507.53);
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['newTransactions']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['amount']").isEqualTo(97.98);
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['available']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['amount']").isEqualTo(3000.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['creditLimit']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['amount']").isEqualTo(2902.02);
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['currentBalance']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['amount']").isEqualTo(0.0);
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).array("['currentBalances']").field("['interimPayments']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isEqualTo("CE3C22D3-D413-4FB2-8219-1160FEE0FC21");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isEqualTo("100216");
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isEqualTo("3089503200100301");
			assertThatJson(parsedJson).array("['currentBalances']").array("['registrationRoles']").arrayField().isEqualTo("ACCOUNT_HOLDER").value();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['schemeCustomerId']").isEqualTo(0);
			assertThatJson(parsedJson).array("['currentBalances']").contains("['outstanding']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['newTransactions']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['available']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['creditLimit']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['currentBalance']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['interimPayments']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['tetheredGuid']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountName']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['accountNumber']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['registrationRoles']").isNull();
			assertThatJson(parsedJson).array("['currentBalances']").contains("['errorCode']").isEqualTo("Error while retrieving tethered user details");
			assertThatJson(parsedJson).field("['totalRecordCount']").isEqualTo(2);
	}

	@Test
	public void validate_view_invoices_fail() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "13245312321345")
					.header("Authorization", "Bearer dummy_value10")
					.body("{\"schemeCustomerId\":\"9876543\",\"tetheredUserGuid\":\"trwtes-7cf-bcf3-asda-9f7f-84068f15571a\",\"searchCriteria\":{\"dateFrom\":\"2020-05-05\",\"dateTo\":\"2021-03-15\"},\"pagingRequest\":{\"page\":5,\"maximumDisplayRows\":10}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/piba/account/invoices");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("SchemaError");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("SchemaError").value();
	}

	@Test
	public void validate_view_invoices_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "13245312321345")
					.header("Authorization", "Bearer dummy_value10")
					.body("{\"schemeCustomerId\":\"123456\",\"tetheredUserGuid\":\"f6e317cf-bcf3-asda-9f7f-84068f15571a\",\"searchCriteria\":{\"dateFrom\":\"2020-05-05\",\"dateTo\":\"2021-03-15\"},\"pagingRequest\":{\"page\":5,\"maximumDisplayRows\":10}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/piba/account/invoices");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['broughtForward']").field("['amount']").isEqualTo(755.88);
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['broughtForward']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['broughtForward']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).field("['response']").array("['invoices']").contains("['fileAutoID']").isEqualTo(123456);
			assertThatJson(parsedJson).field("['response']").array("['invoices']").contains("['invoiceNo']").isEqualTo(123456780);
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['invoiceValue']").field("['amount']").isEqualTo(55.8);
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['invoiceValue']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['invoiceValue']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['overdueBalance']").field("['amount']").isEqualTo(755.88);
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['overdueBalance']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['overdueBalance']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['paymentsReceived']").field("['amount']").isEqualTo(35.99);
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['paymentsReceived']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['paymentsReceived']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['statementBalance']").field("['amount']").isEqualTo(155.88);
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['statementBalance']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['invoices']").field("['statementBalance']").field("['currencySymbol']").isEqualTo("\u00A3");
			assertThatJson(parsedJson).field("['response']").array("['invoices']").contains("['statementDate']").isEqualTo("2020-08-02");
			assertThatJson(parsedJson).field("['pagingResult']").field("['fromRecord']").isEqualTo(1);
			assertThatJson(parsedJson).field("['pagingResult']").field("['toRecord']").isEqualTo(10);
			assertThatJson(parsedJson).field("['pagingResult']").field("['totalRecordCount']").isEqualTo(1);
			assertThatJson(parsedJson).field("['pagingResult']").field("['lastPage']").isEqualTo(1);
	}

	@Test
	public void validate_view_transactions_fail() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "13245312321345")
					.header("Authorization", "Bearer dummy_value10")
					.body("{\"schemeCustomerId\":\"782215\",\"tetheredUserGuid\":\"f6e317cf-bcf3-4859-9f7f-84068f15571b\",\"searchCriteria\":{\"dateSearch\":{\"dateFrom\":\"2020-01-01\",\"dateTo\":\"2021-03-15\",\"transactionTypes\":\"Both\"}},\"pagingRequest\":{\"page\":5,\"maximumDisplayRows\":10}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/piba/account/transactions");

		// then:
			assertThat(response.statusCode()).isEqualTo(500);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("ValidationError");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("UserNotFoundForSystem").value();
	}

	@Test
	public void validate_view_transactions_fail_search_criteri_both_set() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "13245312321345")
					.header("Authorization", "Bearer dummy_value10")
					.body("{\"schemeCustomerId\":\"782216\",\"tetheredUserGuid\":\"9a2cf458-9c10-461b-8dad-a8b5712653cc\",\"searchCriteria\":{\"dateSearch\":{\"dateFrom\":\"2020-01-01\",\"dateTo\":\"2021-03-15\",\"transactionTypes\":\"Both\"},\"invoiceNumberSearch\":{\"invoiceNumber\":5}},\"pagingRequest\":{\"page\":5,\"maximumDisplayRows\":10}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/piba/account/transactions");

		// then:
			assertThat(response.statusCode()).isEqualTo(400);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['code']").isEqualTo("001");
			assertThatJson(parsedJson).array("['details']").arrayField().isEqualTo("searchCriteria Invalid selection criteria").value();
	}

	@Test
	public void validate_view_transactions_success() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "13245312321345")
					.header("Authorization", "Bearer dummy_value10")
					.body("{\"schemeCustomerId\":\"782216\",\"tetheredUserGuid\":\"f6e317cf-bcf3-4859-9f7f-84068f15571a\",\"searchCriteria\":{\"dateSearch\":{\"dateFrom\":\"2020-01-01\",\"dateTo\":\"2021-03-15\",\"transactionTypes\":\"Both\"}},\"pagingRequest\":{\"page\":5,\"maximumDisplayRows\":10}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/piba/account/transactions");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['invoiceDate']").isEqualTo("2020-08-02");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['transactionDate']").isEqualTo("2020-02-13T18:40:00");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['netAmount']").field("['amount']").isEqualTo(719.89);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['netAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['taxAmount']").field("['amount']").isEqualTo(35.99);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['taxAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['grossAmount']").field("['amount']").isEqualTo(755.88);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['grossAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['location']").isEqualTo("Jersey St Helier (Charing Cros");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['pan']").isEqualTo("30895001*******0018");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['cardName']").isEqualTo("Craig Penton");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['description']").isEqualTo("Accommodation");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['quantity']").isEqualTo(1);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['netAmount']").field("['amount']").isEqualTo(719.89);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['netAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['taxAmount']").field("['amount']").isEqualTo(35.99);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['taxAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['grossAmount']").field("['amount']").isEqualTo(755.88);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['grossAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['guestName']").isEqualTo("Craig Penton");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(5);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(3);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(8);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(9);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(1);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['transactionDate']").isEqualTo("2020-02-13T21:39:00");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['netAmount']").field("['amount']").isEqualTo(179.97);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['netAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['taxAmount']").field("['amount']").isEqualTo(34.2);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['taxAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['grossAmount']").field("['amount']").isEqualTo(214.17);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['grossAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['location']").isEqualTo("Frankfurt");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['netAmount']").field("['amount']").isEqualTo(179.97);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['netAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['taxAmount']").field("['amount']").isEqualTo(34.2);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['taxAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['grossAmount']").field("['amount']").isEqualTo(214.17);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['grossAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(27);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(23);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(25);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(20);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(19);
			assertThatJson(parsedJson).field("['pagingResult']").field("['fromRecord']").isEqualTo(41);
			assertThatJson(parsedJson).field("['pagingResult']").field("['toRecord']").isEqualTo(50);
			assertThatJson(parsedJson).field("['pagingResult']").field("['totalRecordCount']").isEqualTo(594);
			assertThatJson(parsedJson).field("['pagingResult']").field("['lastPage']").isEqualTo(60);
	}

	@Test
	public void validate_view_transactions_success_invoice_criteria() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "13245312321345")
					.header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImI5OGZkOWY5LTBmOTQtNDI3Yy1hMjE4LWIzMzg0ZDRkZjNmMiJ9")
					.body("{\"schemeCustomerId\":\"782217\",\"tetheredUserGuid\":\"f6e317cf-bcf3-4859-9f7f-84068f15571f\",\"searchCriteria\":{\"invoiceNumberSearch\":{\"invoiceNumber\":5}},\"pagingRequest\":{\"page\":5,\"maximumDisplayRows\":10}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/piba/account/transactions");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['invoiceDate']").isEqualTo("2020-08-02");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['invoiceNo']").isEqualTo(5);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['transactionDate']").isEqualTo("2020-02-13T18:40:00");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['netAmount']").field("['amount']").isEqualTo(719.89);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['netAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['taxAmount']").field("['amount']").isEqualTo(35.99);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['taxAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['grossAmount']").field("['amount']").isEqualTo(755.88);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['grossAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['location']").isEqualTo("Jersey St Helier (Charing Cros");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['pan']").isEqualTo("30895001*******0018");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['cardName']").isEqualTo("Craig Penton");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['description']").isEqualTo("Accommodation");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['quantity']").isEqualTo(1);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['netAmount']").field("['amount']").isEqualTo(719.89);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['netAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['taxAmount']").field("['amount']").isEqualTo(35.99);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['taxAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['grossAmount']").field("['amount']").isEqualTo(755.88);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['grossAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['guestName']").isEqualTo("Craig Penton");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(5);
			assertThatJson(parsedJson).field("['pagingResult']").field("['fromRecord']").isEqualTo(41);
			assertThatJson(parsedJson).field("['pagingResult']").field("['toRecord']").isEqualTo(50);
			assertThatJson(parsedJson).field("['pagingResult']").field("['totalRecordCount']").isEqualTo(594);
			assertThatJson(parsedJson).field("['pagingResult']").field("['lastPage']").isEqualTo(60);
	}

	@Test
	public void validate_view_transactions_success_search_criteria_null() throws Exception {
		// given:
			MockMvcRequestSpecification request = given()
					.header("Content-Type", "application/json")
					.header("session-id", "13245312321345")
					.header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImI5OGZkOWY5LTBmOTQtNDI3Yy1hMjE4LWIzMzg0ZDRkZjNmMiJ9")
					.body("{\"schemeCustomerId\":\"782216\",\"tetheredUserGuid\":\"9a2cf458-9c10-461b-8dad-a8b5712653cc\",\"pagingRequest\":{\"page\":5,\"maximumDisplayRows\":10}}");

		// when:
			ResponseOptions response = given().spec(request)
					.post("/piba/account/transactions");

		// then:
			assertThat(response.statusCode()).isEqualTo(200);
			assertThat(response.header("Content-Type")).isEqualTo("application/json");

		// and:
			DocumentContext parsedJson = JsonPath.parse(response.getBody().asString());
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['invoiceDate']").isEqualTo("2020-08-02");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['transactionDate']").isEqualTo("2020-02-13T18:40:00");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['netAmount']").field("['amount']").isEqualTo(719.89);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['netAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['taxAmount']").field("['amount']").isEqualTo(35.99);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['taxAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['grossAmount']").field("['amount']").isEqualTo(755.88);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['grossAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['location']").isEqualTo("Jersey St Helier (Charing Cros");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['pan']").isEqualTo("30895001*******0018");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['cardName']").isEqualTo("Craig Penton");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['description']").isEqualTo("Accommodation");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['quantity']").isEqualTo(1);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['netAmount']").field("['amount']").isEqualTo(719.89);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['netAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['taxAmount']").field("['amount']").isEqualTo(35.99);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['taxAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['grossAmount']").field("['amount']").isEqualTo(755.88);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['grossAmount']").field("['currencyCode']").isEqualTo("GBP");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['guestName']").isEqualTo("Craig Penton");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(5);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(3);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(8);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(9);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(1);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['transactionDate']").isEqualTo("2020-02-13T21:39:00");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['netAmount']").field("['amount']").isEqualTo(179.97);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['netAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['taxAmount']").field("['amount']").isEqualTo(34.2);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['taxAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['grossAmount']").field("['amount']").isEqualTo(214.17);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").field("['grossAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").contains("['location']").isEqualTo("Frankfurt");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['netAmount']").field("['amount']").isEqualTo(179.97);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['netAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['taxAmount']").field("['amount']").isEqualTo(34.2);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['taxAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['grossAmount']").field("['amount']").isEqualTo(214.17);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").field("['grossAmount']").field("['currencyCode']").isEqualTo("EUR");
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(27);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(23);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(25);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(20);
			assertThatJson(parsedJson).field("['response']").array("['transactions']").array("['lineItems']").contains("['invoiceLineItem']").isEqualTo(19);
			assertThatJson(parsedJson).field("['pagingResult']").field("['fromRecord']").isEqualTo(41);
			assertThatJson(parsedJson).field("['pagingResult']").field("['toRecord']").isEqualTo(50);
			assertThatJson(parsedJson).field("['pagingResult']").field("['totalRecordCount']").isEqualTo(594);
			assertThatJson(parsedJson).field("['pagingResult']").field("['lastPage']").isEqualTo(60);
	}

}
