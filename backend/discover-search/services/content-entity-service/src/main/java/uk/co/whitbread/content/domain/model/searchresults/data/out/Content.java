package uk.co.whitbread.content.domain.model.searchresults.data.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

  private Results results;
  private Filter filter;
  private String totalHotels;
  private Map map;
  private Global global;
  private Seo seo;
  private List<DynamicFilters> dynamicFilters;
}