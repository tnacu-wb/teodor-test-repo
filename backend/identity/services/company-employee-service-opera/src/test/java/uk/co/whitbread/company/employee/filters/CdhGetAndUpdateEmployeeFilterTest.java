package uk.co.whitbread.company.employee.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.employee.helper.AuthTestHelper.configureAuth0Context;
import static uk.co.whitbread.company.employee.utils.BuildRequests.buildEmployee;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.Employee;
import uk.co.whitbread.company.employee.service.CdhAuthorizationService;
import uk.co.whitbread.company.employee.service.EmployeeService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class CdhGetAndUpdateEmployeeFilterTest {

  private static final String COMPANY_ID = "companyId";
  private static final String EMPLOYEE_ID = "employeeId";
  private static final String AUTHORIZATION = "Bearer token";
  private static final String AUTHORIZATION_PARAM = "Authorization";
  private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "companyAccountId";
  private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeAccountId";
  private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";

  @LocalServerPort
  private int serverPort;

  private HttpClient httpClient;
  private ObjectMapper objectMapper;

  @Autowired
  private TenantRepository tenantRepository;

  @MockitoBean
  private CdhAuthorizationService mockCdhAuthorizationService;
  @MockitoBean
  private EmployeeService mockEmployeeService;
  @MockitoBean
  private TokenService mockTokenService;
  @MockitoBean
  private EmployeeMapper mockEmployeeMapper;
  @MockitoBean
  private JwtDecoder jwtDecoder;

  private CdhEmployeeDetails cdhEmployeeDetails;

  @BeforeEach
  void setUp() {
    configureAuth0Context(tenantRepository, jwtDecoder);
    httpClient = HttpClient.newHttpClient();
    objectMapper = new ObjectMapper().findAndRegisterModules();

    cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
  }

  @ParameterizedTest
  @CsvSource({
      "PUT, true, true, 204",
      "PUT, false, true, 204",
      "PUT, true, false, 204",
      "GET, true, true, 200",
      "GET, false, true, 200",
      "GET, true, false, 200"
  })
  void successfulRequests_shouldReturnExpectedStatus(
      String method,
      boolean sameEmployee,
      boolean superAccessLevelUser,
      int expectedStatus) {
    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID)).thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ID))
        .thenReturn(sameEmployee);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(superAccessLevelUser);
    if ("PUT".equals(method)) {
      doNothing().when(mockEmployeeService).updateCdhEmployee(any(), any(), any(), any(CdhEmployeeDetails.class), any());
      assertStatus(performPutWithAuth(), expectedStatus);
    } else {
      when(mockEmployeeMapper.toEmployee(any(GetEmployeeResponse.class))).thenReturn(new Employee());
      assertStatus(performGetWithAuth(), expectedStatus);
    }
  }

  @Test
  void genericUserGetsOthersDetails_shouldGetUnauthorized() {
    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID)).thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ID)).thenReturn(false);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails)).thenReturn(false);

    assertStatus(performGetWithAuth(), 401);
  }

  @Test
  void missingTokenHeader_shouldGetUnauthorizedResponse() {
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(null)).thenReturn(CdhEmployeeDetails.builder().build());

    assertStatus(performPutWithoutAuth(), 401);
  }

  @Test
  void missingCdhIdsFromToken_shouldGetUnauthorizedResponse() {
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(CdhEmployeeDetails.builder().build());

    assertStatus(performPutWithAuth(), 401);
  }

  @Test
  void incorrectCompany_shouldGetUnauthorizedResponse() {
    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID)).thenReturn(false);

    assertStatus(performPutWithAuth(), 401);
  }

  private int performGetWithAuth() {
    return performGet(AUTHORIZATION);
  }

  private int performPutWithAuth() {
    return performPut(AUTHORIZATION);
  }

  private int performPutWithoutAuth() {
    return performPut(null);
  }

  private int performGet(String authorizationHeader) {
    try {
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
          .uri(URI.create("http://localhost:" + serverPort + "/companies/" + COMPANY_ID + "/employees/" + EMPLOYEE_ID))
          .header("Content-Type", "application/json")
          .GET();

      if (authorizationHeader != null) {
        requestBuilder.header(AUTHORIZATION_PARAM, authorizationHeader);
      }

      return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()).statusCode();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private int performPut(String authorizationHeader) {
    try {
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
          .uri(URI.create("http://localhost:" + serverPort + "/companies/" + COMPANY_ID + "/employees/" + EMPLOYEE_ID))
          .header("Content-Type", "application/json")
          .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(buildEmployee())));

      if (authorizationHeader != null) {
        requestBuilder.header(AUTHORIZATION_PARAM, authorizationHeader);
      }

      return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString()).statusCode();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void assertStatus(int responseStatus, int expectedStatus) {
    assertEquals(expectedStatus, responseStatus);
  }
}
