package uk.co.whitbread.wallet.domain.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "wallet")
public class WalletProperties {

  private Cert cert;
  private String passJson;
  private String templateDir;
  private ContactCentre contactCentre;
  private String premierInnLink;
  private PassStyle pi;
  private PassStyle bb;
  private PassStyle hub;
  private PassStyle zip;
  private String earlyCheckInTime;

  @Data
  public static class Cert {

    private String password;
    private String p12LocalPath;
    private String p12S3Path;
    private String wwdrcaLocalPath;
    private String wwdrcaS3Path;
  }
  
  @Data
  public static class ContactCentre {

    private String flex;
    private String nonFlex;
  }

  @Data
  public static class PassStyle {

    private String foregroundColor;
    private String backgroundColor;
    private String labelColor;
  }
}