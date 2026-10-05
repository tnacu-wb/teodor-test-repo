package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAllowances {
  @JsonProperty("AllowAdditionalCosts")
  private boolean allowAdditionalCosts;
  @JsonProperty("AllowAlcohol")
  private boolean allowAlcohol;
  @JsonProperty("AllowCarParking")
  private boolean allowCarParking;
  @JsonProperty("AllowIndividualCards")
  private boolean allowIndividualCards;
  @JsonProperty("AllowPremierSaverRates")
  private boolean allowPremierSaverRates;
  @JsonProperty("ExtrasCodes")
  private List<String> extrasCodes;
  @JsonProperty("MaxDinnerBudgets")
  private MaxDinnerBudgets maxDinnerBudgets;
  @JsonProperty("MaxNumberOfNights")
  private Integer maxNumberOfNights;
  @JsonProperty("UpsellItemsAllowed")
  private List<String> upsellItemsAllowed;
}
