package uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RestrictionsByDateRange {

    private List<RestrictionSets> restrictionSets;
    private String hotelId;
    private boolean hasMore;

}
