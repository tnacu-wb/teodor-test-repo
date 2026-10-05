package uk.co.whitbread.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "config.service.oauth-client")
public class OAuthProperties {

  private String host;
  private String tokenUrl;
  private String clientId;
  private String clientSecret;
  private String scope;
  private String grantType;

}



