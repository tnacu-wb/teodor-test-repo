package uk.co.whitbread.company.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.whitbread.company.config.TestApplication;
import uk.co.whitbread.company.config.TestSecurityConfig;
import uk.co.whitbread.company.model.Address;
import uk.co.whitbread.company.model.CheckCompanyResponse;
import uk.co.whitbread.company.model.CompanyDetailsResponse;
import uk.co.whitbread.company.model.CompanySummary;
import uk.co.whitbread.company.model.MainContact;
import uk.co.whitbread.company.service.cdh.CdhAuthorizationService;
import uk.co.whitbread.company.service.cdh.CdhCompanyDetailsService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@SpringBootTest(
    classes = {TestApplication.class, TestSecurityConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("testCdh")
class CompanyControllerTest {

    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_TOKEN = "Bearer Authorization";
    private static final String COMPANY_ID = "companyId";
    private static final String COMPANY_ID_PARAM = "company-id";
    private static final String EMPLOYEE_ID_PARAM = "employee-id";

    private static final String ADDRESS_LINE_1 = "4 Braintree Road";
    private static final String POSTCODE = "HA4 0EJ";
    private static final String COUNTRY = "GB";
    private static final String EMPLOYEE_EMAIL_ADDRESS = "methmal@gmail.com";
    private static final String EMPLOYEE_ID = "1";
    private static final String TITLE = "Mr";
    private static final String FIRST_NAME = "Meth";
    private static final String LAST_NAME = "Test";
    private static final String ALTERNATE_COMPANY_NAME = "Whit";
    private static final String COMPANY_NAME = "Whitbread The Second";

    @LocalServerPort
    private int serverPort;

    @MockitoBean
    private TokenService authTokenService;

    @MockitoBean
    private CdhCompanyDetailsService cdhCompanyDetailsService;

    @MockitoBean
    private CdhAuthorizationService cdhAuthorizationService;

    private ObjectMapper objectMapper;
    private String companyName;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String baseUrl() {
        return "http://localhost:" + serverPort;
    }

    private static String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        companyName = "Whitbread";
    }

    @Test
    void searchByCompanyNameAndAddress_shouldHandleValidationError() throws Exception {
        String url = baseUrl() + "/company/check?"
            + "addressLine1=" + enc("120 Holborn")
            + "&postCode=" + enc("EC1N 2TD")
            + "&country=GB";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .header(COMPANY_ID_PARAM, COMPANY_ID)
            .header(EMPLOYEE_ID_PARAM, EMPLOYEE_ID)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(400);
        JsonNode body = objectMapper.readTree(response.body());
        assertThat(body.get("code").asText()).isEqualTo("013");
        assertThat(body.get("details")).hasSize(1);
        assertThat(body.get("details").get(0).asText())
            .isEqualTo("Required request parameter 'companyName' for method parameter type String is not present");
    }

    @Test
    void searchByCompanyNameAndAddress_shouldGetOkResponse() throws Exception {
        var checkResponse = new CheckCompanyResponse();
        checkResponse.setExistingCompany(true);
        when(cdhCompanyDetailsService.searchByCompanyNameAndAddress(any(), any())).thenReturn(checkResponse);

        String url = baseUrl() + "/company/check?"
            + "companyName=" + enc(companyName)
            + "&addressLine1=" + enc("120 Holborn")
            + "&postCode=" + enc("EC1N 2TD")
            + "&country=GB";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .header(COMPANY_ID_PARAM, COMPANY_ID)
            .header(EMPLOYEE_ID_PARAM, EMPLOYEE_ID)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        JsonNode body = objectMapper.readTree(response.body());
        assertThat(body.get("existingCompany").asBoolean()).isTrue();
    }

    @Test
    void updateCompanyDetails_shouldReturnNO_CONTENT() throws Exception {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
                .userEmail("userEmail@gmail.com")
                .companyAccountId(COMPANY_ID)
                .employeeAccountId(EMPLOYEE_ID)
                .build();

        when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN))
                .thenReturn(cdhEmployeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        var companySummary = buildCompanySummary();
        doNothing().when(cdhCompanyDetailsService)
                .updateCompany(COMPANY_ID, companySummary, cdhEmployeeDetails.getUserEmail());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/company/admin/" + COMPANY_ID))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(buildCompanySummary())))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    void retrieveCompanyDetails_shouldGetOKResponse() throws Exception {
        CompanyDetailsResponse expectedResponse = objectMapper.readValue(
                new File("src/test/resources/mappings/CompanyDetailsResponse.json"),
                CompanyDetailsResponse.class);

        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
                .userEmail("userEmail@gmail.com")
                .companyAccountId(COMPANY_ID)
                .employeeAccountId(EMPLOYEE_ID)
                .build();

        when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN))
                        .thenReturn(cdhEmployeeDetails);
        when(cdhCompanyDetailsService.getCompanyDetails(COMPANY_ID, cdhEmployeeDetails.getUserEmail()))
                .thenReturn(expectedResponse);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/company/" + COMPANY_ID))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("Cache-Control")).hasValue("no-cache");
        JsonNode body = objectMapper.readTree(response.body());
        assertThat(body.get("success").asBoolean()).isTrue();
    }

    @Test
    void retrieveCompanyDetails_unauthorizedForEmptyCompanyAccountId() throws Exception {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .userEmail("userEmail@gmail.com")
            .companyAccountId(StringUtils.EMPTY)
            .employeeAccountId(EMPLOYEE_ID)
            .build();
        when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN))
            .thenReturn(cdhEmployeeDetails);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/company/" + COMPANY_ID))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    void retrieveCompanyDetails_unauthorizedForEmptyEmployeeAccountId() throws Exception {
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .userEmail("userEmail@gmail.com")
            .companyAccountId(COMPANY_ID)
            .employeeAccountId(StringUtils.EMPTY)
            .build();
        when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN))
            .thenReturn(cdhEmployeeDetails);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/company/" + COMPANY_ID))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    void retrieveCompanyDetails_unauthorizedForEmptyAuthorization() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/company/" + COMPANY_ID))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, "")
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    private CompanySummary buildCompanySummary() {
        Address companyAddress = Address.builder()
                .addressLine1(ADDRESS_LINE_1)
                .country(COUNTRY)
                .postCode(POSTCODE)
                .build();

        MainContact mainContact = MainContact.builder()
                .id(EMPLOYEE_ID)
                .emailAddress(EMPLOYEE_EMAIL_ADDRESS)
                .title(TITLE)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();

        return CompanySummary.builder()
                .alternateCompanyName(ALTERNATE_COMPANY_NAME)
                .companyName(COMPANY_NAME)
                .companyAddress(companyAddress)
                .mainContact(mainContact)
                .build();
    }
}
