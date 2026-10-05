package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RatePlanBasedOnRates {

    private DynamicBaseRate dynamicBaseRate;

}
