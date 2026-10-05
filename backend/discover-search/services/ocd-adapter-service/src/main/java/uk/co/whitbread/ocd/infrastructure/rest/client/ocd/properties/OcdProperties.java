package uk.co.whitbread.ocd.infrastructure.rest.client.ocd.properties;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.ocd")
public class OcdProperties {

  private String host;
  private String clientId;
  private String clientSecret;
  private String appKey;
  private String channelCode;
  private String enterpriseId;
  private String username;
  private String password;
  private String scope;
  private String authEndpoint;
  private Boolean isClientCredentialsEnabled = false;
  private Boolean wireTapAccessToken = false;
  private Map<String, String> languages;
}
