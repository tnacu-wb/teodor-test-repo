package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RateAmountBoundaries {

    private Minimum minimum;
    private Maximum maximum;

}
