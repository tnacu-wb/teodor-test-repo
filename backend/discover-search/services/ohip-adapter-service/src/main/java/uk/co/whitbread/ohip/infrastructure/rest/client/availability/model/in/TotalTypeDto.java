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
public class TotalTypeDto {

  @JsonProperty("description")
  private String description;
  @JsonProperty("amountBeforeTax")
  private BigDecimal amountBeforeTax;
  @JsonProperty("amountAfterTax")
  private BigDecimal amountAfterTax;
  @JsonProperty("currencyCode")
  private String currencyCode;
  @JsonProperty("currencySymbol")
  private String currencySymbol;
  @JsonProperty("decimalPlaces")
  private Integer decimalPlaces;
  @JsonProperty("code")
  private String code;
  @JsonProperty("rateOverride")
  private Boolean rateOverride;
}

