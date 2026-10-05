package uk.co.whitbread.hotel.account.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UtilsTest {

  @Test
  void testSanitizeInputString_RemovesControlCharacters() {
    String input = "Hello\u0007World\n";
    String expected = "HelloWorld";
    assertEquals(expected, Utils.sanitizeInputString(input));
  }

  @Test
  void testSanitizeInputString_NullInput() {
    assertEquals("", Utils.sanitizeInputString(null));
  }

  @Test
  void testSanitizeInputString_NoControlCharacters() {
    String input = "HelloWorld";
    assertEquals("HelloWorld", Utils.sanitizeInputString(input));
  }

  @Test
  void testSanitizeInputString_EmptyString() {
    assertEquals("", Utils.sanitizeInputString(""));
  }

}