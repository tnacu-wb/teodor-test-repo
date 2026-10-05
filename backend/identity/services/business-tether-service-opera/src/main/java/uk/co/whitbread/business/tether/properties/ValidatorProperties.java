package uk.co.whitbread.business.tether.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@ConfigurationProperties("validator.link-code")
@Data
public class ValidatorProperties {
    private int length;
}
