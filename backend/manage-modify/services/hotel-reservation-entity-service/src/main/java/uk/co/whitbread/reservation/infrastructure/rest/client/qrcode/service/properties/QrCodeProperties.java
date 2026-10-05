package uk.co.whitbread.reservation.infrastructure.rest.client.qrcode.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "qrcode")
public class QrCodeProperties {
  
  private String inputCharset;
  private int width;
  private int height;
  private String filename;
}
