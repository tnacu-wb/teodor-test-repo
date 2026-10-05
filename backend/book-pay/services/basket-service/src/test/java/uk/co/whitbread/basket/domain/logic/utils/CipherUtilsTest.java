package uk.co.whitbread.basket.domain.logic.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CipherUtilsTest {

  @Test
  void shouldSuccessfullyEncryptAndDecryptText() throws Exception {
    // Arrange
    var textToEncrypt = "this is my text for encryption";

    // Act
    var encryptedText = CipherUtils.encryptWithPrefixInitVector(textToEncrypt);
    var decryptedText = CipherUtils.decryptWithPrefixInitVector(encryptedText);

    // Assert
    assertThat(decryptedText, is(textToEncrypt));
  }

  @Test
  void shouldThrowErrorForInvalidEncryptedText()  throws Exception{
    // Arrange
    var textToEncrypt = "this is my text for encryption";

    // Act
    var encryptedText = CipherUtils.encryptWithPrefixInitVector(textToEncrypt) + "invalid";

    // Assert
    assertThrows(IllegalArgumentException.class, () -> CipherUtils.decryptWithPrefixInitVector(encryptedText));
  }
}
