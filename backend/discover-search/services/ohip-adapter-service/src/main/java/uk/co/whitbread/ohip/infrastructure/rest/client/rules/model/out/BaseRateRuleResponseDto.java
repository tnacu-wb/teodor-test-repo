package uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseRateRuleResponseDto {
  
  @JsonProperty("promoCode")
  private String promoCode;
  
  @JsonProperty("ratePlanCode")
  private String ratePlanCode;
  
  @JsonProperty("baseRate")
  private String baseRate;
}
