package uk.co.whitbread.ondemandrefreshservice.utils;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.util.SanitizingUtils;

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
    void sanitize_whenToStringThrowsException() {
        Object object = Mockito.mock(Object.class);
        Mockito.when(object.toString()).thenThrow(new RuntimeException(("Exception")));
        assertEquals("", SanitizingUtils.sanitize(object));
    }
}
