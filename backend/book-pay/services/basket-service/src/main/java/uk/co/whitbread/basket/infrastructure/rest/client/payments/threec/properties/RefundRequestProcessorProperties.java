package uk.co.whitbread.basket.infrastructure.rest.client.payments.threec.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.refund")
public class RefundRequestProcessorProperties {

  private String host;
  private String tokenRefundEndpoint;

}
