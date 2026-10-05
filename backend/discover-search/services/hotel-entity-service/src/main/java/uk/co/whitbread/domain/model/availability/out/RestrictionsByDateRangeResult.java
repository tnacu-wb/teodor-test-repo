package uk.co.whitbread.domain.model.availability.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestrictionsByDateRangeResult {
  private List<RestrictionSets> restrictionSets;
  private String hotelId;
  private boolean hasMore;
}
