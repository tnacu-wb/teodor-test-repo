package uk.co.whitbread.marketing.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "azure.oauth-client")
public class OauthProperties {
    private String host;
    private String tokenUrl;
    private String clientId;
    private String clientSecret;
    private String scope;
    private String grantType;

    @PostConstruct
    public void concatenateTokenUrlSuffix() {
        tokenUrl = tokenUrl + "/oauth2/v2.0/token";
    }

}
