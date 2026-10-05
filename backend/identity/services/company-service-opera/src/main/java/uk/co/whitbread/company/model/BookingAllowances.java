package uk.co.whitbread.company.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class BookingAllowances {
    @Valid
    private PriceCapLocations maxDinnerBudgets;
    
    @NotNull
    private List<@Range(min = 1, max = 5) String> extrasCodes;
    private List<String> upsellItemsAllowed;
    private boolean allowAlcohol;
    private boolean allowCarParking;
    private boolean allowAdditionalCosts;
    private boolean allowPremierSaverRates;
    private boolean allowIndividualCards;
    private long maxNumberOfNights;
}
