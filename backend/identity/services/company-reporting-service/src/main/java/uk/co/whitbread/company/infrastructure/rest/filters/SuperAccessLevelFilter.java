package uk.co.whitbread.company.infrastructure.rest.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.whitbread.commons.exceptions.model.ErrorResponse;
import uk.co.whitbread.company.infrastructure.exceptions.AuthorizationException;
import uk.co.whitbread.company.infrastructure.exceptions.ErrorCode;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class SuperAccessLevelFilter extends OncePerRequestFilter {

  private static final String SUPER = "SUPER";

  private static final String FILTER_PATH_SEGMENT = "/admin";

  private static final String ACCESS_DENIED = "Access Denied";

  private static final String COMPANY_ID = "companyId";

  private final AuthenticatedUserService authenticatedUserService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {

    try {
      if (getAccessLevel(request).equals(SUPER)) {

        chain.doFilter(request, response);
      } else {
        log.warn("Unauthorized {} request to {} at {}",
            request.getMethod(), request.getRequestURI(), LocalDateTime.now());
        setErrorResponse(HttpStatus.UNAUTHORIZED, response, ACCESS_DENIED);
      }
    } catch (AuthorizationException e) {
      setErrorResponse(HttpStatus.UNAUTHORIZED, response, e.getMessage());
    }
  }

  public void setErrorResponse(HttpStatus status, HttpServletResponse response, String message) {
    response.setStatus(status.value());
    response.setContentType("application/json");
    ErrorResponse apiError = new ErrorResponse(status.value(), message,
        ErrorCode.UNAUTHORIZED_EXCEPTION.getCode(), null);
    try {
      response.getOutputStream().print(new ObjectMapper().writeValueAsString(apiError));
    } catch (IOException e) {
      log.error("Unable to convert the response");
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    return !path.contains(FILTER_PATH_SEGMENT);
  }

  private String getAccessLevel(HttpServletRequest request) {
    if (!authenticatedUserService.isUserAuthenticated()
        || (null == authenticatedUserService.getAuthenticatedUser().getAccount().getAccessLevel())) {
      throw new AuthorizationException(ACCESS_DENIED);
    }
    request.setAttribute(COMPANY_ID,
        authenticatedUserService.getAuthenticatedUser().getAccount().getCompanyId());
    return authenticatedUserService.getAuthenticatedUser().getAccount().getAccessLevel();
  }

}
