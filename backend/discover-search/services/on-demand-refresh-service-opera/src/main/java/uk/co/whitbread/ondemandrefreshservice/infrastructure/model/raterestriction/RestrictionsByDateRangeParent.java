package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RestrictionsByDateRangeParent {

    private RestrictionsByDateRange restrictionsByDateRange;

}
