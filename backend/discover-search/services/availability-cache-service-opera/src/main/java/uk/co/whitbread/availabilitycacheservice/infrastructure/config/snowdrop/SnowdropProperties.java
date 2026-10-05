package uk.co.whitbread.availabilitycacheservice.infrastructure.config.snowdrop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "feign-clients.snowdrop")
public class SnowdropProperties {

  private String host;
  private Authentication authentication;

  @Data
  public static class Authentication {

    private boolean enabled;
    private String username;
    private String password;
  }

}
