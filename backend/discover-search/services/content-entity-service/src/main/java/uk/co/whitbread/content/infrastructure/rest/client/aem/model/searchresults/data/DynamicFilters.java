package uk.co.whitbread.content.infrastructure.rest.client.aem.model.searchresults.data;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFilters {

  private String groupTitle;
  private GroupOperator groupOperator;
  private List<GroupItems> groupItems;
}
