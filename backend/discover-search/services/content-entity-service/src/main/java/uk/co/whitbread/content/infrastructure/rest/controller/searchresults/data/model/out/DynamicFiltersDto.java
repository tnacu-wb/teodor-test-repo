package uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFiltersDto {

  private String groupTitle;
  private GroupOperatorDto groupOperator;
  private List<GroupItemsDto> groupItems;
}
