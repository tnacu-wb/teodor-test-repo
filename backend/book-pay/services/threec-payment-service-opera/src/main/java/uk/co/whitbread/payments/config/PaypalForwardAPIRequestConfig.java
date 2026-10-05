package uk.co.whitbread.payments.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.co.whitbread.payments.properties.YamlPropertySourceFactory;

import java.util.List;

@ConfigurationProperties(prefix = "paypal-transformations")
@PropertySource(value = "classpath:paypal.yml", factory = YamlPropertySourceFactory.class)
@Configuration
@Data
@Slf4j
public class PaypalForwardAPIRequestConfig {
    private List<PaypalForwardAPIRequest> paypalForwardAPIRequest;
}

