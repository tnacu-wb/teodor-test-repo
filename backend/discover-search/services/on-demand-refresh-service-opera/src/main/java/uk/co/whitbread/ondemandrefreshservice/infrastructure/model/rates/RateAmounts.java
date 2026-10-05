package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RateAmounts {

    private double onePersonRate;
    private double twoPersonRate;
    private double extraPersonRate;
    private double extraChildRate;
    private boolean overrideFloorAmount;

}
