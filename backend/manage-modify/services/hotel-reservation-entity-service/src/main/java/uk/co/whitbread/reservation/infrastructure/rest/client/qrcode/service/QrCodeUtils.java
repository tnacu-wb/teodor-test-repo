package uk.co.whitbread.reservation.infrastructure.rest.client.qrcode.service;

import java.io.ByteArrayOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.core.exception.QRGenerationException;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.stereotype.Component;
import uk.co.whitbread.reservation.infrastructure.rest.client.qrcode.service.properties.QrCodeProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class QrCodeUtils {

  private final QrCodeProperties qrCodeProperties;
  
  public ByteArrayOutputStream generateQrCode(String code) throws QRGenerationException {
    return QRCode.from(code)
        .withCharset(qrCodeProperties.getInputCharset())
        .to(ImageType.PNG)
        .withSize(qrCodeProperties.getWidth(), qrCodeProperties.getHeight())
        .stream();
  }
  
  public String getFilename() {
    return qrCodeProperties.getFilename();
  }
}
