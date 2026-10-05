package uk.co.whitbread.basket.domain.logic.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import uk.co.whitbread.basket.domain.exception.InvalidTokenException;

class TokenUtilsTest {

  private static final String BASKET_REF = "AWM5978223";

  @Test
  void shouldCallEncryptWithBookingRef() {

    try (MockedStatic<CipherUtils> cipherUtilsMock = Mockito.mockStatic(CipherUtils.class)) {

      // Arrange
      ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);

      // Act
      TokenUtils.getToken(BASKET_REF);

      // Assert
      cipherUtilsMock.verify(() -> CipherUtils.encryptWithPrefixInitVector(argCaptor.capture()));
      assertEquals(BASKET_REF, argCaptor.getValue().split("\\|")[0]);
    }
  }

  @Test
   void shouldReturnIsValidTrueForValidToken() {

    try (MockedStatic<CipherUtils> cipherUtilsMock = Mockito.mockStatic(CipherUtils.class)) {

      // Arrange
      var decryptedText = BASKET_REF + "|" + Instant.now().getEpochSecond();
      cipherUtilsMock.when(() -> CipherUtils.decryptWithPrefixInitVector(any(String.class))).thenReturn(decryptedText);

      // Act
      Boolean valid = TokenUtils.isValid("", BASKET_REF);

      // Assert
      assertTrue(valid);
    }
  }

  @Test
  void shouldReturnIsValidFalseForOtherBasketRef() {

    try (MockedStatic<CipherUtils> cipherUtilsMock = Mockito.mockStatic(CipherUtils.class)) {

      // Arrange
      var otherBasketRef = "AWM5978111";
      var decryptedText = otherBasketRef + "|" + Instant.now().getEpochSecond();
      cipherUtilsMock.when(() -> CipherUtils.decryptWithPrefixInitVector(any(String.class))).thenReturn(decryptedText);

      // Act
      Boolean valid = TokenUtils.isValid("", BASKET_REF);

      // Assert
      assertFalse(valid);
    }
  }

  @Test
  void shouldReturnIsValidFalseForExpiredToken() {

    try (MockedStatic<CipherUtils> cipherUtilsMock = Mockito.mockStatic(CipherUtils.class)) {

      // Arrange
      var oneHourAgoTimestamp = Instant.now().getEpochSecond() - 3600;
      var decryptedText = BASKET_REF + "|" + oneHourAgoTimestamp;
      cipherUtilsMock.when(() -> CipherUtils.decryptWithPrefixInitVector(any(String.class))).thenReturn(decryptedText);

      // Act
      Boolean valid = TokenUtils.isValid("", BASKET_REF);

      // Assert
      assertFalse(valid);
    }
  }

  @Test
  void validateToken_throwsInvalidTokenException_whenTokenIsEmpty() {
    // Act & Assert
    assertThrows(InvalidTokenException.class,
        () -> TokenUtils.validateToken("", BASKET_REF));
  }

  @Test
  void validateToken_throwsInvalidTokenException_whenTokenIsNull() {
    // Act & Assert
    assertThrows(InvalidTokenException.class,
        () -> TokenUtils.validateToken(null, BASKET_REF));
  }

  @Test
  void validateToken_throwsInvalidTokenException_whenTokenIsInvalid() {
    try (MockedStatic<CipherUtils> cipherUtilsMock = Mockito.mockStatic(CipherUtils.class)) {
      // Arrange
      cipherUtilsMock.when(() -> CipherUtils.decryptWithPrefixInitVector(any(String.class)))
          .thenReturn("INVALID_TOKEN|" + Instant.now().getEpochSecond());

      // Act & Assert
      InvalidTokenException exception = assertThrows(InvalidTokenException.class,
          () -> TokenUtils.validateToken("invalid_token", BASKET_REF));
      assertTrue(exception.getMessage().contains("Invalid token"));
    }
  }

  @Test
  void validateToken_throwsInvalidTokenException_whenTokenIsExpired() {
    try (MockedStatic<CipherUtils> cipherUtilsMock = Mockito.mockStatic(CipherUtils.class)) {
      // Arrange
      var expiredTimestamp = Instant.now().getEpochSecond() - 3600;
      var decryptedText = BASKET_REF + "|" + expiredTimestamp;
      cipherUtilsMock.when(() -> CipherUtils.decryptWithPrefixInitVector(any(String.class)))
          .thenReturn(decryptedText);

      // Act & Assert
      InvalidTokenException exception = assertThrows(InvalidTokenException.class,
          () -> TokenUtils.validateToken("valid_token", BASKET_REF));
      assertTrue(exception.getMessage().contains("Invalid token"));
    }
  }

  @Test
  void validateToken_doesNotThrow_whenTokenIsValid() {
    try (MockedStatic<CipherUtils> cipherUtilsMock = Mockito.mockStatic(CipherUtils.class)) {
      // Arrange
      var validTimestamp = Instant.now().getEpochSecond();
      var decryptedText = BASKET_REF + "|" + validTimestamp;
      cipherUtilsMock.when(() -> CipherUtils.decryptWithPrefixInitVector(any(String.class)))
          .thenReturn(decryptedText);

      // Act & Assert - should not throw
      TokenUtils.validateToken("valid_token", BASKET_REF);
    }
  }

  @Test
  void parseToken_replacesSpacesWithPlus() {
    // Arrange
    var tokenWithSpaces = "token with spaces";
    
    // Act
    var result = TokenUtils.parseToken(tokenWithSpaces);
    
    // Assert
    assertEquals("token+with+spaces", result);
  }
}
