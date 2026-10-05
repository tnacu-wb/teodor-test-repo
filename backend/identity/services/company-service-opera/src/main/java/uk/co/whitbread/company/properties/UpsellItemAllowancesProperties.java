package uk.co.whitbread.company.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class UpsellItemAllowancesProperties {
    @Value("${upsellItemsAllowances.premierBreakfast}")
    private String premierBreakfast;
    @Value("${upsellItemsAllowances.continentalBreakfast}")
    private String continentalBreakfast;
    @Value("${upsellItemsAllowances.mealDeal}")
    private String mealDeal;
    @Value("${upsellItemsAllowances.wiFi}")
    private String wiFi;
    @Value("${upsellItemsAllowances.hubBreakfast}")
    private String hubBreakfast;
}
