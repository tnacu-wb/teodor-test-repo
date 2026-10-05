package uk.co.whitbread.company.employee.filters;

import static uk.co.whitbread.company.employee.utils.FilterUtils.UNAUTHORISED_RESPONSE;
import static uk.co.whitbread.company.employee.utils.SanitizingUtils.sanitize;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class CdhSuperAccessLevelFilter extends OncePerRequestFilter {

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

    if (cdhAuthorizationService.isSameCompany(tokenDetails.getCompanyAccountId(), companyIdFromPath)
        && (cdhAuthorizationService.isSuperAccessLevelUser(tokenDetails)
        || cdhAuthorizationService.isBusinessPayManagerLevelUser(tokenDetails))) {
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
    String path = request.getServletPath();
    return !path.contains("/admin") ||
            HttpMethod.OPTIONS.matches(request.getMethod());
  }

  private String getCompanyIdPathParam(HttpServletRequest request) {
    String path = request.getServletPath();
    String[] sections = path.split("/");
    if (path.contains("/admin")) {
      return sections[3];
    }
    return sections[2];
  }
}
