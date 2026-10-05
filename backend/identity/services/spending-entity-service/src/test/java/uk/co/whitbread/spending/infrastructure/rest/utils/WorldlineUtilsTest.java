package uk.co.whitbread.spending.infrastructure.rest.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.spending.infrastructure.rest.utils.WorldlineUtils.TRUE_CLIENT_IP_HEADER;
import static uk.co.whitbread.spending.infrastructure.rest.utils.WorldlineUtils.X_FORWARDED_FOR_HEADER;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.spending.domain.model.in.worldline.TrustedPartnerCredentials;

@ExtendWith(MockitoExtension.class)
class WorldlineUtilsTest {

  private static final String DEFAULT_IP = "1.1.1.1";

  @ParameterizedTest
  @CsvSource({
        "'192.168.1.1', '192.168.1.1', ''",
        "'192.168.1.1, 192.168.1.2', '192.168.1.1', ''",
        "'', '192.168.1.3', '192.168.1.3'"
  })
  void getClientIp_WhenXForwardedForHeaderIsPresentOrNot_ThenCorrectIpIsRetrieved(String xForwardedFor, String expectedIp, String remoteAddr) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(X_FORWARDED_FOR_HEADER)).thenReturn(xForwardedFor.isEmpty() ? null : xForwardedFor);
    when(request.getHeader(TRUE_CLIENT_IP_HEADER)).thenReturn(null);
    lenient().when(request.getRemoteAddr()).thenReturn(remoteAddr.isEmpty() ? null : remoteAddr);

    String clientIp = WorldlineUtils.getClientIp(request, DEFAULT_IP);

    assertEquals(expectedIp, clientIp);
  }

  @ParameterizedTest
  @CsvSource({
      "'192.168.1.1', '192.168.1.1', ''",
      "'192.168.1.1', '192.168.1.1', ''",
      "'', '192.168.1.3', '192.168.1.3'"
  })
  void getClientIp_WhenClientIpIsPresentOrNot_ThenCorrectIpIsRetrieved(String trueClientIp, String expectedIp, String remoteAddr) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(TRUE_CLIENT_IP_HEADER)).thenReturn(trueClientIp.isEmpty() ? null : trueClientIp);
    lenient().when(request.getRemoteAddr()).thenReturn(remoteAddr.isEmpty() ? null : remoteAddr);

    String clientIp = WorldlineUtils.getClientIp(request, DEFAULT_IP);

    assertEquals(expectedIp, clientIp);
  }

  @Test
  void getClientIp_WhenXForwardedForHasInvalidIp_ThenDefaultIpIsUsed() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(X_FORWARDED_FOR_HEADER)).thenReturn("999.999.999.999");

    String clientIp = WorldlineUtils.getClientIp(request, DEFAULT_IP);

    assertEquals(DEFAULT_IP, clientIp);
  }

  @Test
  void getClientIp_WhenTrueClientIpHasInvalidIp_ThenDefaultIpIsUsed() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(TRUE_CLIENT_IP_HEADER)).thenReturn("999.999.999.999");

    String clientIp = WorldlineUtils.getClientIp(request, DEFAULT_IP);

    assertEquals(DEFAULT_IP, clientIp);
  }

  @Test
  void getClientIp_WhenRequestObjectIsNull_ThenDefaultIpIsSent() {
    String clientIp = WorldlineUtils.getClientIp(null, DEFAULT_IP);

    assertEquals(DEFAULT_IP, clientIp);
  }

  @Test
  void getClientIp_WhenAllIpAddressAreInvalid_ThenDefaultIpIsUsed() {
    // Arrange
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(WorldlineUtils.TRUE_CLIENT_IP_HEADER)).thenReturn("unknown");
    when(request.getHeader(WorldlineUtils.X_FORWARDED_FOR_HEADER)).thenReturn("unknown");
    when(request.getRemoteAddr()).thenReturn("unknown");

    String defaultIpAddress = "1.1.1.1";

    // Act
    String clientIp = WorldlineUtils.getClientIp(request, defaultIpAddress);

    // Assert
    assertEquals(defaultIpAddress, clientIp);
  }

  @Test
  void serializeHeader_HappyCase_ReturnsSerializedJson() {
    // Arrange
    TrustedPartnerCredentials authHeader = TrustedPartnerCredentials.builder()
        .username("username")
        .password("pass123").build();

    // Act
    String result = WorldlineUtils.serializeHeader(authHeader);

    // Assert
    assertEquals("{\"Username\":\"username\",\"Password\":\"pass123\"}", result);
  }

  @Test
  void serializeHeader_WhenJsonProcessingExceptionThrown_ReturnsNull() {
      // Arrange
      TrustedPartnerCredentials authHeader = new TrustedPartnerCredentials() {
          @Override
          public String getUsername() {
              throw new RuntimeException("Simulated exception");
          }
      };

      // Act
      String result = WorldlineUtils.serializeHeader(authHeader);

      // Assert
      assertNull(result);
  }

}