package uk.co.whitbread.piba.account.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LogUtilsTest {

    @Test
    void testStringReplaceAllRegex() {
        String testingString = "\n\rhaving multiple \n\rnew lines that I will need to remove\n";

        assertEquals("having multiple new lines that I will need to remove", LogUtils.sanitisedStringWithMaxLengthLimit(testingString, 10000));
    }

    @Test
    void testNullStringReplaceAllRegex() {
        assertNull(LogUtils.sanitisedStringWithMaxLengthLimit(null, 10000));
    }
}