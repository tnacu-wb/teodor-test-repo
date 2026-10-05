package uk.co.whitbread.hotel.card.filters;

import static uk.co.whitbread.hotel.card.utils.FilterUtils.UNAUTHORISED_RESPONSE;
import static uk.co.whitbread.hotel.card.utils.SanitizingUtils.sanitize;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.whitbread.hotel.card.exceptions.HotelAccountClientException;
import uk.co.whitbread.hotel.card.service.CdhAuthorizationService;
import uk.co.whitbread.hotel.card.utils.FilterUtils;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@Slf4j
@Component
@Order(1)
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
    try {
      if (cdhAuthorizationService.isSameEntity(tokenDetails.getCompanyAccountId(),
          companyIdFromPath)
          && cdhAuthorizationService.isSuperAccessLevelUser(tokenDetails)) {
        chain.doFilter(request, response);
      } else {
        log.warn("Unauthorized {} request to {} at {} for companyId: {} and employeeId: {}",
            request.getMethod(), sanitize(request.getRequestURI()), LocalDateTime.now(),
            tokenDetails.getCompanyAccountId(), tokenDetails.getEmployeeAccountId());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORISED_RESPONSE);
      }
    } catch (HotelAccountClientException e) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
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
