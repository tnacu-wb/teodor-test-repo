package uk.co.whitbread.basket.infrastructure.rest.client.payments.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.initiate-payment")
public class InitiatePaymentProperties {

  private String reservationsCache;
}