package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaxDinnerBudgets {
  @JsonProperty("GreaterLondon")
  private GreaterLondon greaterLondon;
  @JsonProperty("Ireland")
  private Ireland ireland;
  @JsonProperty("UkWide")
  private UkWide ukWide;
}
