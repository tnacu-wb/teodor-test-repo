package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction;

import lombok.*;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.Links;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RateRestrictionResponse {

    private RestrictionsByDateRangeParent restrictionsByDateRange;
    private List<Links> links;
}
