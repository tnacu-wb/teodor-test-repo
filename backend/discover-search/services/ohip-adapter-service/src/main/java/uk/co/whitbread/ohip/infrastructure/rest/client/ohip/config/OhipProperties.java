package uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.ohip")
public class OhipProperties {

  private String host;
  private String clientId;
  private String clientSecret;
  private String appKey;
  private String enterpriseId;
  private String username;
  private String password;
  private String scope;
  private String authEndpoint;
  private Boolean isClientCredentialsEnabled = false;
  private Boolean wireTapAccessToken = false;
  private Map<String, String> languages;
  private Long tokenRefreshClockSkew;
}

