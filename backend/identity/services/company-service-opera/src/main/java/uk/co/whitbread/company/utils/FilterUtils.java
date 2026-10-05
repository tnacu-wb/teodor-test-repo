package uk.co.whitbread.company.utils;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@Component
@RequiredArgsConstructor
public class FilterUtils {

  public static final String UNAUTHORISED_RESPONSE = "Unauthorized to perform this operation";
  private static final String MISSING_AUTHORIZATION_TOKEN_RESPONSE = "Missing authorization token";

  private final TokenService tokenService;


  public EmployeeHeaderDetails getEmployeeHeaderDetails(HttpServletRequest request) {
    String sessionId = null;
    String companyId = null;
    String employeeId = null;

    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (isNotBlank(authorization)) {
      sessionId = tokenService.retrieveAndVerifyToken(authorization).orElse(null);
      EmployeeDetails employeeDetails = tokenService.retrieveEmployeeDetailsAndVerifyToken(authorization);
      companyId = employeeDetails.getCompanyId();
      employeeId = employeeDetails.getEmployeeId();
    }
    if (isBlank(sessionId)) {
      sessionId = request.getHeader("session-id");
    }
    if (isBlank(companyId)) {
      companyId = request.getHeader("company-id");
    }
    if (isBlank(employeeId)) {
      employeeId = request.getHeader("employee-id");
    }
    return EmployeeHeaderDetails.builder()
        .sessionId(sessionId)
        .companyId(companyId)
        .employeeId(employeeId)
        .build();
  }

  public Optional<CdhEmployeeDetails> getCdhIdsFromToken(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    String authorization = request.getHeader("Authorization");
    if (StringUtils.isEmpty(authorization)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          MISSING_AUTHORIZATION_TOKEN_RESPONSE);
      return Optional.empty();
    }
    CdhEmployeeDetails tokenDetails;
    try {
      tokenDetails = tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
    } catch (TokenVerificationException ex) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
      return Optional.empty();
    }
    if (StringUtils.isEmpty(tokenDetails.getCompanyAccountId())
        || StringUtils.isEmpty(tokenDetails.getEmployeeAccountId())) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORISED_RESPONSE);
      return Optional.empty();
    }
    return Optional.of(tokenDetails);
  }

  public static String getCompanyIdPathParam(HttpServletRequest request) {
    String path = request.getServletPath();
    return path.split("/")[2];
  }

  public static boolean isGetRegistrationQuestionsEndpoint(HttpServletRequest request) {
    String path = request.getServletPath();
    return request.getMethod().equals("GET")
        && path.matches("^/companies/.{1,50}/employees/.{1,50}/registration-questions$");
  }

  public static boolean isGetRegistrationQuestionsByCompanyIdEndpoint(HttpServletRequest request) {
    String path = request.getServletPath();
    return request.getMethod().equals("GET")
        && path.matches("^/companies/.{1,50}/registration-questions$");
  }
}
