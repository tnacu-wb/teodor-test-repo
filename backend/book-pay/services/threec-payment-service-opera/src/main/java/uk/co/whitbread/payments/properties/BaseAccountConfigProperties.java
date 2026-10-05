package uk.co.whitbread.payments.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@ConfigurationProperties(prefix = "provider.accounts.configuration")
@PropertySource(value = "classpath:providers.yml", factory = YamlPropertySourceFactory.class)
@Data
public class BaseAccountConfigProperties {
    private String serviceAction;
    private String newCardTemplate;
    private String savedCardTemplate;
    private String newCardTrxOption;
    private String savedCardTrxOption;
    private String newCardCnpTemplate;
    private String savedCardCnpTemplate;
    private String fraudProfile;
    private boolean fraudScreened;
}
