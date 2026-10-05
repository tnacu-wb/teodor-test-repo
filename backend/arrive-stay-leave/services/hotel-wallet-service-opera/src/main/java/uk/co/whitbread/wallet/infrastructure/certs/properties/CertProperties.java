package uk.co.whitbread.wallet.infrastructure.certs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wallet.cert")
public class CertProperties {

  private String password;
  private String p12LocalPath;
  private String p12S3Path;
  private String wwdrcaLocalPath;
  private String wwdrcaS3Path;

}