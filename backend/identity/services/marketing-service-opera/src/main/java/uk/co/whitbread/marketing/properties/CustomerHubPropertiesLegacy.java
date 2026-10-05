package uk.co.whitbread.marketing.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@RefreshScope
public class CustomerHubPropertiesLegacy {

    @Value("${customerHub.authKeyHeaderName}")
    private String authKeyHeaderName;
    @Value("${customerHub.timeout}")
    private int timeout;
    @Value("${customerHub.sourceSystem}")
    private String sourceSystem;
    @Value("${customerHub.service.updateMarketingPreferences.url}")
    private String updateMarketingPreferencesServiceUrl;
    @Value("${customerHub.service.updateMarketingPreferences.authKey}")
    private String updateMarketingPreferencesAuthKey;
}
