package uk.co.whitbread.ohip.infrastructure.rest.client.packages.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "enterprise")
public class EnterpriseProperties {

  private List<String> fetchInstructions;
}
