package uk.co.whitbread.reservation.infrastructure.rest.client.rules.service.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.rules")
public class RulesAdapterProperties {

  private String host;
  private String amendmentRulesEndpoint;
  private String maxRoomRulesEndpoint;
  private String maxNightsRulesEndpoint;
  private String maxRoomOccupancyEndpoint;
  private List<String> wbRoomTypes;
  private String channelBasedOnSourceIdEndpoint;
  private String vatCodesEndpoint;
  private String businessAllowanceEndpoint;
  private String singleOccupancySupplementEndpoint;
}
