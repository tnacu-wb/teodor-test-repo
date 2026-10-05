package uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {

  private String price;
  private String distance;
  private String recommended;
  private String mapLong;
  private String listLong;
  private String filtersLong;
  private String map;
  private String list;
  private String filter;
}
