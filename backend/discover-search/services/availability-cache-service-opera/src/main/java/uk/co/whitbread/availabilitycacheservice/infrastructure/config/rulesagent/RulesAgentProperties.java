package uk.co.whitbread.availabilitycacheservice.infrastructure.config.rulesagent;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.rules-agent")
public class RulesAgentProperties {
  private String host;
  private String roomSubstitutionEndpoint;
}
