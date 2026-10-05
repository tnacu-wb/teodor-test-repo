package uk.co.whitbread.ohip.domain.logic;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.co.whitbread.ohip.domain.model.availability.out.MealsIncluded;

@Data
@Component
@ConfigurationProperties(prefix = "meals")
public class MealsIncludedConfigProperties {

  private Map<String, MealsIncluded> config;

  public MealsIncluded getMealConfig(String ratePlanCode) {
    return config.getOrDefault(ratePlanCode, new MealsIncluded());
  }


}
