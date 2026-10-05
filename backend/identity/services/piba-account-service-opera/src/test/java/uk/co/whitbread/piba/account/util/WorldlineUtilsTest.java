package uk.co.whitbread.piba.account.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.co.whitbread.piba.account.model.TrustedPartnerCredentials;
import uk.co.whitbread.piba.account.properties.WorldlineRestProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.piba.account.util.WorldlineUtils.TRUE_CLIENT_IP_HEADER;
import static uk.co.whitbread.piba.account.util.WorldlineUtils.X_FORWARDED_FOR_HEADER;

class WorldlineUtilsTest {

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private WorldlineUtils worldlineUtils;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @ParameterizedTest
  @CsvSource({
      "'192.168.1.1', '192.168.1.1', ''",
      "'192.168.1.1, 192.168.1.2', '192.168.1.1', ''",
      "'', '192.168.1.3', '192.168.1.3'"
  })
  void testGetClientIp(String xForwardedFor, String expectedIp, String remoteAddr) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(X_FORWARDED_FOR_HEADER)).thenReturn(xForwardedFor.isEmpty() ? null : xForwardedFor);
    when(request.getRemoteAddr()).thenReturn(remoteAddr.isEmpty() ? null : remoteAddr);

    WorldlineRestProperties worldlineProperties = mock(WorldlineRestProperties.class);
    when(worldlineProperties.getDefaultIpAddress()).thenReturn("0.0.0.0");
    WorldlineUtils utils = new WorldlineUtils(worldlineProperties);

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
    when(request.getHeader(X_FORWARDED_FOR_HEADER)).thenReturn(null);
    when(request.getHeader(TRUE_CLIENT_IP_HEADER)).thenReturn(trueClientIp.isEmpty() ? null : trueClientIp);
    when(request.getRemoteAddr()).thenReturn(remoteAddr.isEmpty() ? null : remoteAddr);

    WorldlineRestProperties worldlineProperties = mock(WorldlineRestProperties.class);
    when(worldlineProperties.getDefaultIpAddress()).thenReturn("0.0.0.0");
    WorldlineUtils utils = new WorldlineUtils(worldlineProperties);

    String clientIp = utils.getClientIp(request);
    assertEquals(expectedIp, clientIp);
  }

  @Test
  void testGetClientIp_withInvalidIp() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader(X_FORWARDED_FOR_HEADER)).thenReturn("999.999.999.999");

    WorldlineRestProperties worldlineProperties = mock(WorldlineRestProperties.class);
    when(worldlineProperties.getDefaultIpAddress()).thenReturn("0.0.0.0");
    WorldlineUtils utils = new WorldlineUtils(worldlineProperties);

    String clientIp = utils.getClientIp(request);
    assertEquals("0.0.0.0", clientIp);
  }

  @Test
  void testSerializeObject() throws JsonProcessingException {
    // Arrange
    var object = TrustedPartnerCredentials.builder()
        .username("username")
        .password("password")
        .build();

    Mockito.when(objectMapper.writeValueAsString(any())).thenReturn("{\"Username\":\"username\",\"Password\":\"password\"}");

    // Act
    String result = worldlineUtils.serializeObject(object);

    // Assert
    assertEquals("{\"Username\":\"username\",\"Password\":\"password\"}", result);
  }

  @Test
  void testSerializeObjectShouldThrowException() throws JsonProcessingException {
    // Arrange
    Object obj = new Object();
    Mockito.when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

    // Act
    String result = worldlineUtils.serializeObject(obj);

    // Assert
    assertNull(result);
  }
}