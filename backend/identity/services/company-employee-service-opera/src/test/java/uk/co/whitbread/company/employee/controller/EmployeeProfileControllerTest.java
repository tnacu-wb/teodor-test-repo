package uk.co.whitbread.company.employee.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.employee.helper.AuthTestHelper.configureAuth0Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.company.employee.model.BookingPreference;
import uk.co.whitbread.company.employee.model.UpdateRegistrationRequest;
import uk.co.whitbread.company.employee.service.CdhAuthorizationService;
import uk.co.whitbread.company.employee.service.EmployeeProfileService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class EmployeeProfileControllerTest {

  private static final String COMPANY_ID = "companyId";
  private static final String EMPLOYEE_ID = "employeeId";
  private static final String AUTHORIZATION = "Bearer token";
  private static final String AUTHORIZATION_PARAM = "Authorization";
  private static final String LANGUAGE = "en";
  private static final String LANGUAGE_PARAM = "language";
  private static final String COUNTRY = "gb";
  private static final String COUNTRY_PARAM = "country";

  @LocalServerPort
  private int serverPort;

  private HttpClient httpClient;
  private ObjectMapper objectMapper;

  @MockitoBean
  private EmployeeProfileService mockEmployeeProfileService;

  @MockitoBean
  private TokenService mockTokenService;

  @MockitoBean
  private CdhAuthorizationService mockCdhAuthorizationService;
  @MockitoBean
  private JwtDecoder jwtDecoder;
  @Autowired
  private TenantRepository tenantRepository;

  private CdhEmployeeDetails cdhEmployeeDetails;

  @BeforeEach
  void setUp() {
    httpClient = HttpClient.newHttpClient();
    objectMapper = new ObjectMapper().findAndRegisterModules();
    configureAuth0Context(tenantRepository, jwtDecoder);
    cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ID)
        .employeeAccountId(EMPLOYEE_ID)
        .userEmail("user@company.com")
        .build();
    when(mockCdhAuthorizationService.isSameCompany(any(), any())).thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(any(), any())).thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);
  }

  @Test
  void getEmployeeBookingPreferences_shouldReturnUnauthorized() {
    assertStatus(sendRequest("GET", bookingPreferencesPath(), null, Map.of(), null),
        HttpStatus.SC_UNAUTHORIZED);

    verify(mockEmployeeProfileService, times(0)).getCdhEmployeeBookingPreferences(any(), any(), any());
  }

  @Test
  void updateEmployeeBookingPreferences_shouldReturnNoContent() {
    BookingPreference bookingPreference = BookingPreference.builder()
        .adults(2)
        .children(1)
        .cotRequired(false)
        .mealDeal(true)
        .premierBreakfast(false)
        .build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);

    assertStatus(sendRequest("PATCH", bookingPreferencesPath(), AUTHORIZATION, Map.of(), bookingPreference),
        HttpStatus.SC_NO_CONTENT);

    verify(mockEmployeeProfileService).updateCdhEmployeeBookingPreferences(
        COMPANY_ID, EMPLOYEE_ID, bookingPreference, cdhEmployeeDetails.getUserEmail());
  }

  @Test
  void updateEmployeeBookingPreferences_shouldReturnUnauthorized() {
    assertStatus(sendRequest("PATCH", bookingPreferencesPath(), null, Map.of(), null),
        HttpStatus.SC_UNAUTHORIZED);

    verify(mockEmployeeProfileService, times(0)).updateCdhEmployeeBookingPreferences(any(), any(), any(), any());
  }

  @Test
  void approveRejectEmployee_shouldReturnNoContent() {
    UpdateRegistrationRequest payload = new UpdateRegistrationRequest();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);

    assertStatus(sendRequest(
        "PATCH",
        approveRejectPath(),
        AUTHORIZATION,
        Map.of(LANGUAGE_PARAM, LANGUAGE, COUNTRY_PARAM, COUNTRY),
        payload),
        HttpStatus.SC_NO_CONTENT);

    verify(mockEmployeeProfileService).updateCdhRegistration(payload, cdhEmployeeDetails, LANGUAGE);
  }

  @Test
  void approveRejectEmployee_shouldReturnUnauthorized() {
    assertStatus(sendRequest(
        "PATCH",
        approveRejectPath(),
        null,
        Map.of(LANGUAGE_PARAM, LANGUAGE, COUNTRY_PARAM, COUNTRY),
        null),
        HttpStatus.SC_UNAUTHORIZED);

    verify(mockEmployeeProfileService, times(0)).updateCdhRegistration(any(), any(), any());
  }

  private String bookingPreferencesPath() {
    return "/companies/" + COMPANY_ID + "/employees/" + EMPLOYEE_ID + "/bookingpreferences";
  }

  private String approveRejectPath() {
    return "/companies/admin/" + COMPANY_ID + "/employees/approvereject";
  }

  private HttpResponse<String> sendRequest(String method, String path, String authorization,
      Map<String, String> extraHeaders, Object body) {
    try {
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
          .uri(URI.create("http://localhost:" + serverPort + path))
          .header("Content-Type", "application/json");

      if (authorization != null) {
        requestBuilder.header(AUTHORIZATION_PARAM, authorization);
      }

      Map<String, String> headers = new HashMap<>(extraHeaders);
      for (Map.Entry<String, String> header : headers.entrySet()) {
        requestBuilder.header(header.getKey(), header.getValue());
      }

      if (body == null) {
        requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
      } else if (body instanceof String rawBody) {
        requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(rawBody));
      } else {
        requestBuilder.method(method,
            HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
      }

      return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void assertStatus(HttpResponse<String> response, int expectedStatus) {
    assertEquals(expectedStatus, response.statusCode());
  }
}
