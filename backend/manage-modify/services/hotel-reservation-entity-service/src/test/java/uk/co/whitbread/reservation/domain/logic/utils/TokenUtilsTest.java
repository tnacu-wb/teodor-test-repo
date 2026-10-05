package uk.co.whitbread.reservation.domain.logic.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
}
