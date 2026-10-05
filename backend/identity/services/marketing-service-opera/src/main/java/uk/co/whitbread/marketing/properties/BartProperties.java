package uk.co.whitbread.marketing.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import uk.co.whitbread.bart.properties.BaseBartProperties;

@Data
@Component
@RefreshScope
public class BartProperties implements BaseBartProperties {

    @Value("${bart.username}")
    private String username;
    @Value("${bart.password}")
    private String password;
    @Value("${bart.service.marketingService.url}")
    private String subscriptionServiceUrl;
    @Value("${bart.service.sharedDataService.url}")
    private String sharedDataServiceUrl;

}
