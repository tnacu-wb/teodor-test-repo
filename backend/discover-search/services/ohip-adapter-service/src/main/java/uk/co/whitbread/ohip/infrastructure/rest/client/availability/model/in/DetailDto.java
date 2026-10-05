package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

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
public class DetailDto {

  @JsonProperty("summaryDate")
  private String summaryDate;
  @JsonProperty("revenue")
  private BigDecimal revenue;
  @JsonProperty("package")
  private BigDecimal packageDetails;
  @JsonProperty("tax")
  private BigDecimal tax;
  @JsonProperty("gross")
  private BigDecimal gross;
  @JsonProperty("net")
  private BigDecimal net;
  @JsonProperty("ratePlanCode")
  private String ratePlanCode;
  @JsonProperty("currencyCode")
  private String currencyCode;
}
