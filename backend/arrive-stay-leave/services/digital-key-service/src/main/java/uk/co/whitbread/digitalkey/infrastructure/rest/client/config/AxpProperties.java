package uk.co.whitbread.digitalkey.infrastructure.rest.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.axp")
public class AxpProperties {
  private String host;
  private String apiKey;
  private String brandId;
  private String generateOtpEndPoint;
  private String passProvisioningWithOtp;
  private String registerMobileDeviceEndpoint;
  private String googleWalletProvisioningWithOtp;
}

