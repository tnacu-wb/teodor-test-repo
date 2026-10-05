package uk.co.whitbread.piba.registration.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LogUtilsTest {

  @Test
  void testStringReplaceAllRegex() {
    String testingString = "\n\rhaving multiple \n\rnew lines that I will need to remove\n";

    assertEquals("having multiple new lines that I will need to remove",
        LogUtils.sanitisedStringWithMaxLengthLimit(testingString, 10000));
  }

  @Test
  void testNullStringReplaceAllRegex() {
    assertNull(LogUtils.sanitisedStringWithMaxLengthLimit(null, 10000));
  }

}