package uk.co.whitbread.reservation.infrastructure.rest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.io.ByteArrayOutputStream;
import net.glxn.qrgen.core.exception.QRGenerationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.reservation.infrastructure.rest.client.qrcode.service.QrCodeUtils;
import uk.co.whitbread.reservation.infrastructure.rest.controller.qrcode.QrCodeController;

@ExtendWith(MockitoExtension.class)
public class QrCodeControllerTest {
  
  @Mock
  private QrCodeUtils mockService;
  
  @Mock
  private ByteArrayOutputStream mockByteStream;
  
  @InjectMocks
  private QrCodeController qrCodeController;
  
  @Test
  public void shouldGenerateQRCode() {
    //Given
    String code = "testCode";
    
    doReturn(new byte[]{1,2,3}).when(mockByteStream).toByteArray();
    doReturn(mockByteStream).when(mockService).generateQrCode(eq(code));
    doReturn("mockFileName").when(mockService).getFilename();
    
    
    //When
    ResponseEntity<byte[]> responseEntity = qrCodeController.generateReservationQrCode(code);
    
    //Then
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertEquals(responseEntity.getHeaders().get("Content-Type").get(0), MediaType.IMAGE_PNG.toString());
    assertEquals(responseEntity.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0), "attachment; filename=mockFileName");
  }
  
  @Test
  public void shouldHandleServiceException() {
    //Given
    String code = "testCode";
    doThrow(new QRGenerationException("Service Exception", new RuntimeException())).when(mockService).generateQrCode(eq(code));
    
    //When
    ResponseEntity<byte[]> responseEntity = qrCodeController.generateReservationQrCode(code);
    
    //Then
    assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    assertEquals(responseEntity.getHeaders().get("Content-Type").get(0), MediaType.IMAGE_PNG.toString());
    assertEquals(responseEntity.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0), "attachment; filename=null");
  }
}
