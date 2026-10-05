package uk.co.whitbread.dashboard.domain.properties;

import java.util.HashMap;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "dashboard")
@Component
@Data
public class DashboardProperties {

  private int maxDays;
  /** For one hotel to be considered a frequent booking it needs to have at least minToBeFrequent past stays. **/
  private int minToBeFrequent;
  /** Max number of hotels to be displayed as frequent bookings. **/
  private int maxFrequent;
  private String environment;
  private HashMap<String, HashMap<String, String>> actionMapping;

}
