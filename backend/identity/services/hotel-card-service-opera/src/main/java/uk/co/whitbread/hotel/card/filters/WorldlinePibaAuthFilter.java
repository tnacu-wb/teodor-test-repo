package uk.co.whitbread.hotel.card.filters;

import static java.util.Objects.isNull;
import static uk.co.whitbread.hotel.card.utils.FilterUtils.isInnBusinessEndpoints;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.whitbread.hotel.card.exceptions.HotelAccountClientException;
import uk.co.whitbread.hotel.card.utils.FilterUtils;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class WorldlinePibaAuthFilter extends OncePerRequestFilter {

  private final FilterUtils filterUtils;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain chain)
      throws ServletException, IOException {

    Optional<CdhEmployeeDetails> optionalTokenDetails = filterUtils.getCdhIdsFromToken(request,
        response);
    if (optionalTokenDetails.isEmpty()
        || isNull(optionalTokenDetails.get().getEmployeeAccountId())
        || isNull(optionalTokenDetails.get().getCompanyAccountId())
    ) {
      return;
    }
    try {
      chain.doFilter(request, response);
    } catch (HotelAccountClientException e) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return !isInnBusinessEndpoints(request);
  }

}
