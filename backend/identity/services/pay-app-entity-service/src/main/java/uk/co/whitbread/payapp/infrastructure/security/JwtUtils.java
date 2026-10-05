package uk.co.whitbread.payapp.infrastructure.security;

import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payapp.ErrorCode;
import uk.co.whitbread.payapp.domain.model.in.jwt.JwtTokenClaims;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.exceptions.InvalidTokenException;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationParticipant;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;

@Slf4j
@AllArgsConstructor
public class JwtUtils {

  private static final String TM_ROLE = "SUPER";
  private static final String BOOKER_ROLE = "BOOKER";

  private AuthenticatedUserService authenticatedUserService;

  public JwtTokenClaims parseToken() {
    var authenticatedUser = authenticatedUserService.getAuthenticatedUser();
    var jwtEmployeeId = authenticatedUser.getAccount().getBartEmployeeId();
    var jwtCompanyId = authenticatedUser.getAccount().getBartId();
    var jwtEmail = authenticatedUser.getAccount().getEmail();

    if (StringUtils.isBlank(jwtEmployeeId)
        || StringUtils.isBlank(jwtCompanyId)
        || StringUtils.isBlank(jwtEmail)) {
      var ex = new InvalidTokenException(ErrorCode.TOKEN_CANNOT_BE_PARSED,
          "Cannot parse the jwt token.");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    return new JwtTokenClaims(jwtCompanyId, jwtEmployeeId, jwtEmail);
  }


  public boolean hasRights(ApplicationResponse application) {

    var account = Optional.ofNullable(authenticatedUserService.getAuthenticatedUser())
        .map(CustomJwtAuthenticationToken::getAccount)
        .orElse(Account.builder().build());

    var isParticipant = application.getParticipants().stream()
        .map(ApplicationParticipant::getParticipantId)
        .anyMatch(applicantId -> applicantId.toString().equals(account.getBartEmployeeId()));

    var isInSameCompany = Objects.equals(account.getBartId(), String.valueOf(application.getCompanyId()));

    return (TM_ROLE.equalsIgnoreCase(account.getAccessLevel())
        || BOOKER_ROLE.equalsIgnoreCase(account.getAccessLevel())
        || isParticipant)
        && isInSameCompany;
  }

}