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
public class ContentDto {

  private ResultsDto results;
  private FilterDto filter;
  private String totalHotels;
  private MapDto map;
  private SrpGlobalDto global;
  private SeoDto seo;
  private List<DynamicFiltersDto> dynamicFilters;
}