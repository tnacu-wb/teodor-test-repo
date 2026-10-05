package uk.co.whitbread.company.employee.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.service.TokenService;

@ExtendWith(MockitoExtension.class)
class FilterUtilsTest {

  @Mock
  private TokenService tokenService;

  @Test
  void getCdhIdsFromToken_shouldReturnEmptyWhenAuthorizationHeaderMissing() throws IOException {
    FilterUtils filterUtils = new FilterUtils(tokenService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    var result = filterUtils.getCdhIdsFromToken(request, response);

    assertTrue(result.isEmpty());
    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    assertEquals("Missing authorization token", response.getErrorMessage());
    verifyNoInteractions(tokenService);
  }

  @Test
  void getCdhIdsFromToken_shouldReturnEmptyWhenTokenVerificationFails() throws IOException {
    FilterUtils filterUtils = new FilterUtils(tokenService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.addHeader("Authorization", "Bearer token");
    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken("Bearer token"))
        .thenThrow(new TokenVerificationException("Token invalid"));

    var result = filterUtils.getCdhIdsFromToken(request, response);

    assertTrue(result.isEmpty());
    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    assertEquals("Token invalid", response.getErrorMessage());
  }

  @Test
  void getCdhIdsFromToken_shouldReturnEmptyWhenCompanyOrEmployeeIdMissing() throws IOException {
    FilterUtils filterUtils = new FilterUtils(tokenService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.addHeader("Authorization", "Bearer token");
    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken("Bearer token"))
        .thenReturn(CdhEmployeeDetails.builder()
            .companyAccountId("")
            .employeeAccountId("employee-id")
            .userEmail("user@test.com")
            .build());

    var result = filterUtils.getCdhIdsFromToken(request, response);

    assertTrue(result.isEmpty());
    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    assertEquals(FilterUtils.UNAUTHORISED_RESPONSE, response.getErrorMessage());
  }

  @Test
  void getCdhIdsFromToken_shouldReturnTokenDetailsWhenIdsPresent() throws IOException {
    FilterUtils filterUtils = new FilterUtils(tokenService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    CdhEmployeeDetails tokenDetails = CdhEmployeeDetails.builder()
        .companyAccountId("company-id")
        .employeeAccountId("employee-id")
        .userEmail("user@test.com")
        .build();
    request.addHeader("Authorization", "Bearer token");
    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken("Bearer token")).thenReturn(tokenDetails);

    var result = filterUtils.getCdhIdsFromToken(request, response);

    assertTrue(result.isPresent());
    assertEquals(tokenDetails, result.get());
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
  }

  @Test
  void getEmployeeHeaderDetails_shouldUseTokenValuesWhenAuthorizationPresent() {
    FilterUtils filterUtils = new FilterUtils(tokenService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer token");
    request.addHeader("session-id", "header-session");
    request.addHeader("company-id", "header-company");
    request.addHeader("employee-id", "header-employee");
    when(tokenService.retrieveAndVerifyToken("Bearer token")).thenReturn(Optional.of("token-session"));
    when(tokenService.retrieveEmployeeDetailsAndVerifyToken("Bearer token"))
        .thenReturn(new EmployeeDetails("token-company", "token-employee"));

    EmployeeHeaderDetails result = filterUtils.getEmployeeHeaderDetails(request);

    assertEquals("token-session", result.getSessionId());
    assertEquals("token-company", result.getCompanyId());
    assertEquals("token-employee", result.getEmployeeId());
  }

  @Test
  void getEmployeeHeaderDetails_shouldFallbackToHeadersWhenTokenValuesBlank() {
    FilterUtils filterUtils = new FilterUtils(tokenService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer token");
    request.addHeader("session-id", "header-session");
    request.addHeader("company-id", "header-company");
    request.addHeader("employee-id", "header-employee");
    when(tokenService.retrieveAndVerifyToken("Bearer token")).thenReturn(Optional.empty());
    when(tokenService.retrieveEmployeeDetailsAndVerifyToken("Bearer token"))
        .thenReturn(new EmployeeDetails("", null));

    EmployeeHeaderDetails result = filterUtils.getEmployeeHeaderDetails(request);

    assertEquals("header-session", result.getSessionId());
    assertEquals("header-company", result.getCompanyId());
    assertEquals("header-employee", result.getEmployeeId());
  }

  @Test
  void getEmployeeHeaderDetails_shouldUseHeadersWhenAuthorizationMissing() {
    FilterUtils filterUtils = new FilterUtils(tokenService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("session-id", "header-session");
    request.addHeader("company-id", "header-company");
    request.addHeader("employee-id", "header-employee");

    EmployeeHeaderDetails result = filterUtils.getEmployeeHeaderDetails(request);

    assertEquals("header-session", result.getSessionId());
    assertEquals("header-company", result.getCompanyId());
    assertEquals("header-employee", result.getEmployeeId());
    verifyNoInteractions(tokenService);
  }

  @Test
  void getCompanyIdPathParam_shouldReturnCompanySegment() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath("/companies/company-id/employees/employee-id");

    assertEquals("company-id", FilterUtils.getCompanyIdPathParam(request));
  }

  @Test
  void getEmployeeIdPathParam_shouldReturnEmployeeSegment() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setServletPath("/companies/company-id/employees/employee-id");

    assertEquals("employee-id", FilterUtils.getEmployeeIdPathParam(request));
  }
}
