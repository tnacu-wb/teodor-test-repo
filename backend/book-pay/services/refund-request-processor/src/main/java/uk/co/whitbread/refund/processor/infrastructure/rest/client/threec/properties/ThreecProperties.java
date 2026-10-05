package uk.co.whitbread.refund.processor.infrastructure.rest.client.threec.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import uk.co.whitbread.refund.processor.infrastructure.config.YamlPropertySourceFactory;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.threec")
@PropertySource(value = "classpath:apiConfigs.yml", factory = YamlPropertySourceFactory.class)
public class ThreecProperties {

  private String host;
  private String paymentsEndpoint;
  private String refundsEndpoint;
  private String threecKey;
  private String threecValue;

}

