package uk.co.whitbread.hotel.card.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@Component
@RequiredArgsConstructor
public class FilterUtils {

  public static final String UNAUTHORISED_RESPONSE = "Unauthorized to perform this operation";
  private static final String MISSING_AUTHORIZATION_TOKEN_RESPONSE = "Missing authorization token";
  public static final String REGEX_DELETE_UPDATE_GET_ENDPOINT = "^/customers/hotels/.{1,50}/cards/.{0,50}$";

  private final TokenService tokenService;

  public Optional<CdhEmployeeDetails> getCdhIdsFromToken(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
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

  public static boolean isAddEmployeePaymentCardsEndpoint(HttpServletRequest request) {
    String path = request.getServletPath();
    return request.getMethod().equals(HttpMethod.POST.name())
        && path.matches("^/customers/hotels/.{1,50}/cards$");
  }

  public static boolean isDeleteEmployeePaymentCardsEndpoint(HttpServletRequest request) {
    String path = request.getServletPath();
    return request.getMethod().equals("DELETE")
        && path.matches(REGEX_DELETE_UPDATE_GET_ENDPOINT);
  }

  public static boolean isGetEmployeePaymentCardsEndpoint(HttpServletRequest request) {
    String path = request.getServletPath();
    return request.getMethod().equals(HttpMethod.GET.name())
        && path.matches(REGEX_DELETE_UPDATE_GET_ENDPOINT);
  }

  public static boolean isUpdateEmployeePaymentCardsEndpoint(HttpServletRequest request) {
    String path = request.getServletPath();
    return request.getMethod().equals(HttpMethod.PUT.name())
        && path.matches(REGEX_DELETE_UPDATE_GET_ENDPOINT);
  }

  public static boolean isGetCompanyPaymentCardsEndpoint(HttpServletRequest request) {
    String path = request.getServletPath();
    return request.getMethod().equals(HttpMethod.GET.name())
            && path.matches("^/companies/.{1,50}/cards");
  }

  public static boolean isInnBusinessEndpoints(HttpServletRequest request) {
    String path = request.getServletPath();
    return path.matches("^/innb/account/.*$");
  }
}
