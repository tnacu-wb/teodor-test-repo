package uk.co.whitbread.ohip.domain.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SanitizingUtilsTest {

  @Test
  void sanitizeStrings() {
    assertEquals("test", SanitizingUtils.sanitize("test"));
    assertEquals("testtest", SanitizingUtils.sanitize("test\ntest"));
    assertEquals("testtest", SanitizingUtils.sanitize("test\rtest"));
    assertEquals("testtest", SanitizingUtils.sanitize("test\r\ntest"));
    assertEquals("", SanitizingUtils.sanitize(""));
  }

}
