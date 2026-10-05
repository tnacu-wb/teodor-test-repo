package uk.co.whitbread.cdh.infrastructure.rest.client.oauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cdh.oauth-client")
public class OAuthProperties {

  private String host;
  private String tokenUrl;
  private String clientId;
  private String clientSecret;
  private String scope;
  private String grantType;
}


