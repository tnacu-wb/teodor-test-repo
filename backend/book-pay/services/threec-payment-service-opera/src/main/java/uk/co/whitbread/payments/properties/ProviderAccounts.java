package uk.co.whitbread.payments.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@ConfigurationProperties(prefix = "provider")
@PropertySource(value = "classpath:providers.yml", factory = YamlPropertySourceFactory.class)
@Configuration
@Data
public class ProviderAccounts {
    List<ProviderAccount> accounts;
}
