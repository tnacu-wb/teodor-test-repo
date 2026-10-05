package uk.co.whitbread.company.employee.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.employee.helper.AuthTestHelper.configureAuth0Context;
import static uk.co.whitbread.company.employee.utils.Auth0ManagementTransformer.ACTIVATION_KEY_LABEL;
import static uk.co.whitbread.company.employee.utils.Auth0ManagementTransformer.EMPLOYEE_STATUS_LABEL;
import static uk.co.whitbread.company.employee.utils.BuildRequests.buildEmployee;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.company.employee.exceptions.EmployeeNotFoundException;
import uk.co.whitbread.company.employee.mapper.EmployeeMapper;
import uk.co.whitbread.company.employee.model.AccessLevel;
import uk.co.whitbread.company.employee.model.BookingPreference;
import uk.co.whitbread.company.employee.model.Employee;
import uk.co.whitbread.company.employee.model.EmployeeStatus;
import uk.co.whitbread.company.employee.model.GetEmployeeActivationResponse;
import uk.co.whitbread.company.employee.model.GetEmployeeResponse;
import uk.co.whitbread.company.employee.model.GetEmployeesRequest;
import uk.co.whitbread.company.employee.model.GetEmployeesResponse;
import uk.co.whitbread.company.employee.model.InviteRequest;
import uk.co.whitbread.company.employee.model.UpdateAccessLevelRequest;
import uk.co.whitbread.company.employee.service.Auth0Service;
import uk.co.whitbread.company.employee.service.CdhAuthorizationService;
import uk.co.whitbread.company.employee.service.EmployeeActivationService;
import uk.co.whitbread.company.employee.service.EmployeeProfileService;
import uk.co.whitbread.company.employee.service.EmployeeService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class CdhEmployeeControllerTest {

  private static final String COMPANY_ID = "companyId";
  private static final String EMPLOYEE_ID = "employeeId";
  private static final String AUTHORIZATION = "Bearer token";
  private static final String AUTHORIZATION_PARAM = "Authorization";
  private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "companyAccountId";
  private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeAccountId";
  private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";
  private static final String ACTIVATION_KEY = "activationKey";
  private static final String ACTIVATION_KEY_PARAM = "activation-key";
  private static final String LANGUAGE = "en";
  private static final String LANGUAGE_PARAM = "language";
  private static final String IS_INNBUSINESS_PARAM = "innBusiness";
  private static final boolean IS_INNBUSINESS = true;
  private static final String TRAVEL_MANAGER_ACTIVATION_ENDPOINT = "/companies/activation";
  private static final String SIZE_PARAM = "size";
  private static final String PAGE_PARAM = "page";
  private static final String ACTIVATION_DETAILS_ENDPOINT = "/companies/employees/activation-details";

  @LocalServerPort
  private int serverPort;

  private HttpClient httpClient;
  private ObjectMapper objectMapper;

  @MockitoBean
  private EmployeeService mockEmployeeService;
  @MockitoBean
  private EmployeeActivationService mockEmployeeActivationService;
  @MockitoBean
  private CdhAuthorizationService mockCdhAuthorizationService;
  @MockitoBean
  private TokenService mockTokenService;
  @MockitoBean
  private Auth0Service mockAuth0Service;
  @MockitoBean
  private EmployeeMapper mockEmployeeMapper;
  @MockitoBean
  private EmployeeProfileService mockEmployeeProfileService;
  @MockitoBean
  private JwtDecoder jwtDecoder;
  @Autowired
  private TenantRepository tenantRepository;

  private CdhEmployeeDetails globalCdhEmployeeDetails;

  @BeforeEach
  void setUp() {
    httpClient = HttpClient.newHttpClient();
    objectMapper = new ObjectMapper().findAndRegisterModules();
    configureAuth0Context(tenantRepository, jwtDecoder);
    globalCdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .accessLevel(AccessLevel.SUPER.name())
        .build();
  }

  @Test
  void getCdhEmployee_shouldGetOKResponse() throws Exception {
    GetEmployeeResponse expectedResponse = objectMapper.readValue(
        new File("src/test/resources/mapping/GetEmployeeResponse.json"),
        GetEmployeeResponse.class);
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ID))
        .thenReturn(false);
    when(mockEmployeeMapper.toEmployee(any(uk.co.whitbread.shared.cdh.model.GetEmployeeResponse.class)))
        .thenReturn(expectedResponse.getEmployee());

    assertStatus(sendRequest("GET", businessEmployeePath(), AUTHORIZATION, Map.of(), null), HttpStatus.SC_OK);
  }

  @Test
  void getCdhEmployee_shouldGetNotFoundResponse() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ID))
        .thenReturn(false);
    when(mockEmployeeService.getCdhEmployee(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
        .thenThrow(new EmployeeNotFoundException("Employee not found"));

    assertStatus(sendRequest("GET", businessEmployeePath(), AUTHORIZATION, Map.of(), null), HttpStatus.SC_NOT_FOUND);
  }

  @Test
  void activateTravelManagerInCdh_shouldGetOKResponse() throws Exception {
    GetEmployeeResponse expectedResponse = objectMapper.readValue(
        new File("src/test/resources/mapping/GetEmployeeResponse.json"),
        GetEmployeeResponse.class);

    when(mockEmployeeActivationService.activateTravelManagerInCdh(ACTIVATION_KEY))
        .thenReturn(expectedResponse.getEmployee());

    assertStatus(sendRequest(
        "POST",
        TRAVEL_MANAGER_ACTIVATION_ENDPOINT,
        null,
        Map.of(),
        null,
        Map.of(ACTIVATION_KEY_PARAM, ACTIVATION_KEY)), HttpStatus.SC_OK);

    Employee employee = expectedResponse.getEmployee();
    Map<String, Object> appMetadata = new HashMap<>(Map.of(EMPLOYEE_STATUS_LABEL, EmployeeStatus.ACTIVE));
    appMetadata.put(ACTIVATION_KEY_LABEL, null);
    verify(mockAuth0Service).updateAppMetadata(employee.getEmailAddress(), appMetadata);
  }

  @Test
  void activateTravelManagerInCdh_shouldGetNotFoundResponse() {
    when(mockEmployeeActivationService.activateTravelManagerInCdh(ACTIVATION_KEY))
        .thenThrow(new EmployeeNotFoundException("Activation key is not valid"));

    assertStatus(sendRequest(
        "POST",
        TRAVEL_MANAGER_ACTIVATION_ENDPOINT,
        null,
        Map.of(),
        null,
        Map.of(ACTIVATION_KEY_PARAM, ACTIVATION_KEY)), HttpStatus.SC_NOT_FOUND);

    verify(mockAuth0Service, times(0)).updateAppMetadata(anyString(), anyMap());
  }

  @Test
  void updateCdhEmployee_shouldGetNoContentResponse() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();
    Employee employee = buildEmployee();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ID))
        .thenReturn(false);

    assertStatus(sendRequest(
        "PUT",
        businessEmployeePath(),
        AUTHORIZATION,
        Map.of(LANGUAGE_PARAM, LANGUAGE),
        employee), HttpStatus.SC_NO_CONTENT);

    verify(mockEmployeeService)
        .updateCdhEmployee(COMPANY_ID, EMPLOYEE_ID, employee, cdhEmployeeDetails, LANGUAGE);
  }

  @Test
  void activateCdhEmployee_shouldGetNoContentResponse() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();
    Employee employee = buildEmployee();

    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ID))
        .thenReturn(false);

    assertStatus(sendRequest(
        "PUT",
        businessEmployeePath(),
        null,
        Map.of(),
        employee,
        Map.of(ACTIVATION_KEY_PARAM, ACTIVATION_KEY)), HttpStatus.SC_NO_CONTENT);

    verify(mockEmployeeService).activateCdhEmployee(ACTIVATION_KEY, employee, EMPLOYEE_ID);
  }

  @Test
  void getEmployeeBookingPreferencesFromCdh_shouldReturnOK() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();

    BookingPreference bookingPreference = BookingPreference.builder()
        .mealDeal(true)
        .children(2)
        .premierBreakfast(false)
        .adults(2)
        .build();

    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(false);
    when(mockCdhAuthorizationService.isSameEmployee(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ID))
        .thenReturn(true);
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION)).thenReturn(cdhEmployeeDetails);
    when(mockEmployeeProfileService.getCdhEmployeeBookingPreferences(COMPANY_ID, EMPLOYEE_ID, USER_EMAIL_FROM_TOKEN))
        .thenReturn(bookingPreference);

    HttpResponse<String> response = sendRequest("GET", bookingPreferencesPath(), AUTHORIZATION, Map.of(), null);
    assertStatus(response, HttpStatus.SC_OK);

    JsonNode responseBody = readBody(response);
    assertEquals(2, responseBody.get("adults").asInt());
    assertEquals(true, responseBody.get("mealDeal").asBoolean());
    assertEquals(2, responseBody.get("children").asInt());
    assertFalse(responseBody.get("premierBreakfast").asBoolean());
  }

  @Test
  void inviteEmployee_ShouldReturnCREATED() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();

    InviteRequest inviteRequest = new InviteRequest();
    inviteRequest.setEmailAddress(USER_EMAIL_FROM_TOKEN);

    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ID))
        .thenReturn(false);
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION)).thenReturn(cdhEmployeeDetails);

    assertStatus(sendRequest(
        "POST",
        inviteEmployeePath(),
        AUTHORIZATION,
        Map.of(LANGUAGE_PARAM, LANGUAGE),
        inviteRequest), HttpStatus.SC_CREATED);

    verify(mockEmployeeService).inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE);
  }

  @Test
  void inviteInnBEmployee_ShouldReturnCREATED() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();

    InviteRequest inviteRequest = new InviteRequest();
    inviteRequest.setEmailAddress(USER_EMAIL_FROM_TOKEN);
    inviteRequest.setInnBusiness(true);

    when(mockCdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, EMPLOYEE_ID))
        .thenReturn(false);
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION)).thenReturn(cdhEmployeeDetails);

    assertStatus(sendRequest(
        "POST",
        inviteEmployeePath(),
        AUTHORIZATION,
        Map.of(LANGUAGE_PARAM, LANGUAGE, IS_INNBUSINESS_PARAM, String.valueOf(IS_INNBUSINESS)),
        inviteRequest), HttpStatus.SC_CREATED);

    verify(mockEmployeeService).inviteCdhEmployee(COMPANY_ID, inviteRequest, cdhEmployeeDetails, LANGUAGE);
  }

  @Test
  void updateEmployeeAccessLevel_shouldReturnNoContent() {
    UpdateAccessLevelRequest request = new UpdateAccessLevelRequest();
    request.setAccessLevel(AccessLevel.BOOKER);

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(globalCdhEmployeeDetails);
    when(mockCdhAuthorizationService.isSameCompany(any(), any())).thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(any(), any())).thenReturn(true);

    assertStatus(sendRequest(
        "PATCH",
        updateAccessLevelPath(),
        AUTHORIZATION,
        Map.of(LANGUAGE_PARAM, LANGUAGE),
        request), HttpStatus.SC_NO_CONTENT);

    verify(mockEmployeeService).updateCdhEmployeeAccessLevel(
        COMPANY_ID, EMPLOYEE_ID, request.getAccessLevel(), USER_EMAIL_FROM_TOKEN, LANGUAGE);
  }

  @Test
  void updateEmployeeAccessLevel_shouldReturnUnauthorized() {
    UpdateAccessLevelRequest request = new UpdateAccessLevelRequest();
    request.setAccessLevel(AccessLevel.BOOKER);

    assertStatus(sendRequest(
        "PATCH",
        updateAccessLevelPath(),
        null,
        Map.of(LANGUAGE_PARAM, LANGUAGE),
        request), HttpStatus.SC_UNAUTHORIZED);

    verify(mockEmployeeService, times(0)).updateCdhEmployeeAccessLevel(any(), any(), any(), any(), any());
  }

  @Test
  void updateEmployeeAccessLevel_shouldReturnBadRequest() {
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(globalCdhEmployeeDetails);
    when(mockCdhAuthorizationService.isSameCompany(any(), any())).thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(any(), any())).thenReturn(true);

    assertStatus(sendRequest(
        "PATCH",
        updateAccessLevelPath(),
        AUTHORIZATION,
        Map.of(LANGUAGE_PARAM, LANGUAGE),
        "{}"), HttpStatus.SC_BAD_REQUEST);

    verify(mockEmployeeService, times(0)).updateCdhEmployeeAccessLevel(any(), any(), any(), any(), any());
  }

  @Test
  void getEmployees_shouldReturnOK() {
    GetEmployeesResponse response = new GetEmployeesResponse();
    response.setEmployees(List.of());

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(globalCdhEmployeeDetails);
    when(mockCdhAuthorizationService.isSameCompany(any(), any())).thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(any(), any())).thenReturn(true);
    when(mockEmployeeService.getEmployeesFromCdh(any(GetEmployeesRequest.class), eq(COMPANY_ACCOUNT_ID_FROM_TOKEN),
        eq(USER_EMAIL_FROM_TOKEN))).thenReturn(response);

    assertStatus(sendRequest(
        "GET",
        employeesPath(COMPANY_ACCOUNT_ID_FROM_TOKEN),
        AUTHORIZATION,
        Map.of(),
        null,
        Map.of(SIZE_PARAM, "10", PAGE_PARAM, "1")), HttpStatus.SC_OK);

    verify(mockEmployeeService).getEmployeesFromCdh(
        any(GetEmployeesRequest.class), eq(COMPANY_ACCOUNT_ID_FROM_TOKEN), eq(USER_EMAIL_FROM_TOKEN));
  }

  @Test
  void getEmployees_shouldReturnUnauthorized_whenMissingToken() {
    assertStatus(sendRequest(
        "GET",
        employeesPath(COMPANY_ID),
        null,
        Map.of(),
        null,
        Map.of(SIZE_PARAM, "10", PAGE_PARAM, "1")), HttpStatus.SC_UNAUTHORIZED);

    verify(mockEmployeeService, times(0)).getEmployeesFromCdh(any(), any(), any());
  }

  @Test
  void getEmployees_shouldReturnUnauthorized_whenForbidden() {
    CdhEmployeeDetails forbiddenDetails = CdhEmployeeDetails.builder()
        .companyAccountId("otherCompany")
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .accessLevel("NONE")
        .build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(forbiddenDetails);
    when(mockCdhAuthorizationService.isSameCompany(any(), any())).thenReturn(true);
    when(mockCdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);
    when(mockCdhAuthorizationService.isSameEmployee(any(), any())).thenReturn(true);

    assertStatus(sendRequest(
        "GET",
        employeesPath(COMPANY_ID),
        AUTHORIZATION,
        Map.of(),
        null,
        Map.of(SIZE_PARAM, "10", PAGE_PARAM, "1")), HttpStatus.SC_UNAUTHORIZED);

    verify(mockEmployeeService, times(0)).getEmployeesFromCdh(any(), any(), any());
  }

  @Test
  void getActivationDetails_shouldReturnOK() {
    GetEmployeeActivationResponse activationResponse = new GetEmployeeActivationResponse();
    Employee employee = buildEmployee();
    activationResponse.setEmployee(employee);
    activationResponse.setSessionId("session123");
    activationResponse.setCompanyId(COMPANY_ID);

    when(mockEmployeeActivationService.getEmployeeActivationDetails(ACTIVATION_KEY))
        .thenReturn(activationResponse);

    HttpResponse<String> response = sendRequest(
        "GET",
        ACTIVATION_DETAILS_ENDPOINT,
        null,
        Map.of(),
        null,
        Map.of(ACTIVATION_KEY_PARAM, ACTIVATION_KEY));
    assertStatus(response, HttpStatus.SC_OK);
    assertEquals(employee.getEmailAddress(), readBody(response).get("emailAddress").asText());

    verify(mockEmployeeActivationService).getEmployeeActivationDetails(ACTIVATION_KEY);
  }

  @Test
  void getActivationDetails_shouldReturnNotFound() {
    when(mockEmployeeActivationService.getEmployeeActivationDetails(ACTIVATION_KEY))
        .thenThrow(new EmployeeNotFoundException("Activation key not found"));

    assertStatus(sendRequest(
        "GET",
        ACTIVATION_DETAILS_ENDPOINT,
        null,
        Map.of(),
        null,
        Map.of(ACTIVATION_KEY_PARAM, ACTIVATION_KEY)), HttpStatus.SC_NOT_FOUND);

    verify(mockEmployeeActivationService).getEmployeeActivationDetails(ACTIVATION_KEY);
  }

  @Test
  void getActivationDetails_shouldReturnBadRequest_whenMissingActivationKey() {
    assertStatus(sendRequest("GET", ACTIVATION_DETAILS_ENDPOINT, null, Map.of(), null), HttpStatus.SC_BAD_REQUEST);

    verify(mockEmployeeActivationService, times(0)).getEmployeeActivationDetails(anyString());
  }

  private String businessEmployeePath() {
    return "/companies/" + COMPANY_ID + "/employees/" + EMPLOYEE_ID;
  }

  private String bookingPreferencesPath() {
    return businessEmployeePath() + "/bookingpreferences";
  }

  private String inviteEmployeePath() {
    return "/companies/admin/" + COMPANY_ID + "/employees/invite";
  }

  private String updateAccessLevelPath() {
    return "/companies/admin/" + COMPANY_ID + "/employees/" + EMPLOYEE_ID + "/accesslevel";
  }

  private String employeesPath(String companyId) {
    return "/companies/" + companyId + "/employees";
  }

  private HttpResponse<String> sendRequest(String method, String path, String authorization,
      Map<String, String> headers, Object body) {
    return sendRequest(method, path, authorization, headers, body, Map.of());
  }

  private HttpResponse<String> sendRequest(String method, String path, String authorization,
      Map<String, String> headers, Object body, Map<String, String> queryParams) {
    try {
      HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
          .uri(URI.create("http://localhost:" + serverPort + appendQueryParams(path, queryParams)))
          .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);

      if (authorization != null) {
        requestBuilder.header(AUTHORIZATION_PARAM, authorization);
      }

      for (Map.Entry<String, String> header : headers.entrySet()) {
        requestBuilder.header(header.getKey(), header.getValue());
      }

      if (body == null) {
        requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
      } else if (body instanceof String rawBody) {
        requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(rawBody));
      } else {
        requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
      }

      return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String appendQueryParams(String path, Map<String, String> queryParams) {
    if (queryParams.isEmpty()) {
      return path;
    }

    String query = queryParams.entrySet().stream()
        .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
            + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
        .reduce((left, right) -> left + "&" + right)
        .orElse("");
    return path + "?" + query;
  }

  private JsonNode readBody(HttpResponse<String> response) {
    try {
      return objectMapper.readTree(response.body());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void assertStatus(HttpResponse<String> response, int expectedStatus) {
    assertEquals(expectedStatus, response.statusCode());
  }
}
