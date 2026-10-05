package uk.co.whitbread.hotel.account.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.whitbread.hotel.account.config.WorldlineProperties;

class NetworkUtilsTest {

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
    NetworkUtils utils = new NetworkUtils(worldlineProperties);

    String clientIp = utils.getClientIp(request);
    assertEquals(expectedIp, clientIp);
  }

  @Test
  void testGetClientIp_withInvalidIp() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("X-Forwarded-For")).thenReturn("999.999.999.999");

    WorldlineProperties worldlineProperties = mock(WorldlineProperties.class);
    when(worldlineProperties.getDefaultIpAddress()).thenReturn("0.0.0.0");
    NetworkUtils utils = new NetworkUtils(worldlineProperties);

    String clientIp = utils.getClientIp(request);
    assertEquals("0.0.0.0", clientIp);
  }

}