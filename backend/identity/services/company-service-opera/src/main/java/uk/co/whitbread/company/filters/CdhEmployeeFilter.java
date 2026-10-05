package uk.co.whitbread.company.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.whitbread.company.service.cdh.CdhAuthorizationService;
import uk.co.whitbread.company.utils.FilterUtils;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static uk.co.whitbread.company.utils.FilterUtils.*;
import static uk.co.whitbread.company.utils.Utils.sanitizeInputString;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhEmployeeFilter extends OncePerRequestFilter {

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

    if (cdhAuthorizationService.isSameCompany(tokenDetails.getCompanyAccountId(),
        companyIdFromPath)) {
      chain.doFilter(request, response);
    } else {
      log.warn("Unauthorized {} request to {} at {} for companyId: {} and employeeId: {}",
          request.getMethod(), sanitizeInputString(request.getRequestURI()), LocalDateTime.now(),
          tokenDetails.getCompanyAccountId(), tokenDetails.getEmployeeAccountId());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORISED_RESPONSE);
    }
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    String path = request.getServletPath();
    return (path.contains("swagger") ||
        path.contains("v3/api-docs") ||
        path.contains("actuator") ||
        path.contains("/company/check") ||
        path.contains("/admin") ||
        isGetRegistrationQuestionsEndpoint(request) ||
        isGetRegistrationQuestionsByCompanyIdEndpoint(request)) ||
        HttpMethod.OPTIONS.matches(request.getMethod());
  }
}
