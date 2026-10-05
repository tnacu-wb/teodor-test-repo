package uk.co.whitbread.kiosk.domain.logic.config;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentTypeConfig {

  private Map<String, String> type;
  
}
