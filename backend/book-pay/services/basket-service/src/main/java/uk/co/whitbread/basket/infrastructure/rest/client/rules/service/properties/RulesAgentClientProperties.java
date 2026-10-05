package uk.co.whitbread.basket.infrastructure.rest.client.rules.service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.rules-agent")
public class RulesAgentClientProperties {

  private String host;
  private String businessAllowanceEndpoint;
  private String vatCodesEndpoint;
}
