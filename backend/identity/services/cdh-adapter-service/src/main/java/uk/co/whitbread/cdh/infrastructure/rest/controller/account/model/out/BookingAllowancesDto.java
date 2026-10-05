package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingAllowancesDto {
  private boolean allowAdditionalCosts;
  private boolean allowAlcohol;
  private boolean allowCarParking;
  private boolean allowIndividualCards;
  private boolean allowPremierSaverRates;
  private List<String> extrasCodes;
  private MaxDinnerBudgetsDto maxDinnerBudgets;
  private Integer maxNumberOfNights;
  private List<String> upsellItemsAllowed;
}
