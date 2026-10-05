package uk.co.whitbread.ohip.infrastructure.rest.client.rules.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.rules-agent")
public class RulesAgentProperties {

  private String host;
  private String roomSubstitutionEndpoint;
  private String bookingChannelInfo;
  private String vatCodesEndpoint;
  private List<String> wbRoomTypes;
  private String businessAllowanceEndpoint;
  private String channelSourceInfoEndpoint;
  private String baseRateEndpoint;
}
