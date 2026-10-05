package uk.co.whitbread.payapp.infrastructure.util;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.exceptions.EmailMismatchException;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties.WorldlineProperties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilsTest {

  @ParameterizedTest
  @CsvSource({
      "'192.168.1.1', '192.168.1.1', ''",
      "'192.168.1.1, 192.168.1.2', '192.168.1.1', ''",
      "'', '192.168.1.3', '192.168.1.3'"
  })
  void testGetClientIp(String xForwardedFor, String expectedIp, String remoteAddr) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("X-Forwarded-For")).thenReturn(xForwardedFor.isEmpty() ? null : xForwardedFor);
    when(request.getRemoteAddr()).thenReturn(remoteAddr.isEmpty() ? null : remoteAddr);

    WorldlineProperties worldlineProperties = mock(WorldlineProperties.class);
    when(worldlineProperties.getDefaultIpAddress()).thenReturn("0.0.0.0");
    Utils utils = new Utils(worldlineProperties);

    String clientIp = utils.getClientIp(request);
    assertEquals(expectedIp, clientIp);
  }

  @ParameterizedTest
  @CsvSource({
      "'192.168.1.1', '192.168.1.1', ''",
      "'192.168.1.1', '192.168.1.1', ''",
      "'', '192.168.1.3', '192.168.1.3'"
  })
  void testGetClientIpTrueClientHeader(String trueClientIp, String expectedIp, String remoteAddr) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("X-Forwarded-For")).thenReturn(null);
    when(request.getHeader("true-client-ip")).thenReturn(trueClientIp.isEmpty() ? null : trueClientIp);
    when(request.getRemoteAddr()).thenReturn(remoteAddr.isEmpty() ? null : remoteAddr);

    WorldlineProperties worldlineProperties = mock(WorldlineProperties.class);
    when(worldlineProperties.getDefaultIpAddress()).thenReturn("0.0.0.0");
    Utils utils = new Utils(worldlineProperties);

    String clientIp = utils.getClientIp(request);
    assertEquals(expectedIp, clientIp);
  }

  @Test
  void testGetClientIp_withInvalidIp() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("X-Forwarded-For")).thenReturn("999.999.999.999");

    WorldlineProperties worldlineProperties = mock(WorldlineProperties.class);
    when(worldlineProperties.getDefaultIpAddress()).thenReturn("0.0.0.0");
    Utils utils = new Utils(worldlineProperties);

    String clientIp = utils.getClientIp(request);
    assertEquals("0.0.0.0", clientIp);
  }

  @Test
  void testValidateEmailWithToken_validEmail() {
    assertDoesNotThrow(() -> Utils.validateEmailWithToken("test@example.com", "test@example.com"));
  }

  @Test
  void testValidateEmailWithToken_invalidEmail() {
    EmailMismatchException exception = assertThrows(EmailMismatchException.class,
        () -> Utils.validateEmailWithToken("wrong@example.com", "test@example.com"));
    assertEquals("Email from request body was different from email in authorization token", exception.getMessage());
  }

  @Test
  void testSanitizeInputString() {
    String input = "test\nstring\r";
    String sanitized = Utils.sanitizeInputString(input);
    assertEquals("teststring", sanitized);
  }
}
