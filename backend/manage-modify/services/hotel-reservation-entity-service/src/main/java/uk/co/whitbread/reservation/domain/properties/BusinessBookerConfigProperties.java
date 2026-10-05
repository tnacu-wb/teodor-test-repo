package uk.co.whitbread.reservation.domain.properties;

import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "business-booker")
public class BusinessBookerConfigProperties {

  private Set<String> negociatedRatePlanSet;
}
