package uk.co.whitbread.hotel.payment.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RefreshScope
@Data
@ConfigurationProperties(prefix="domain")
public class DomainUrlProperties {

    private List<String> regex;
    private String defaultUrl;
    private String defaultIp;

}
