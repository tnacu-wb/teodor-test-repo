package uk.co.whitbread.content.domain.model.searchresults.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

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
