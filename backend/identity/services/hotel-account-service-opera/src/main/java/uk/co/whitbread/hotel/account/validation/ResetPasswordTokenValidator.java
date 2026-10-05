package uk.co.whitbread.hotel.account.validation;

import static uk.co.whitbread.hotel.account.service.auth0.Auth0Service.RESET_PASSWORD_TOKEN_EXPIRY_FIELD;
import static uk.co.whitbread.hotel.account.service.auth0.Auth0Service.RESET_PASSWORD_TOKEN_FIELD;

import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import uk.co.whitbread.hotel.account.exceptions.InvalidTokenException;

@RequiredArgsConstructor
public class ResetPasswordTokenValidator {

  private static final String INVALID_TOKEN_MESSAGE = "The provided token is invalid: ";

  private final Map<String, Object> userAppMetadata;
  private final long currentTimeSeconds;

  public void validateToken(String resetPasswordToken) {
    validateTokenValue(resetPasswordToken);
    validateTokenExpiry(resetPasswordToken);
  }

  private void validateTokenValue(String passwordToken) {
    final String resetPassToken = (String) userAppMetadata.get(RESET_PASSWORD_TOKEN_FIELD);
    if (!Objects.equals(resetPassToken, passwordToken)) {
      throw new InvalidTokenException(INVALID_TOKEN_MESSAGE + passwordToken);
    }
  }

  private void validateTokenExpiry(String passwordToken) {
    final Object tokenExpiryValue = userAppMetadata.get(RESET_PASSWORD_TOKEN_EXPIRY_FIELD);
    if (Objects.isNull(tokenExpiryValue)) {
      throw new InvalidTokenException(INVALID_TOKEN_MESSAGE + passwordToken);
    }
    final long tokenExpiry;
    if (tokenExpiryValue instanceof Integer) {
      tokenExpiry = Integer.toUnsignedLong((int) tokenExpiryValue);
    } else {
      tokenExpiry = (long) tokenExpiryValue;
    }
    if (currentTimeSeconds > tokenExpiry) {
      throw new InvalidTokenException(INVALID_TOKEN_MESSAGE + passwordToken);
    }
  }
}
