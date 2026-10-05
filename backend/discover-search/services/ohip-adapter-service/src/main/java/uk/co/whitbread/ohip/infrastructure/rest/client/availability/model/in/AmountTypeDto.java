package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
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
public class AmountTypeDto {

  @JsonProperty("base")
  private TotalTypeDto base;
  @JsonProperty("shareRatePercentage")
  private BigDecimal shareRatePercentage;
  @JsonProperty("total")
  private TotalTypeDto total;
  @JsonProperty("effectiveRate")
  private TotalTypeDto effectiveRate;
  @JsonProperty("start")
  private String start;
  @JsonProperty("end")
  private String end;
}