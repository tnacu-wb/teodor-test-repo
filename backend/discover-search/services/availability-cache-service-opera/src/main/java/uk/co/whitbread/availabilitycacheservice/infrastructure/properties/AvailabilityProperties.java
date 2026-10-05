package uk.co.whitbread.availabilitycacheservice.infrastructure.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "availability")
public class AvailabilityProperties {

  List<String> ratePlanClasses;

  List<String> employeeRatePlanClasses;
}
