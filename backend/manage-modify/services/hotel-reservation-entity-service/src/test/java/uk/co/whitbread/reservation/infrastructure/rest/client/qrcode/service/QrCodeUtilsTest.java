package uk.co.whitbread.reservation.infrastructure.rest.client.qrcode.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import net.glxn.qrgen.core.exception.QRGenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.reservation.infrastructure.rest.client.qrcode.service.properties.QrCodeProperties;

public class QrCodeUtilsTest {
  
  private static final String FILENAME = "QR_CODE.png";
  private static final String CODE = "test";
  private static final String CHARSET = "UTF-8";
  
  private QrCodeUtils mockService;
  private QrCodeProperties properties;
  
  @BeforeEach
  public void setUp() {
    properties = mock(QrCodeProperties.class);
    mockService = new QrCodeUtils(properties);
  }
  
  @Test
  public void testSuccessfulQrCodeGeneration() {
    setProperties(150, 150, CHARSET, FILENAME);
    ByteArrayOutputStream stream = mockService.generateQrCode(CODE);
    assertTrue(stream.size() > 0);
  }
  
  @Test
  public void testFailedQrCodeGeneration() {
    setProperties(-15,-15, CHARSET, FILENAME);
    assertThrows(QRGenerationException.class, () -> {
      mockService.generateQrCode(CODE);
    });
  }
  
  @Test
  public void testGetFilename() {
    setProperties(0, 0, CHARSET, FILENAME);
    assertEquals(FILENAME, mockService.getFilename());
  }
  
  private void setProperties(int width, int height, String charset, String filename) {
    when(properties.getWidth()).thenReturn(width);
    when(properties.getHeight()).thenReturn(height);
    when(properties.getInputCharset()).thenReturn(charset);
    when(properties.getFilename()).thenReturn(filename);
  }
}
