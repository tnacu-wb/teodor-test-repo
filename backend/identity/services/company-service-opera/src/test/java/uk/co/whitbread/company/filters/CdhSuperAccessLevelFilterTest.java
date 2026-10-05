package uk.co.whitbread.company.filters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.whitbread.company.service.cdh.CdhAuthorizationService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("testCdh")
class CdhSuperAccessLevelFilterTest {

  private static final String COMPANY_ID = "125";
  private static final String EMPLOYEE_ID = "1";
  private static final String AUTHORIZATION = "Bearer token";
  private static final String AUTHORIZATION_PARAM = "Authorization";
  private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "companyAccountId";
  private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeAccountId";
  private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";
  private CdhEmployeeDetails cdhEmployeeDetails;

  @LocalServerPort
  private int serverPort;

  @MockitoBean
  private CdhAuthorizationService mockCdhAuthorizationService;
  @MockitoBean
  private TokenService mockTokenService;

  private final HttpClient httpClient = HttpClient.newHttpClient();

  private String registrationQuestionsUrl() {
    return "http://localhost:" + serverPort + "/companies/" + COMPANY_ID + "/employees/" + EMPLOYEE_ID + "/registration-questions";
  }

  @BeforeEach
  void setUp() {
    cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN).build();
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
  }

  @Test
  void userDoesNotHaveSuperLevelAccess_shouldGetUnauthorizedResponse() throws Exception {
    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(false);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(registrationQuestionsUrl()))
        .header("Content-Type", "application/json")
        .header(AUTHORIZATION_PARAM, AUTHORIZATION)
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(401);
  }

  @Test
  void missingTokenHeader_shouldGetUnauthorizedResponse() throws Exception {
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(null))
        .thenReturn(CdhEmployeeDetails.builder().build());

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(registrationQuestionsUrl()))
        .header("Content-Type", "application/json")
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(401);
  }

  @Test
  void missingCdhIdsFromToken_shouldGetUnauthorizedResponse() throws Exception {
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(CdhEmployeeDetails.builder().build());

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(registrationQuestionsUrl()))
        .header("Content-Type", "application/json")
        .header(AUTHORIZATION_PARAM, AUTHORIZATION)
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(401);
  }

  @Test
  void incorrectCompany_shouldGetUnauthorizedResponse() throws Exception {
    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(false);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(registrationQuestionsUrl()))
        .header("Content-Type", "application/json")
        .header(AUTHORIZATION_PARAM, AUTHORIZATION)
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(401);
  }
}
