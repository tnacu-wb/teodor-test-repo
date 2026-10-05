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
public class Content {

  private Results results;
  private Filter filter;
  private String totalHotels;
  private Map map;
  private Global global;
  private AemSeo seo;
  private List<DynamicFilters> dynamicFilters;
}
