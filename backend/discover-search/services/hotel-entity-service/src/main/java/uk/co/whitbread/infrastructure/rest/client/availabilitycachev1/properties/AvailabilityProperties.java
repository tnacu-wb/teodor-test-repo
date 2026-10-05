package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "availability")
public class AvailabilityProperties {

  RecommendedSearchOption recommendedSearch;

  List<String> ratePlanClasses;

  List<String> employeeRatePlanClasses;

}
