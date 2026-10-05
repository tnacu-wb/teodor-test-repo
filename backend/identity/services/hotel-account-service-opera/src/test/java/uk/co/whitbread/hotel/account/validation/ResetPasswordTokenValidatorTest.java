package uk.co.whitbread.hotel.account.validation;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.account.exceptions.InvalidTokenException;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.whitbread.hotel.account.service.auth0.Auth0Service.RESET_PASSWORD_TOKEN_EXPIRY_FIELD;
import static uk.co.whitbread.hotel.account.service.auth0.Auth0Service.RESET_PASSWORD_TOKEN_FIELD;

class ResetPasswordTokenValidatorTest {

  @Test
  void validateToken_success() {
    final String token = "abcdxyz";
    final long now = Instant.now().getEpochSecond();
    final long expiryTimeSeconds = now + 3600;
    final Map<String, Object> userAppMetadata = Map.of(RESET_PASSWORD_TOKEN_FIELD, token,
        RESET_PASSWORD_TOKEN_EXPIRY_FIELD, expiryTimeSeconds);

    final ResetPasswordTokenValidator tokenValidator = new ResetPasswordTokenValidator(userAppMetadata, now);

    tokenValidator.validateToken(token);
  }

  @Test
  void validateToken_wrongTokenValue() {
    final String token = "abcdxyz";
    final long now = Instant.now().getEpochSecond();
    final long expiryTimeSeconds = now + 3600;
    final Map<String, Object> userAppMetadata = Map.of(RESET_PASSWORD_TOKEN_FIELD, token,
        RESET_PASSWORD_TOKEN_EXPIRY_FIELD, expiryTimeSeconds);

    final ResetPasswordTokenValidator tokenValidator = new ResetPasswordTokenValidator(userAppMetadata, now);

    assertThrows(InvalidTokenException.class, () -> tokenValidator.validateToken("efg"));
  }

  @Test
  void validateToken_tokenExpired() {
    final String token = "abcdxyz";
    final long now = Instant.now().getEpochSecond();
    final long expiryTimeSeconds = now - 3600;
    final Map<String, Object> userAppMetadata = Map.of(RESET_PASSWORD_TOKEN_FIELD, token,
        RESET_PASSWORD_TOKEN_EXPIRY_FIELD, expiryTimeSeconds);

    final ResetPasswordTokenValidator tokenValidator = new ResetPasswordTokenValidator(userAppMetadata, now);

    assertThrows(InvalidTokenException.class, () -> tokenValidator.validateToken(token));
  }

  @Test
  void validateToken_tokenValueNotPresent() {
    final long now = Instant.now().getEpochSecond();
    final long expiryTimeSeconds = now + 3600;
    final Map<String, Object> userAppMetadata = Map.of(RESET_PASSWORD_TOKEN_EXPIRY_FIELD, expiryTimeSeconds);

    final ResetPasswordTokenValidator tokenValidator = new ResetPasswordTokenValidator(userAppMetadata, now);

    assertThrows(InvalidTokenException.class, () -> tokenValidator.validateToken("efg"));
  }

  @Test
  void validateToken_tokenExpiryTimeNotPresent() {
    final String token = "abcdxyz";
    final long now = Instant.now().getEpochSecond();
    final Map<String, Object> userAppMetadata = Map.of(RESET_PASSWORD_TOKEN_FIELD, token);

    final ResetPasswordTokenValidator tokenValidator = new ResetPasswordTokenValidator(userAppMetadata, now);

    assertThrows(InvalidTokenException.class, () -> tokenValidator.validateToken(token));
  }
}
