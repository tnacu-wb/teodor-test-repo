package uk.co.whitbread.hotel.card.filters;

import static uk.co.whitbread.hotel.card.utils.FilterUtils.isAddEmployeePaymentCardsEndpoint;
import static uk.co.whitbread.hotel.card.utils.FilterUtils.isDeleteEmployeePaymentCardsEndpoint;
import static uk.co.whitbread.hotel.card.utils.FilterUtils.isGetEmployeePaymentCardsEndpoint;
import static uk.co.whitbread.hotel.card.utils.FilterUtils.isGetCompanyPaymentCardsEndpoint;
import static uk.co.whitbread.hotel.card.utils.FilterUtils.isUpdateEmployeePaymentCardsEndpoint;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.co.whitbread.hotel.card.exceptions.HotelAccountClientException;
import uk.co.whitbread.hotel.card.utils.FilterUtils;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class CdhEmployeeFilter extends OncePerRequestFilter {

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
    try {
      chain.doFilter(request, response);
    } catch (HotelAccountClientException e) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
    }
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return (!isAddEmployeePaymentCardsEndpoint(request) &&
                !isDeleteEmployeePaymentCardsEndpoint(request) &&
                !isUpdateEmployeePaymentCardsEndpoint(request) &&
                !isGetEmployeePaymentCardsEndpoint(request)) &&
                !isGetCompanyPaymentCardsEndpoint(request)||
                HttpMethod.OPTIONS.matches(request.getMethod());
  }
}
