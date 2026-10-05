package uk.co.whitbread.hotel.account.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RefreshScope
@ConfigurationProperties(prefix = "url.whitelist")
@Data
public class WhitelistProperties {

    private List<String> regex;

}
