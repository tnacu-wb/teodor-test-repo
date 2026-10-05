package uk.co.whitbread.basket.domain.logic.config;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uk.co.whitbread.basket.domain.model.payments.in.Allowance;

@ConfigurationProperties(prefix = "distribution")
@Configuration
@Data
public class DistributionProperties {

  private Map<Integer, Allowance> meals;

  private List<String> mailSuppressionSorceCodes;

}
