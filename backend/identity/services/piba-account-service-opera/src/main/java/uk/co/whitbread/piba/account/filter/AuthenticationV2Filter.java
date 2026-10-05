package uk.co.whitbread.piba.account.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import uk.co.whitbread.piba.account.util.FilterUtils;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationV2Filter extends OncePerRequestFilter {

  private final FilterUtils filterUtils;
  private final HandlerExceptionResolver handlerExceptionResolver;

  private static final String RESET_MEMORABLE_WORD_PATH = "/memorableword";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException {
    Optional<CdhEmployeeDetails> optionalTokenDetails = filterUtils.getCdhIdsFromToken(request,
        response);
    if (optionalTokenDetails.isEmpty()) {
      return;
    }
    try {
      chain.doFilter(request, response);
    } catch (Exception e) {
      log.error("Spring Security Filter Chain Exception:", e);
      handlerExceptionResolver.resolveException(request, response, null, e);
    }
  }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
      String uri = request.getRequestURI();
      if (uri.contains(RESET_MEMORABLE_WORD_PATH)) {
        return false;
      }
      return !uri.contains("v2") || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
}
