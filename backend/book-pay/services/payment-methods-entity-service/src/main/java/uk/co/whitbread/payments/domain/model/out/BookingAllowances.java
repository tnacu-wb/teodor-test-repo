package uk.co.whitbread.payments.domain.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingAllowances {

  private boolean allowAlcohol;
  private boolean allowCarParking;
  private boolean allowAdditionalCosts;
  private boolean allowPremierSaverRates;
  private boolean allowIndividualCards;
  private long maxNumberOfNights;
  private PriceCapLocations maxDinnerBudgets;

}
