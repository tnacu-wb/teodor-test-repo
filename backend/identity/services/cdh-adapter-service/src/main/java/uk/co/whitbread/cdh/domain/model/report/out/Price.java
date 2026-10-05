package uk.co.whitbread.cdh.domain.model.report.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Price {

  @JsonProperty("Currency")
  private String currency;

  @JsonProperty("Amount")
  private BigDecimal amount;

  @JsonProperty("NetAmount")
  private BigDecimal netAmount;

}
