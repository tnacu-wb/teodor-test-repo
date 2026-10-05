package uk.co.whitbread.spending.domain.model.out.worldline;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentValue {
  @JsonProperty("value")
  private String value;

  @JsonProperty("currencyCode")
  private String currencyCode;
}
