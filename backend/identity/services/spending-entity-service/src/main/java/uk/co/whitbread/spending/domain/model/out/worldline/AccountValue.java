package uk.co.whitbread.spending.domain.model.out.worldline;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountValue {
  @JsonProperty("value")
  private BigDecimal value;

  @JsonProperty("currencyCode")
  private String currencyCode;
}
