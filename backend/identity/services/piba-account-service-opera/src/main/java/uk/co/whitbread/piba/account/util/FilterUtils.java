package uk.co.whitbread.piba.account.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@Component
@RequiredArgsConstructor
public class FilterUtils {

  public static final String UNAUTHORISED_RESPONSE = "Unauthorized to perform this operation";
  private static final String MISSING_AUTHORIZATION_TOKEN_RESPONSE = "Missing authorization token";

  private final TokenService tokenService;

  public Optional<CdhEmployeeDetails> getCdhIdsFromToken(HttpServletRequest request,
      HttpServletResponse response) throws IOException {
    String authorization = request.getHeader("Authorization");
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

}
