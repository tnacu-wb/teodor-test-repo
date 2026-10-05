package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;

import lombok.Data;

@Data
public class BookingAllowancesDto {

  private boolean allowAlcohol;
  private boolean allowCarParking;
  private boolean allowAdditionalCosts;
  private boolean allowPremierSaverRates;
  private boolean allowIndividualCards;
  private long maxNumberOfNights;
  private PriceCapLocationsDto maxDinnerBudgets;

}
