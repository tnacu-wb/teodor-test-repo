package uk.co.whitbread.piba.account.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.whitbread.piba.account.helper.AuthTestHelper;
import uk.co.whitbread.piba.account.model.CreditProposeLimitRequest;
import uk.co.whitbread.piba.account.model.CreditProposeLimitResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalances;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalancesResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceListDownloadResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsByInvoiceNumberCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountsResponse;
import uk.co.whitbread.piba.account.model.ResetMemorableWordRequest;
import uk.co.whitbread.piba.account.model.ResetMemorableWordResponse;
import uk.co.whitbread.piba.account.model.TetheredGuidDetails;
import uk.co.whitbread.piba.account.model.TetheredUserRequest;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.service.PibaAccountService;
import uk.co.whitbread.piba.account.util.TestUtil;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;
import uk.co.whitbread.shared.cdh.EmployeeDataService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PibaAccountControllerTest {
    @LocalServerPort
    int serverPort;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String EMPLOYEE_ID_HEADER = "employee-id";
    private static final String COMPANY_ID_HEADER = "company-id";
    private static final String VIEW_ALL_HEADER = "viewAll";
    private static final String VALID_COMPANY_ID = "55";
    private static final String VALID_EMPLOYEE_ID = "22";

    @MockitoBean
    private PibaAccountService mockPibaAccountService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private EmployeeDataService employeeDataService;

    @MockitoBean
    private CacheManager cacheManager1HourCdh;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private TenantRepository tenantRepository;

    private final CustomerAccountCurrentBalancesResponse customerAccountCurrentBalancesResponse= new CustomerAccountCurrentBalancesResponse();
    private final CustomerAccountsResponse customerAccountsResponse = new CustomerAccountsResponse();
    private final CustomerAccountTransactionsRequest customerAccountTransactionsRequest = new CustomerAccountTransactionsRequest();
    private final ByteArrayOutputStream transactionListOutputStream = new ByteArrayOutputStream();
    private final CustomerAccountInvoiceListDownloadResponse downloadResponse = new CustomerAccountInvoiceListDownloadResponse();
    private final TestUtil testUtil = new TestUtil();
    private final CustomerAccountInvoiceResponse customerAccountInvoiceResponse = new CustomerAccountInvoiceResponse();

    @Autowired
    private ObjectMapper objectMapper;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = HttpClient.newHttpClient();
        AuthTestHelper.configureAuth0Context(tenantRepository, jwtDecoder);
    }

    private String baseUrl() {
        return "http://localhost:" + serverPort;
    }

    @Test
    void getBalance_ShouldReturnHttpStatusCode200() throws Exception {

        when(mockPibaAccountService.viewCurrentBalance(any(),eq(true))).thenReturn(customerAccountCurrentBalancesResponse);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/balance"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .header(COMPANY_ID_HEADER, VALID_COMPANY_ID)
                .header(EMPLOYEE_ID_HEADER, VALID_EMPLOYEE_ID)
                .header(VIEW_ALL_HEADER, "true")
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void getTransactions_ShouldReturnHttpStatusCode200() throws Exception {
        CustomerAccountTransactionsCriteria customerAccountTransactionsCriteria=new CustomerAccountTransactionsCriteria();
        CustomerAccountTransactionsByInvoiceNumberCriteria transactionsByInvoiceNumberCriteria= new CustomerAccountTransactionsByInvoiceNumberCriteria();
        transactionsByInvoiceNumberCriteria.setInvoiceNumber(123);
        customerAccountTransactionsCriteria.setInvoiceNumberSearch(transactionsByInvoiceNumberCriteria);
        customerAccountTransactionsRequest.setSchemeCustomerId(132);
        customerAccountTransactionsRequest.setTetheredUserGuid("92562846-62b5-42d8-b393-132a1d78fd1a");
        customerAccountTransactionsRequest.setSearchCriteria(customerAccountTransactionsCriteria);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/transactions"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(customerAccountTransactionsRequest)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void downloadTransactions_ShouldReturnHttpStatusCode200() throws Exception {
        when(mockPibaAccountService.downloadTransactions(132,"92562846-62b5-42d8-b393-132a1d78fd1a")).thenReturn(transactionListOutputStream);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/transactions/download/132/92562846-62b5-42d8-b393-132a1d78fd1a"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void getInvoices_ShouldReturnHttpStatusCode200() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/invoices"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(
                    testUtil.createCustomerAccountInvoiceRequest(TestUtil.TETHERED_USER_GUID,TestUtil.SCHEME_CUSTOMER_ID))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void getInvoicesV2_ShouldReturnHttpStatusCode200() throws Exception {
        when(mockPibaAccountService.viewInvoicesV2(anyString(), any())).thenReturn(customerAccountInvoiceResponse);
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(CdhEmployeeDetails.builder()
                .companyAccountId(VALID_COMPANY_ID)
                .employeeAccountId(VALID_EMPLOYEE_ID)
                .userEmail("user-mail")
                .build());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/v2/piba/account/invoices"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(
                    testUtil.createCustomerAccountInvoiceRequest(TestUtil.TETHERED_USER_GUID,TestUtil.SCHEME_CUSTOMER_ID))))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void downloadInvoices_ShouldReturnHttpStatusCode200() throws Exception {
        when(mockPibaAccountService.downloadInvoices(783582, "1a409f7d-a66c-4991-a424-97b7ce0e6cf9", 22094, Scheme.GB)).thenReturn(downloadResponse);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/invoices/download/783582/1a409f7d-a66c-4991-a424-97b7ce0e6cf9/22094?scheme=GB"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void downloadInvoicesV2_ShouldReturnHttpStatusCode200() throws Exception {
        String token = "Bearer random_token";
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(CdhEmployeeDetails.builder()
                .companyAccountId(VALID_COMPANY_ID)
                .employeeAccountId(VALID_EMPLOYEE_ID)
                .userEmail("user-mail")
                .build());
        when(mockPibaAccountService.downloadInvoicesV2(token, 783582, "1a409f7d-a66c-4991-a424-97b7ce0e6cf9", 22094, Scheme.GB)).thenReturn(downloadResponse);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/v2/piba/account/invoices/download/783582/1a409f7d-a66c-4991-a424-97b7ce0e6cf9/22094?scheme=GB"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, token)
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void proposeNewCreditLimit_ShouldReturnHttpStatusCode200() throws Exception {

        CreditProposeLimitRequest creditProposeLimitRequest = new CreditProposeLimitRequest(TestUtil.TETHERED_USER_GUID,
                                                                                            TestUtil.SCHEME_CUSTOMER_ID,
                                                                                            700);
        CreditProposeLimitResponse creditProposeLimitResponse = new CreditProposeLimitResponse("888991ff-d0ee-435d-ac78-57e91556deca");
        when(mockPibaAccountService.proposeNewCreditLimit(any())).thenReturn(creditProposeLimitResponse);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/proposecreditlimit"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(creditProposeLimitRequest)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }
    
    
    @Test
    void resetMemorableWord_ShouldReturnHttpStatusCode200() throws Exception {
        
        ResetMemorableWordRequest memorableWordRequest = new ResetMemorableWordRequest(TestUtil.TETHERED_USER_GUID,
                TestUtil.MEMORABLE_WORD);
        ResetMemorableWordResponse response = new ResetMemorableWordResponse("3462");
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(CdhEmployeeDetails.builder()
                .companyAccountId(VALID_COMPANY_ID)
                .employeeAccountId(VALID_EMPLOYEE_ID)
                .userEmail("user-mail")
                .build());
        when(mockPibaAccountService.resetMemorableWord(any())).thenReturn(response);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/memorableword"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(memorableWordRequest)))
                .build();

        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(resp.statusCode()).isEqualTo(200);
    }

    @Test
    void resetMemorableWordWithScheme_ShouldReturnHttpStatusCode200() throws Exception {

        ResetMemorableWordRequest memorableWordRequest = new ResetMemorableWordRequest(TestUtil.TETHERED_USER_GUID,
                TestUtil.MEMORABLE_WORD);
        ResetMemorableWordResponse response = new ResetMemorableWordResponse("3462");
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(CdhEmployeeDetails.builder()
                .companyAccountId(VALID_COMPANY_ID)
                .employeeAccountId(VALID_EMPLOYEE_ID)
                .userEmail("user-mail")
                .build());
        when(mockPibaAccountService.resetMemorableWord(any())).thenReturn(response);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/memorableword?scheme=GB"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(memorableWordRequest)))
                .build();

        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(resp.statusCode()).isEqualTo(200);
    }

    @Test
    void getAccounts_ShouldReturnHttpStatusCode200() throws Exception {

        when(mockPibaAccountService.getAccounts(any(), eq(true), eq(false))).thenReturn(customerAccountsResponse);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/customers"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .header(COMPANY_ID_HEADER, VALID_COMPANY_ID)
                .header(EMPLOYEE_ID_HEADER, VALID_EMPLOYEE_ID)
                .header(VIEW_ALL_HEADER, "true")
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void viewAccountBalanceSummary_ShouldReturnHttpStatusCode200() throws Exception {
        // Arrange
        when(mockPibaAccountService.viewCurrentBalanceSummary(any(),eq(true))).thenReturn(customerAccountCurrentBalancesResponse);

        // Act & Assert
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/balance/summary"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .header(VIEW_ALL_HEADER, "true")
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void viewAccountBalanceSummary_ServiceThrowsException() throws Exception {
        // Arrange
        when(mockPibaAccountService.viewCurrentBalanceSummary(any(),eq(true))).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/balance/summary"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .header(VIEW_ALL_HEADER, "true")
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(500);
    }

    @Test
    void registerTetheredUser_ShouldReturnHttpStatusCode204() throws Exception {
        // Arrange
        var tetheredUserRequest = new TetheredUserRequest(Scheme.GB,
            "company-id",
            List.of(new TetheredGuidDetails("employee-id", "tethered-guid")));
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(CdhEmployeeDetails.builder()
                .companyAccountId(VALID_COMPANY_ID)
                .employeeAccountId(VALID_EMPLOYEE_ID)
                .userEmail("user-mail")
                .build());
        doNothing().when(employeeDataService).registerTetheredUser(any(), anyString(), anyString());

        // Act & Assert
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/register/tetheredUser"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .header("accessedBy", "user@email.com")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(tetheredUserRequest)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    void registerTetheredUser_ShouldReturnHttpStatusCode400() throws Exception {
        // Arrange
        doNothing().when(employeeDataService).registerTetheredUser(any(), anyString(), anyString());

        // Act & Assert
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/register/tetheredUser"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .header("accessedBy", "user@email.com")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    void registerTetheredUser_ShouldReturnHttpStatusCode500() throws Exception {
        // Arrange
        var tetheredUserRequest = new TetheredUserRequest(Scheme.GB,
            "company-id",
            List.of(new TetheredGuidDetails("employee-id", "tethered-guid")));
        when(employeeDataService.registerTetheredUser(any(), any(), any())).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/register/tetheredUser"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .header("accessedBy", "user@email.com")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(tetheredUserRequest)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(500);
    }

    @Test
    void resetMemorableWordWithSchemeRequestBody_ShouldReturnHttpStatusCode200() throws Exception {

        ResetMemorableWordRequest memorableWordRequest = new ResetMemorableWordRequest(TestUtil.TETHERED_USER_GUID,
            TestUtil.MEMORABLE_WORD,Scheme.DE);
        ResetMemorableWordResponse response = new ResetMemorableWordResponse("3462");
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(CdhEmployeeDetails.builder()
                .companyAccountId(VALID_COMPANY_ID)
                .employeeAccountId(VALID_EMPLOYEE_ID)
                .userEmail("user-mail")
                .build());
        when(mockPibaAccountService.resetMemorableWord(any())).thenReturn(response);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/piba/account/memorableword?scheme=GB"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(memorableWordRequest)))
                .build();

        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(resp.statusCode()).isEqualTo(200);
    }

    @Test
    void getAccountBalanceV2_ShouldReturnHttpStatusCode200() throws Exception {
        CustomerAccountCurrentBalances customerAccountCurrentBalances = new CustomerAccountCurrentBalances();
        when(mockPibaAccountService.getCustomerAccountCurrentBalance(anyString(), any(), any())).thenReturn(customerAccountCurrentBalances);
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(CdhEmployeeDetails.builder()
                .companyAccountId(VALID_COMPANY_ID)
                .employeeAccountId(VALID_EMPLOYEE_ID)
                .userEmail("user-mail")
                .build());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/v2/piba/account/balance/summary/tether-id/GB"))
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION_HEADER, "Bearer random_token")
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }
}
