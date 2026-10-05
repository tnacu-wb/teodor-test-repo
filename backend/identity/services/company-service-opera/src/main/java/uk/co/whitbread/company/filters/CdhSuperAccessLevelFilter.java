package uk.co.whitbread.company.filters;

import static uk.co.whitbread.company.utils.FilterUtils.UNAUTHORISED_RESPONSE;
import static uk.co.whitbread.company.utils.FilterUtils.isGetRegistrationQuestionsEndpoint;
import static uk.co.whitbread.company.utils.Utils.sanitizeInputString;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.whitbread.company.service.cdh.CdhAuthorizationService;
import uk.co.whitbread.company.utils.FilterUtils;
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
          request.getMethod(), sanitizeInputString(request.getRequestURI()), LocalDateTime.now(),
          tokenDetails.getCompanyAccountId(), tokenDetails.getEmployeeAccountId());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORISED_RESPONSE);
    }
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    String path = request.getServletPath();
    return !(path.contains("/admin") || isGetRegistrationQuestionsEndpoint(request)) ||
        HttpMethod.OPTIONS.matches(request.getMethod());
  }

  private String getCompanyIdPathParam(HttpServletRequest request) {
    String path = request.getServletPath();
    String[] sections = path.split("/");
    if (sections[2].equalsIgnoreCase("admin")) {
      return sections[3];
    }
    return sections[2];
  }
}
