package uk.co.whitbread.reservation.domain.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "promotion")
public class PromotionProperties {

  private List<String> promotionalPackages;
}