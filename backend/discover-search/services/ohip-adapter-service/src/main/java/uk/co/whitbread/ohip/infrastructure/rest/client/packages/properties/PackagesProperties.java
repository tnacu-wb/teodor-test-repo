package uk.co.whitbread.ohip.infrastructure.rest.client.packages.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "packages")
public class PackagesProperties {

  private List<String> freeKidsBreakfast;
  private List<String> fetchInstructions;
  private List<String> donationFetchInstructions;
  private String packageGroupsLimit;
}
