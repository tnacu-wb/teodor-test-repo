package uk.co.whitbread.shared.cdh.oauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.cdh.properties.CustomerDataHubProperties;

@Data
@Component
@ConfigurationProperties(prefix = "cdh.oauth-client")
public class OAuthProperties implements CustomerDataHubProperties {
    private String host;
    private String tokenUrl;
    private String clientId;
    private String clientSecret;
    private String scope;
    private String grantType;
}
