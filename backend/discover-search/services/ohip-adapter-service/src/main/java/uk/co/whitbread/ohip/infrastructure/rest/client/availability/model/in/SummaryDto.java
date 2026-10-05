package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryDto {

  @JsonProperty("details")
  private List<DetailDto> details;
  @JsonProperty("gross")
  private BigDecimal gross;
  @JsonProperty("net")
  private BigDecimal net;
  @JsonProperty("deposit")
  private BigDecimal deposit;
  @JsonProperty("totalCostOfStay")
  private BigDecimal totalCostOfStay;
  @JsonProperty("outStandingCostOfStay")
  private BigDecimal outStandingCostOfStay;
  @JsonProperty("guestPay")
  private BigDecimal guestPay;
  @JsonProperty("routing")
  private BigDecimal routing;
  @JsonProperty("currencyCode")
  private String currencyCode;
  @JsonProperty("start")
  private String start;
  @JsonProperty("end")
  private String end;
  @JsonProperty("hasSuppressedRate")
  private boolean hasSuppressedRate;
}
