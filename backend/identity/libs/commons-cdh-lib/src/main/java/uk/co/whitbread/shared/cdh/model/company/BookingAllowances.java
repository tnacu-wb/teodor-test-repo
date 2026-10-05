package uk.co.whitbread.shared.cdh.model.company;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
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
  private RateCaps maxDinnerBudgets;
  @JsonProperty("MaxNumberOfNights")
  private int maxNumberOfNights;
  @JsonProperty("UpsellItemsAllowed")
  private List<String> upsellItemsAllowed;
}
