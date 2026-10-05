package uk.co.whitbread.company.employee.filters;

import static uk.co.whitbread.company.employee.utils.FilterUtils.UNAUTHORISED_RESPONSE;
import static uk.co.whitbread.company.employee.utils.FilterUtils.getCompanyIdPathParam;
import static uk.co.whitbread.company.employee.utils.FilterUtils.getEmployeeIdPathParam;
import static uk.co.whitbread.company.employee.utils.SanitizingUtils.sanitize;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.whitbread.company.employee.service.CdhAuthorizationService;
import uk.co.whitbread.company.employee.utils.FilterUtils;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhGetAndUpdateEmployeeFilter extends OncePerRequestFilter {

  private final CdhAuthorizationService cdhAuthorizationService;
  private final FilterUtils filterUtils;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain chain)
      throws ServletException, IOException {
    Optional<CdhEmployeeDetails> optionalTokenDetails = filterUtils.getCdhIdsFromToken(request,
        response);
    if (optionalTokenDetails.isEmpty()) {
      return;
    }
    CdhEmployeeDetails tokenDetails = optionalTokenDetails.get();
    String companyIdFromPath = getCompanyIdPathParam(request);
    String employeeIdFromPath = getEmployeeIdPathParam(request);

    if (cdhAuthorizationService.isSameCompany(tokenDetails.getCompanyAccountId(), companyIdFromPath)
        && (cdhAuthorizationService.isSuperAccessLevelUser(tokenDetails)
        || cdhAuthorizationService.isBusinessPayManagerLevelUser(tokenDetails)
        || cdhAuthorizationService.isSameEmployee(tokenDetails.getEmployeeAccountId(),
        employeeIdFromPath))) {
      chain.doFilter(request, response);
    } else {
      log.warn("Unauthorized {} request to {} at {} for companyId: {} and employeeId: {}",
          request.getMethod(), sanitize(request.getRequestURI()), LocalDateTime.now(),
          tokenDetails.getCompanyAccountId(), tokenDetails.getEmployeeAccountId());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORISED_RESPONSE);
    }
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return !(isGetEmployeeEndpoint(request) ||
            isUpdateEmployeeEndpoint(request)) ||
            HttpMethod.OPTIONS.matches(request.getMethod());
  }

  private boolean isGetEmployeeEndpoint(HttpServletRequest request) {
    return isEmployeeEndpoint(request, "GET");
  }

  private boolean isUpdateEmployeeEndpoint(HttpServletRequest request) {
    return isEmployeeEndpoint(request, "PUT");
  }

  private boolean isEmployeeEndpoint(HttpServletRequest request, String method) {
    String path = request.getServletPath();
    String query = request.getQueryString();
    String[] sections = path.split("/");
    return request.getMethod().equals(method)
        && path.matches("^/companies/.{1,50}/employees/.{1,50}")
        && (Objects.isNull(query) || !query.matches("activation-key=.{1,50}$"))
        && sections.length == 5;
  }
}
