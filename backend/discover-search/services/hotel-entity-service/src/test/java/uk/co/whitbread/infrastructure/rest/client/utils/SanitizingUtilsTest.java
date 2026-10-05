package uk.co.whitbread.infrastructure.rest.client.utils;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SanitizingUtilsTest {
    @Test
    void sanitizeStrings() {
        assertEquals("test", SanitizingUtils.sanitize("test"));
        assertEquals("testtest", SanitizingUtils.sanitize("test\ntest"));
        assertEquals("testtest", SanitizingUtils.sanitize("test\rtest"));
        assertEquals("testtest", SanitizingUtils.sanitize("test\r\ntest"));
        assertEquals("", SanitizingUtils.sanitize(""));
        assertEquals("", SanitizingUtils.sanitize(null));
    }

    @Test
    void sanitizeForLog() {
        assertEquals("[USER:test]", SanitizingUtils.sanitizeForLog("test"));
        assertEquals("[USER:testtest]", SanitizingUtils.sanitizeForLog("test\ntest"));
        assertEquals("[USER:testtest]", SanitizingUtils.sanitizeForLog("test\rtest"));
        assertEquals("[USER:testtest]", SanitizingUtils.sanitizeForLog("test\r\ntest"));
        assertEquals("[USER:]", SanitizingUtils.sanitizeForLog(""));
        assertEquals("[USER:null]", SanitizingUtils.sanitizeForLog(null));
    }

    @Test
    void sanitize_whenToStringThrowsException() {
        Object object = Mockito.mock(Object.class);
        Mockito.when(object.toString()).thenThrow(new RuntimeException(("Exception")));
        assertEquals("", SanitizingUtils.sanitize(object));
    }
}
