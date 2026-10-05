package uk.co.whitbread.business.tether.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.business.tether.utils.SanitizingUtils;

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
