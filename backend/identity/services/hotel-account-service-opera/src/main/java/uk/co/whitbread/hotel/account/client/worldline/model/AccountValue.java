package uk.co.whitbread.hotel.account.client.worldline.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class AccountValue {
  @JsonProperty("value")
  private BigDecimal value;

  @JsonProperty("currencyCode")
  private String currencyCode;
}
