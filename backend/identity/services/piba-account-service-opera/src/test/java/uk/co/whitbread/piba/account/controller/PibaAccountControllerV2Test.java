package uk.co.whitbread.piba.account.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.whitbread.piba.account.helper.AuthTestHelper;
import uk.co.whitbread.piba.account.model.TransactionsFileResponse;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.service.PibaAccountService;
import uk.co.whitbread.piba.account.util.WorldlineUtils;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PibaAccountControllerV2Test {
    @LocalServerPort
    int serverPort;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String VALID_COMPANY_ID = "55";
    private static final String VALID_EMPLOYEE_ID = "22";

    @MockitoBean
    private PibaAccountService mockPibaAccountService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CacheManager cacheManager;

    @MockitoBean
    private WorldlineUtils worldlineUtils;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private TenantRepository tenantRepository;

    private final ByteArrayOutputStream transactionListOutputStream = new ByteArrayOutputStream();

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
    void downloadTransactionsV2_ShouldReturnHttpStatusCode200WithCorrectHeaders() throws Exception {
        String token = "Bearer random_token";
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(CdhEmployeeDetails.builder()
                .companyAccountId(VALID_COMPANY_ID)
                .employeeAccountId(VALID_EMPLOYEE_ID)
                .userEmail("user-mail")
                .build());
        when(worldlineUtils.getClientIp(any())).thenReturn("1.1.1.1");
        when(mockPibaAccountService.downloadTransactionsForInvoice(132,"92562846-62b5-42d8-b393-132a1d78fd1a", 222, token, "1.1.1.1", Scheme.DE))
                .thenReturn(new TransactionsFileResponse("filenameTest.xml", transactionListOutputStream));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/v2/piba/account/transactions/download/132/92562846-62b5-42d8-b393-132a1d78fd1a/222?scheme=DE"))
                .header("Content-Type", "application/octet-stream")
                .header(AUTHORIZATION_HEADER, token)
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void downloadTransactionsV2_ShouldReturnHttpStatusCode200ForGbWhenSchemeIsEmpty() throws Exception {
        String token = "Bearer random_token";
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(CdhEmployeeDetails.builder()
                .companyAccountId(VALID_COMPANY_ID)
                .employeeAccountId(VALID_EMPLOYEE_ID)
                .userEmail("user-mail")
                .build());
        when(worldlineUtils.getClientIp(any())).thenReturn("1.1.1.1");
        when(mockPibaAccountService.downloadTransactionsForInvoice(132,"92562846-62b5-42d8-b393-132a1d78fd1a", 222, token, "1.1.1.1", Scheme.GB))
            .thenReturn(new TransactionsFileResponse("", transactionListOutputStream));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + "/v2/piba/account/transactions/download/132/92562846-62b5-42d8-b393-132a1d78fd1a/222"))
                .header("Content-Type", "application/octet-stream")
                .header(AUTHORIZATION_HEADER, token)
                .GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        verify(mockPibaAccountService, times(1)).downloadTransactionsForInvoice(
            132,"92562846-62b5-42d8-b393-132a1d78fd1a", 222, token, "1.1.1.1", Scheme.GB);
    }

   @Test
   void downloadTransactionsForAccountV2_ShouldReturnHttpStatusCode200WithCorrectHeaders() throws Exception {
       String token = "Bearer random_token";
       int schemeCustomerId = 132;
       String tetheredUserGuid = "92562846-62b5-42d8-b393-132a1d78fd1a";
       String expectedFilename = "transactionsTest.csv";
       ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
       TransactionsFileResponse response = new TransactionsFileResponse(expectedFilename, outputStream);

       when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
           .thenReturn(CdhEmployeeDetails.builder()
               .companyAccountId(VALID_COMPANY_ID)
               .employeeAccountId(VALID_EMPLOYEE_ID)
               .userEmail("user-mail")
               .build());
       when(mockPibaAccountService.downloadTransactionsForAccountV2(
               schemeCustomerId, tetheredUserGuid, Scheme.GB, token))
           .thenReturn(response);

       HttpRequest request = HttpRequest.newBuilder()
               .uri(URI.create(baseUrl() + "/v2/piba/account/transactions/download/" + schemeCustomerId + "/" + tetheredUserGuid + "?scheme=GB"))
               .header("Content-Type", "application/octet-stream")
               .header(AUTHORIZATION_HEADER, token)
               .GET().build();

       HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
       assertThat(resp.statusCode()).isEqualTo(200);
   }

}
